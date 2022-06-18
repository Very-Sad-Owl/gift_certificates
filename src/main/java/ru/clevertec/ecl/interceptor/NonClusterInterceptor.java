package ru.clevertec.ecl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.config.ClusterProperties;
import ru.clevertec.ecl.dto.*;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.exception.ServerIsDownException;
import ru.clevertec.ecl.util.health.HealthChecker;
import ru.clevertec.ecl.util.health.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.config.ClusterProperties.changePort;

@Component
@EnableConfigurationProperties
@RequiredArgsConstructor
public class NonClusterInterceptor implements HandlerInterceptor {

    private final ClusterProperties clusterProperties;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final HealthChecker healthChecker;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ContentCachingRequestWrapper wrappedReq = (ContentCachingRequestWrapper)request;

        String method = wrappedReq.getMethod();
        final StringBuffer requestURL = wrappedReq.getRequestURL();

        if (requestURL.toString().contains("seq")) return true;

        boolean redirected = Boolean.parseBoolean(wrappedReq.getParameter("redirected"));
        if (redirected) {
            return true;
        }

        List<Integer> availablePorts = clusterProperties.getCluster().keySet().stream()
                .map(node -> CompletableFuture.supplyAsync(() ->
                        getAvailablePort(node)
                ))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        if (method.equals(HttpMethod.POST.name())) {
            Object entity = jsonToObject(wrappedReq);
            HttpEntity<Object> data = createHttpEntity(entity);
            List<Object> collected = availablePorts.stream()
                    .map(node -> CompletableFuture.supplyAsync(() ->
                            restTemplate.postForObject(changePort(requestURL, clusterProperties.getPort(), node).toString(), data, Object.class)
                    ))
                    .map(CompletableFuture::join).collect(Collectors.toList());

            String json = mapper.writeValueAsString(collected.get(0));
            RequestParser.setResponseAsJson(json, response);
            return false;
        } else if (method.equals(HttpMethod.DELETE.name())) {
            availablePorts
                    .forEach(node ->
                            restTemplate.delete(
                                    changePort(requestURL, clusterProperties.getPort(), node).toString()
                            )
                    );
            return false;
        } else if (method.equals(HttpMethod.PUT.name())) {
            Object entity = jsonToObject(wrappedReq);
            HttpEntity<Object> data = createHttpEntity(entity);
            availablePorts
                    .forEach(node ->
                            restTemplate.put(requestURL.toString(), data)
                    );

            String json = mapper.writeValueAsString(entity);
            RequestParser.setResponseAsJson(json, response);
            return false;
        }
        return true;
    }

    private int getAvailablePort(int portToRedirect) {
        Map<Integer, Status> nodeStatuses = healthChecker.healthCheckEndpoint(portToRedirect);
        if (!nodeStatuses.get(portToRedirect).isOk()) {
            portToRedirect = nodeStatuses.values().stream()
                    .filter(Status::isOk)
                    .findFirst()
                    .map(Status::getPort)
                    .orElseThrow(ServerIsDownException::new);
        }
        return portToRedirect;
    }

    private Object jsonToObject(ContentCachingRequestWrapper wrapper) throws IOException {
        String jsonBody = RequestParser.getJsonBodyFromRequest(wrapper);
        return new ObjectMapper().readValue(jsonBody, Object.class);
    }

    private HttpEntity<Object> createHttpEntity(Object entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(entity, headers);
    }
}
