package ru.clevertec.ecl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.util.health.HealthCheckerService;
import ru.clevertec.ecl.util.health.Status;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.interceptor.common.ClusterProperties.changePort;
import static ru.clevertec.ecl.interceptor.common.RequestEditor.*;
import static ru.clevertec.ecl.interceptor.common.RequestParams.*;

/**
 * {@link HandlerInterceptor} implementation containing handling methods of all controllers' requests.
 *
 * Post handles write/update/delete methods on all entities from its controllers.
 *
 * @author Olga Mailychko
 *
 */
@Component
@EnableConfigurationProperties
@RequiredArgsConstructor
public class ReplicaInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;
    private final HealthCheckerService healthCheckerService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView)
            throws Exception {

        if(request.getRequestURL().toString().contains("error")) return;

        ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper)request;

        boolean replicated = Boolean.parseBoolean(wrappedRequest.getParameter(REPLICATED_PARAM));
        if (replicated) return;

        String method = wrappedRequest.getMethod();
        if  (method.equals(HttpMethod.GET.name())) {
            return;
        }
        StringBuffer requestURL = wrappedRequest.getRequestURL();
        int portSavedInto = getPortFromUrl(requestURL.toString());
        List<Status> replicas = healthCheckerService.healthCheckEndpoint(portSavedInto).values().stream()
                .filter(Status::isOk)
                .filter(val -> val.getPort() != portSavedInto)
                .collect(Collectors.toList());

        if (method.equals(HttpMethod.POST.name()) || method.equals(HttpMethod.PUT.name())) {
            byte[] contentAsByteArray = wrappedRequest.getContentAsByteArray();
            Object entity = new ObjectMapper().readValue(contentAsByteArray, Object.class);
            if (method.equals(HttpMethod.POST.name())) {
                replicas.stream()
                        .map(node -> CompletableFuture.supplyAsync(() ->
                                restTemplate.postForObject(
                                        markUrlAsReplicated(
                                                changePort(requestURL, portSavedInto, node.getPort()), request),
                                        entity, Object.class)))
                        .map(CompletableFuture::join);
            } else {
                replicas.forEach(node -> restTemplate
                        .put(changePort(requestURL, portSavedInto, node.getPort()).toString(), entity));
            }
        } else if (method.equals(HttpMethod.DELETE.name())) {
            replicas.forEach(node -> restTemplate
                    .delete(changePort(requestURL, portSavedInto, node.getPort()).toString()));
        }
    }
}
