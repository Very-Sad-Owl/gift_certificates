package ru.clevertec.ecl.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.exception.UndefinedException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.interceptor.common.RequestEditor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles all RESTTemplate requests in order to change host in their URL.
 *
 * See also {@link ClientHttpRequestInterceptor}
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateInterceptor
        implements ClientHttpRequestInterceptor {

    private final ClusterProperties properties;
    private static final String HOST_PATTERN = "http://(?<host>.*):\\d*/.*";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (!properties.getHost().equals("localhost")) {
            return execution.execute(new MyHttpRequestWrapper(request), body);
        }
        else {
            return execution.execute(request, body);
        }
    }

    /**
     * Custom {@link HttpRequestWrapper} implementation to wrap incoming request.
     *
     * See also {@link ClientHttpRequestInterceptor}
     *
     * @author Olga Mailychko
     *
     */
    private class MyHttpRequestWrapper extends HttpRequestWrapper {

        public MyHttpRequestWrapper(HttpRequest request) {
            super(request);
        }

        @Override
        public URI getURI() {
            try {
                Pattern urlPattern = Pattern.compile(HOST_PATTERN);
                Matcher hostMatcher = urlPattern.matcher(super.getURI().toString());
                if (hostMatcher.matches()) {
                    int port = RequestEditor.getPortFromUrl(super.getURI().toString());
                    String host = hostMatcher.group("host");
                    String changedUrl = super.getURI().toString().replace(host, properties.getHost() + port);
                    log.info(super.getURI() + " has been changed to " + changedUrl);
                    return new URI(changedUrl);
                }
            } catch (URISyntaxException e) {
                throw new UndefinedException(e);
            }
            return super.getURI();
        }
    }
}