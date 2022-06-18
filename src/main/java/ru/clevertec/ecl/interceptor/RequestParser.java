package ru.clevertec.ecl.interceptor;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
//TODO: another  package
public class RequestParser {

    public static long getIdFromRequest(ContentCachingRequestWrapper wrappedReq) {
        return Long.parseLong(wrappedReq.getParameter("id"));
    }

    public static String getJsonBodyFromRequest(ContentCachingRequestWrapper wrappedReq) throws IOException {
        return StreamUtils.copyToString(wrappedReq.getInputStream(), Charset.defaultCharset());
    }

    public static void setResponseAsJson(String json, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
