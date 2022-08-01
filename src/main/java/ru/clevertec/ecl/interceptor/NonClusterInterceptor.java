package ru.clevertec.ecl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.interceptor.common.RequestEditor;
import ru.clevertec.ecl.util.health.HealthCheckerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.interceptor.common.ClusterProperties.changePort;
import static ru.clevertec.ecl.interceptor.common.RequestParams.*;

/**
 * {@link HandlerInterceptor} implementation containing handling methods of non-clusterized entities requests'.
 *
 * Pre handles write/update/delete methods on non-clusterized entities from its controllers.
 *
 * @author Olga Mailychko
 *
 */
@Component
@Slf4j
@EnableConfigurationProperties
@RequiredArgsConstructor
public class NonClusterInterceptor implements HandlerInterceptor {

    private final ClusterProperties clusterProperties;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final HealthCheckerService healthCheckerService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ContentCachingRequestWrapper wrappedReq = (ContentCachingRequestWrapper) request;

        String method = wrappedReq.getMethod();
        final StringBuffer requestURL = wrappedReq.getRequestURL();

        if (requestURL.toString().contains(SEQUENCE_PARAM)) return true;

        boolean redirected = Boolean.parseBoolean(wrappedReq.getParameter(REDIRECTED_PARAM));
        if (redirected) {
            return true;
        }

        List<Integer> available = healthCheckerService.checkAlive();

        if (HttpMethod.POST.name().equals(method)) {
            Object entity = jsonToObject(wrappedReq);
            HttpEntity<Object> data = createHttpEntity(entity);
            List<Object> saved = available.stream()
                    .map(port -> CompletableFuture.supplyAsync(() ->
                            restTemplate
                                    .postForObject(changePort
                                            (requestURL, clusterProperties.getPort(), port).toString(),
                                            data, Object.class))
                    )
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            String json = mapper.writeValueAsString(saved.get(0));
            RequestEditor.setJsonResponse(json, response);
            return false;
        } else if (HttpMethod.DELETE.name().equals(method)) {
            long id = RequestEditor.getIdFromRequest(wrappedReq);
            available.stream()
                    .map(port -> CompletableFuture.supplyAsync(() -> {
                                restTemplate.delete(
                                        changePort(requestURL, clusterProperties.getPort(), port, id).toString());
                                return 0;
                            }
                    )).map(CompletableFuture::join);
            response.setStatus(HttpStatus.OK.value());
            return false;
        } else if (HttpMethod.PUT.name().equals(method)) {
            Object entity = jsonToObject(wrappedReq);
            HttpEntity<Object> data = createHttpEntity(entity);
            available.stream()
                    .map(node -> CompletableFuture.supplyAsync(() -> {
                                restTemplate
                                        .put(changePort(requestURL, clusterProperties.getPort(), node).toString(), data);
                                return 0;
                            })
                    ).map(CompletableFuture::join);
            response.setStatus(HttpStatus.OK.value());
            String json = mapper.writeValueAsString(entity);
            RequestEditor.setJsonResponse(json, response);
            return false;
        }
        return true;
    }

    /**
     * Maps request's body JSON string to object.
     *
     * @param request request with any non-clusterizing entity value as JSON string in its body
     * @return mapped {@link Object} object
     */
    private Object jsonToObject(ContentCachingRequestWrapper request) throws IOException {
        String jsonBody = RequestEditor.getJsonBodyFromRequest(request);
        return new ObjectMapper().readValue(jsonBody, Object.class);
    }

    /**
     * Creates {@link HttpEntity} from given {@link Object} object.
     *
     * @param entity {@link Object} object to write as body
     * @return {@link HttpEntity} with given parameter as body
     */
    private HttpEntity<Object> createHttpEntity(Object entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(entity, headers);
    }
}
