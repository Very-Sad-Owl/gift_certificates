package ru.clevertec.ecl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.config.ClusterProperties;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.exception.ServerIsDownException;
import ru.clevertec.ecl.util.health.HealthChecker;
import ru.clevertec.ecl.util.health.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.config.ClusterProperties.*;

@Component
@EnableConfigurationProperties
@RequiredArgsConstructor
public class ClusterInterceptor implements HandlerInterceptor {

    private final ClusterProperties clusterProperties;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final HealthChecker healthChecker;
    private static final String SEQ_CURR_PATTERN = "http://localhost:%s/orders/seq/current";
    private static final String PAGE_REQUEST = "http://localhost:%s/orders/findAll?page=%s&size=%s&redirected=true";
    private static final String SEQ_NEXT_PATTERN = "http://localhost:%s/orders/seq/next";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ContentCachingRequestWrapper wrappedReq = (ContentCachingRequestWrapper)request;

        String method = wrappedReq.getMethod();
        StringBuffer requestURL = wrappedReq.getRequestURL();

        if (requestURL.toString().contains("seq")) return true;

        boolean redirected = Boolean.parseBoolean(wrappedReq.getParameter("redirected"));
        if (redirected) {
            return true;
        }

        if (method.equals(HttpMethod.GET.name())) {
            String idParam = wrappedReq.getParameter("id");
            String page = wrappedReq.getParameter("page");
            String size = wrappedReq.getParameter("size");
            if ("".equals(idParam)) {
                long id = RequestParser.getIdFromRequest(wrappedReq);
                int portToRedirect = clusterProperties.definePort(id);
                portToRedirect = getAvailableReplica(portToRedirect);
                if (clusterProperties.getSources_port().contains(portToRedirect)) {
                    return true;
                }
                requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
                OrderDto order = restTemplate.getForObject(requestURL.toString(), OrderDto.class);
                String orderJson = mapper.writeValueAsString(order);
                RequestParser.setResponseAsJson(orderJson, response);
                return false;
            }
        } else if (HttpMethod.POST.name().equals(method)) { //TODO
            OrderDto entity = jsonToDto(wrappedReq);
            HttpEntity<OrderDto> data = createHttpEntity(entity);
            List<Integer> availablePorts = getAnyAvailableFromEachNode();
            long id = getMaxSequence(availablePorts) + 1;
            int portToRedirect = clusterProperties.definePort(id);
            portToRedirect = getAvailableReplica(portToRedirect);
            if (clusterProperties.getPort() == portToRedirect) {
                return false;
            }
            requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
            OrderDto orderDTO = restTemplate.postForObject(requestURL.toString(), data, OrderDto.class);
            moveSequence(availablePorts, portToRedirect);
            String orderJson = mapper.writeValueAsString(orderDTO);
            RequestParser.setResponseAsJson(orderJson, response);
            return false;
        } else if (method.equals(HttpMethod.DELETE.name())) {
            long id = RequestParser.getIdFromRequest(wrappedReq);
            int portToRedirect = clusterProperties.definePort(id);
            portToRedirect = getAvailableReplica(portToRedirect);
            if (clusterProperties.getSources_port().contains(portToRedirect)) {
                return true;
            }
            requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
            restTemplate.delete(requestURL.toString());
            return false;
        } else if (method.equals(HttpMethod.PUT.name())) {
            OrderDto entity = jsonToDto(wrappedReq);
            HttpEntity<OrderDto> data = createHttpEntity(entity);
            long id = RequestParser.getIdFromRequest(wrappedReq);
            int portToRedirect = clusterProperties.definePort(id);
            portToRedirect = getAvailableReplica(portToRedirect);
            if (clusterProperties.getSources_port().contains(portToRedirect)) {
                return true;
            }
            requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
            restTemplate.put(requestURL.toString(), data);
            String orderJson = mapper.writeValueAsString(entity);
            RequestParser.setResponseAsJson(orderJson, response);
            return false;
        }
        return true;
    }

    private void moveSequence(List<Integer> ports, int exclude) {
        ports.stream()
                .filter(val -> val != exclude)
                .map(node -> CompletableFuture.supplyAsync(() ->
                        restTemplate.getForObject(String.format(SEQ_NEXT_PATTERN,  node), Object.class)
                ))
                .map(CompletableFuture::join);
    }

    private int getAvailableReplica(int portToRedirect) {
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

    private List<Integer> getAnyAvailableFromEachNode() {
        return clusterProperties.getCluster().keySet().stream()
                .map(node -> CompletableFuture.supplyAsync(() ->
                        getAvailableReplica(node)
                ))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }


    private long getMaxSequence(List<Integer> nodes){
        return nodes.stream()
                .map(node -> CompletableFuture.supplyAsync(() ->
                        restTemplate.getForObject(createCurrSeqUrl(node), Long.class)))
                .map(CompletableFuture::join)
                .max(Long::compareTo)
                .orElseThrow(ServerIsDownException::new);  //TODO: special exc
    }

    private String createCurrSeqUrl(int port) {
        return String.format(SEQ_CURR_PATTERN, port);
    }

    private OrderDto jsonToDto(ContentCachingRequestWrapper wrapper) throws IOException {
        String jsonBody = RequestParser.getJsonBodyFromRequest(wrapper);
        return new ObjectMapper().readValue(jsonBody, OrderDto.class); //TODO: as field
    }

    private HttpEntity<OrderDto> createHttpEntity(OrderDto entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(entity, headers);
    }
}
