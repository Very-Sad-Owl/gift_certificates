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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView)
            throws Exception {

        if(request.getRequestURL().toString().contains("error")) return;

        ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper)request;

        boolean replicated = Boolean.parseBoolean(wrappedRequest.getParameter(REPLICATED_PARAM));
        if (replicated) return;

        String idParam = wrappedRequest.getParameter(ID_PARAM);
        boolean isRequestOnClusterizedEntity = idParam != null;
        String ports = wrappedRequest.getParameter(REDIRECTED_PARAM);
        if (ports == null || "[]".equals(ports)) return;
        List<String> replicateToAsString = Arrays.asList(ports.substring(1, ports.length() - 1).split(", "));
        List<Integer> replicas = replicateToAsString
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        String method = wrappedRequest.getMethod();
        if  (method.equals(HttpMethod.GET.name())) {
            return;
        }
        StringBuffer requestURL = wrappedRequest.getRequestURL();
        int portSavedInto = getPortFromUrl(requestURL.toString());

        if (method.equals(HttpMethod.POST.name()) || method.equals(HttpMethod.PUT.name())) {
            byte[] contentAsByteArray = wrappedRequest.getContentAsByteArray();
            Object entity = new ObjectMapper().readValue(contentAsByteArray, Object.class);
            if (method.equals(HttpMethod.POST.name())) {
                List<Object> saved = replicas.stream()
                        .map(node -> {
                            if (!isRequestOnClusterizedEntity) {
                                return restTemplate.postForObject(
                                        markUrlAsReplicated(
                                                changePort(requestURL, portSavedInto, node), request),
                                        entity, Object.class);
                            } else {
                                long id = Long.parseLong(idParam);
                                return restTemplate.postForObject(
                                        markUrlAsReplicated(
                                                changePort(requestURL, portSavedInto, node, id), request),
                                        entity, Object.class);
                            }
                        }
                        )
                        .collect(Collectors.toList());
            } else {
                replicas.forEach(node -> restTemplate
                        .put(markUrlAsReplicated(
                                changePort(requestURL, portSavedInto, node), request), entity));
            }
        } else if (method.equals(HttpMethod.DELETE.name())) {
            long id = Long.parseLong(idParam);
            replicas.forEach(node -> restTemplate
                    .delete(markUrlAsReplicated(
                            changePort(requestURL, portSavedInto, node, id), request)));
        }
    }
}
