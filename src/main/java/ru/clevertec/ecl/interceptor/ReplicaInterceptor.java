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
import ru.clevertec.ecl.config.ClusterProperties;
import ru.clevertec.ecl.util.health.HealthChecker;
import ru.clevertec.ecl.util.health.Status;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.config.ClusterProperties.changePort;

@Component
@EnableConfigurationProperties
@RequiredArgsConstructor
public class ReplicaInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;
    private final HealthChecker healthChecker;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper)request;

        boolean replicated = Boolean.parseBoolean(wrappedRequest.getParameter("replicated"));
        if (replicated) return;

        String method = wrappedRequest.getMethod();
        if  (method.equals(HttpMethod.GET.name())) {
            return;
        }
        StringBuffer requestURL = wrappedRequest.getRequestURL();
        int portSavedInto = getPortFromUrl(requestURL.toString());
        List<Status> replicas = healthChecker.healthCheckEndpoint(portSavedInto).values().stream()
                .filter(Status::isOk)
                .filter(val -> val.getPort() != portSavedInto)
                .collect(Collectors.toList());

        if (method.equals(HttpMethod.POST.name()) || method.equals(HttpMethod.PUT.name())) {
            byte[] contentAsByteArray = wrappedRequest.getContentAsByteArray();
            Object entity = new ObjectMapper().readValue(contentAsByteArray, Object.class);
            if (method.equals(HttpMethod.POST.name())) {
                replicas.forEach(node -> restTemplate.postForObject(
                        markAsReplicated(changePort(requestURL, portSavedInto, node.getPort()), request),
                        entity, Object.class));
            } else {
                replicas.forEach(node -> restTemplate.put(changePort(requestURL, portSavedInto, node.getPort()).toString(), entity));
            }
        } else if (method.equals(HttpMethod.DELETE.name())) {
            replicas.forEach(node -> restTemplate.delete(changePort(requestURL, portSavedInto, node.getPort()).toString()));
        }
    }

    private int getPortFromUrl(String url) {
        Pattern pattern = Pattern.compile("(.*:)(?<port>\\d*)(/.*)"); //TODO:
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            String port = matcher.group("port");
            return Integer.parseInt(matcher.group("port"));
        }
        return 0; //TODO: may be error url
    }

    private String markAsReplicated(StringBuffer url, HttpServletRequest req) {
        if (req.getParameter("redirected") != null) {
            return url.append("&replicated=true").toString();
        } else {
            return url.append("?replicated=true").toString();
        }
    }

}
