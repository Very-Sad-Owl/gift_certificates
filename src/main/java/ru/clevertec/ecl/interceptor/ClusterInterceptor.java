package ru.clevertec.ecl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.exception.UndefinedException;
import ru.clevertec.ecl.interceptor.common.RequestEditor;
import ru.clevertec.ecl.util.health.HealthCheckerService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static ru.clevertec.ecl.interceptor.common.ClusterProperties.*;
import static ru.clevertec.ecl.interceptor.common.RequestParams.*;

/**
 * {@link HandlerInterceptor} implementation containing handling methods of clusterized entities requests'.
 *
 * Pre handles write/update/delete methods on {@link ru.clevertec.ecl.entity.baseentities.Order} entities from its controllers.
 *
 * @author Olga Mailychko
 *
 */
@Component
@EnableConfigurationProperties
@RequiredArgsConstructor
public class ClusterInterceptor implements HandlerInterceptor {

    private final ClusterProperties clusterProperties;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final HealthCheckerService healthCheckerService;
    private final ObjectMapper objectMapper;
    private static final String SEQ_CURR_PATTERN = "http://localhost:%s/orders/sequence/current";
    private static final String SEQ_NEXT_PATTERN = "http://localhost:%s/orders/sequence/next";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        ContentCachingRequestWrapper wrappedReq = (ContentCachingRequestWrapper)request;

        String method = wrappedReq.getMethod();
        StringBuffer requestURL = wrappedReq.getRequestURL();

        if (requestURL.toString().contains(SEQUENCE_PARAM)) return true;

        boolean redirected = Boolean.parseBoolean(wrappedReq.getParameter(REDIRECTED_PARAM));
        if (redirected) {
            return true;
        }

        List<Integer> availablePorts = healthCheckerService.checkAlive();
        if (method.equals(HttpMethod.GET.name())) {
            String idParam = wrappedReq.getParameter(ID_PARAM);
            if ("".equals(idParam)) {
                long id = RequestEditor.getIdFromRequest(wrappedReq);
                int portToRedirect = clusterProperties.definePortById(id);
                portToRedirect = healthCheckerService.findAnyAliveNodeFromReplicas(portToRedirect);
                if (clusterProperties.getSourcesPort().contains(portToRedirect)) {
                    return true;
                }
                requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
                OrderDto order = restTemplate.getForObject(requestURL.toString(), OrderDto.class);
                String orderJson = mapper.writeValueAsString(order);
                RequestEditor.setJsonResponse(orderJson, response);
                return false;
            }
        } else if (HttpMethod.POST.name().equals(method)) {
            OrderDto entity = jsonStringToOrderDto(wrappedReq);
            HttpEntity<OrderDto> data = createHttpEntity(entity);
            long id = getMaxSequenceValue(availablePorts) + 1;
            int portToRedirect = clusterProperties.definePortById(id);
            portToRedirect = healthCheckerService.findAnyAliveNodeFromReplicas(portToRedirect);
            if (clusterProperties.getPort() == portToRedirect) {
                return false;
            }
            requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
            OrderDto orderDTO = restTemplate.postForObject(requestURL.toString(), data, OrderDto.class);
            moveSequence(availablePorts, portToRedirect);
            String orderJson = mapper.writeValueAsString(orderDTO);
            RequestEditor.setJsonResponse(orderJson, response);
            return false;
        } else if (HttpMethod.DELETE.name().equals(method)) {
            long id = RequestEditor.getIdFromRequest(wrappedReq);
            int portToRedirect = clusterProperties.definePortById(id);
            portToRedirect = healthCheckerService.findAnyAliveNodeFromReplicas(portToRedirect);
            if (clusterProperties.getSourcesPort().contains(portToRedirect)) {
                return true;
            }
            requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
            restTemplate.delete(requestURL.toString());
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return false;
        } else if (HttpMethod.PUT.name().equals(method)) {
            OrderDto entity = jsonStringToOrderDto(wrappedReq);
            HttpEntity<OrderDto> data = createHttpEntity(entity);
            long id = RequestEditor.getIdFromRequest(wrappedReq);
            int portToRedirect = clusterProperties.definePortById(id);
            portToRedirect = healthCheckerService.findAnyAliveNodeFromReplicas(portToRedirect);
            if (clusterProperties.getSourcesPort().contains(portToRedirect)) {
                return true;
            }
            requestURL = changePort(requestURL, clusterProperties.getPort(), portToRedirect);
            restTemplate.put(requestURL.toString(), data);
            String orderJson = mapper.writeValueAsString(entity);
            RequestEditor.setJsonResponse(orderJson, response);
            return false;
        }
        return true;
    }

    /**
     * Sends request on moving sequence.
     *
     * @param ports port values to send request on
     * @param excludedPort port to exclude from sending list
     */
    private void moveSequence(List<Integer> ports, int excludedPort) {
        ports.stream()
                .filter(val -> val != excludedPort)
                .map(node -> CompletableFuture.supplyAsync(() ->
                        restTemplate.getForObject(String.format(SEQ_NEXT_PATTERN,  node), Object.class)
                ))
                .map(CompletableFuture::join);
    }

    /**
     * Get maximum value of orders entity storage's sequence.
     *
     * @param nodes nodes to send request on
     * @return max sequence value from all requested nodes
     */
    private long getMaxSequenceValue(List<Integer> nodes){
        return nodes.stream()
                .map(node -> CompletableFuture.supplyAsync(() ->
                        restTemplate.getForObject(buildUrlToCurrentSequenceValueRequest(node), Long.class)))
                .map(CompletableFuture::join)
                .max(Long::compareTo)
                .orElseThrow(UndefinedException::new);
    }

    /**
     * Forms URL for requesting current orders table's sequence value from given node.
     *
     * @param port node's port to send request on
     * @return request URL as {@link String}
     */
    private String buildUrlToCurrentSequenceValueRequest(int port) {
        return String.format(SEQ_CURR_PATTERN, port);
    }

    /**
     * Maps request's body JSON string to {@link OrderDto} object.
     *
     * @param request request with {@link org.springframework.core.annotation.Order} value as JSON string in its body
     * @return mapped {@link OrderDto} object
     */
    private OrderDto jsonStringToOrderDto(ContentCachingRequestWrapper request) throws IOException {
        String jsonBody = RequestEditor.getJsonBodyFromRequest(request);
        return objectMapper.readValue(jsonBody, OrderDto.class);
    }

    /**
     * Creates {@link HttpEntity} from given {@link OrderDto} object.
     *
     * @param entity {@link OrderDto} object to write as body
     * @return {@link HttpEntity} with given parameter as body
     */
    private HttpEntity<OrderDto> createHttpEntity(OrderDto entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(entity, headers);
    }
}
