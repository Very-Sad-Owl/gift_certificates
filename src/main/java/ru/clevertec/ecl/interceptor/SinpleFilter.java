//package ru.clevertec.ecl.interceptor;
//
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StreamUtils;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Order(1)
//@Component
//public class SinpleFilter implements Filter {
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        String path = ((HttpServletRequest)request).getRequestURL().toString();
//        String method = ((HttpServletRequest) request).getMethod();
//        if (path.contains("8080")) {
////            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
////            byte[] body = StreamUtils.copyToByteArray(wrappedRequest.getInputStream());
////            response.getWriter().write(new String(body));
//            path = path.replaceAll("8080", "8081");
//            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
//            ((HttpServletResponse)response).addHeader("Location", path);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            ((HttpServletResponse)response).sendRedirect(path);
////            chain.doFilter(request,response);
//            return;
//        }
////        chain.doFilter(request, response);
//        return;
//    }
//}
