//package ru.clevertec.ecl.interceptor;
//
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.http.client.support.HttpRequestWrapper;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@Component
//public class UrlOverriderInterceptor implements ClientHttpRequestInterceptor {
//
//    @Override
//    public ClientHttpResponse intercept(HttpRequest request,
//                                        byte[] body,
//                                        ClientHttpRequestExecution execution)
//            throws IOException {
//        URI uri = request.getURI();
//        HttpMethod method = request.getMethod();
//
//        return execution.execute(new MyHttpRequestWrapper(request), body);
//
//    }
//
//    private class MyHttpRequestWrapper extends HttpRequestWrapper {
//        public MyHttpRequestWrapper(HttpRequest request) {
//            super(request);
//        }
//
//        @Override
//        public HttpMethod getMethod() {
//            return super.getMethod();
//        }
//
//        @Override
//        public URI getURI() {
//            try {
//                return new URI(super.getURI().toString());
//            } catch (URISyntaxException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}
