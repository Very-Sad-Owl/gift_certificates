package ru.clevertec.ecl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@EnableConfigurationProperties
public class ClusterInterceptor implements HandlerInterceptor {

    @Value("#{${cluster}}")
    private Map<String, Integer> cluster;
    @Value("${server.port}")
    private int currentPort;
    private static final String FIRST_PARAM_PATTERN = "?%s=%s";
    private static final String PARAM_PATTERN = "&%s=%s";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());
        System.out.println(handler);
        String method = request.getMethod();
        cluster.remove("node"+currentPort);

        StringBuffer requestURL = request.getRequestURL();

        if (method.equals(HttpMethod.PUT.name())
                || method.equals(HttpMethod.PATCH.name())) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            byte[] body = StreamUtils.copyToByteArray(wrappedRequest.getInputStream());
            Map<String, Object> jsonRequest = new ObjectMapper().readValue(body, Map.class);
            int id = (Integer)jsonRequest.get("id");
            int portToRedirect = definePort(id);
            changePort(requestURL, currentPort, portToRedirect);
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", requestURL.toString());
            response.sendRedirect(requestURL.toString());
        } else if (method.equals(HttpMethod.POST.name())) {

        } else if (method.equals(HttpMethod.GET.name())) {
            int id = Integer.parseInt(request.getParameter("id"));
            int portToRedirect = definePort(id);
            rebuildRequestUrl(request.getParameterMap(), requestURL);
            changePort(requestURL, currentPort, portToRedirect);
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", requestURL.toString());
            response.sendRedirect(requestURL.toString());
            return false;
        } else {
            return true;
        }


//            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
//                String key = entry.getKey();
//                String[] values = entry.getValue();
//                map.add(key, values[0]);
//            }
//
//            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
//            byte[] body = StreamUtils.copyToByteArray(wrappedRequest.getInputStream());
//
//            Map<String, Object> jsonRequest = new ObjectMapper().readValue(body, Map.class);
//
//            System.out.println(jsonRequest);
//            response.getWriter().write(new String(body));
//
////            requestURL = requestURL + "?id=" + Long.parseLong(map.getFirst("id"));
        return true;
    }

    private int definePort(int destination) {
        if (destination % 3 == 0) {
            return cluster.get(PortAvailable.NODE_FIRST.getPortName());
        } else if (destination % 3 == 3) {
            return cluster.get(PortAvailable.NODE_SECOND.getPortName());
        } else {
            return cluster.get(PortAvailable.NODE_THIRD.getPortName());
        }
    }

    private void rebuildRequestUrl(Map<String, String[]> params, StringBuffer req) {
        boolean isFirstParam = true;
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (isFirstParam) {
                req.append(String.format(FIRST_PARAM_PATTERN, key, values[0]));
            } else {
                req.append(String.format(PARAM_PATTERN, key, values[0]));
            }
            isFirstParam = false;
        }
    }

    private void changePort(StringBuffer req, int replaced, int replacer) {
        req = new StringBuffer(req.toString().replaceAll(replaced+"", replacer+""));
    }
}
