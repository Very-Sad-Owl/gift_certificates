package ru.clevertec.ecl.interceptor.common;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.exception.InvalidIdException;
import ru.clevertec.ecl.exception.UndefinedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.clevertec.ecl.interceptor.common.RequestParams.REDIRECTED_PARAM;
import static ru.clevertec.ecl.interceptor.common.RequestParams.REPLICATED_PARAM;

/**
 * Contains methods performing editing operations on user's requests and responses.
 *
 * @author Olga Mailychko
 *
 */
public class RequestEditor {

    private static final String FIRST_PARAMETER_WITH_VALUE_PATTERN = "?%s=%s";
    private static final String ANOTHER_PARAMETER_WITH_VALUE_PATTERN = "&%s=%s";
    private static final String PORT_GROUP_NAME = "port";
    private static final String PORT_RETRIEVER_PATTERN_STRING = "(.*:)(?<port>\\d*)(/.*)";

    /**
     * Parses id parameter from given request
     *
     * @param request request with desired id parameter
     * @return id parsed from request
     * @throws InvalidIdException when there is no any id parameter or its value is invalid
     */
    public static long getIdFromRequest(ContentCachingRequestWrapper request) {
        try {
            return Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            throw new InvalidIdException();
        }
    }

    /**
     * Retrieves body from request
     *
     * @param request request with body to retrieve
     * @return request's body as {@link String}
     * @throws IOException if body cannot be read
     */
    public static String getJsonBodyFromRequest(ContentCachingRequestWrapper request) throws IOException {
        return StreamUtils.copyToString(request.getInputStream(), Charset.defaultCharset());
    }

    /**
     * Writes given string as json body of given response.
     *
     * @param json JSON String to be written as body
     * @param response response to write given JSON string as body
     * @throws IOException if body cannot be written
     */
    public static void setJsonResponse(String json, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    /**
     * Adds "replicated" mark as parameter to given request URL.
     *
     * @param url URL to be marked
     * @param request origin request
     * @return result URL as {@link String}
     */
    public static String markUrlAsReplicated(StringBuffer url, HttpServletRequest request) {
        if (request.getParameter(REPLICATED_PARAM) != null) {
            return url.toString();
        }
        if (request.getParameter(REDIRECTED_PARAM) != null) {
            return url.append(String.format(ANOTHER_PARAMETER_WITH_VALUE_PATTERN, REPLICATED_PARAM, "true")).toString();
        } else {
            return url.append(String.format(FIRST_PARAMETER_WITH_VALUE_PATTERN, REPLICATED_PARAM, "true")).toString();
        }
    }

    /**
     * Adds "redirected" mark as parameter to given request URL.
     *
     * @param url URL to be marked
     * @return result URL as {@link String}
     */
    public static String markUrlAsRedirected(StringBuffer url) {
        if (url.toString().contains(REDIRECTED_PARAM)) {
            return url.toString();
        }
        if (url.toString().contains("?")) {
            return url.append(String.format(ANOTHER_PARAMETER_WITH_VALUE_PATTERN, REDIRECTED_PARAM, "true")).toString();
        } else {
            return url.append(String.format(FIRST_PARAMETER_WITH_VALUE_PATTERN, REDIRECTED_PARAM, "true")).toString();
        }
    }

    /**
     * Parses port value from given request URL
     *
     * @param url request's URL
     * @return port value retrieved from given URL
     * @throws UndefinedException if any parsing error occured
     */
    public static int getPortFromUrl(String url) {
        Pattern portRetrieverPattern = Pattern.compile(PORT_RETRIEVER_PATTERN_STRING);
        Matcher portMatcher = portRetrieverPattern.matcher(url);
        if (portMatcher.matches()) {
            String port = portMatcher.group(PORT_GROUP_NAME);
            return Integer.parseInt(port);
        }
        throw new UndefinedException();
    }
}
