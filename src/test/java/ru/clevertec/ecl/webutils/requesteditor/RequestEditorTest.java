package ru.clevertec.ecl.webutils.requesteditor;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.exception.InvalidIdException;
import ru.clevertec.ecl.exception.UndefinedException;
import ru.clevertec.ecl.interceptor.common.RequestEditor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RequestEditor.class)
public class RequestEditorTest {

    MockHttpServletRequest request = new MockHttpServletRequest();

    @BeforeEach
    public void cleanRequest() {
        request.removeAllParameters();
    }

    @Test
    public void getIdFromRequestTest_requestStringWithId_id() {
        long expected = 2;
        request.setParameter("id", expected + "");

        long actual = RequestEditor.getIdFromRequest(new ContentCachingRequestWrapper(request));

        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromRequestTest_requestStringWithInvalidId_invalidIdException() {
        request.setParameter("id", "xd");

        InvalidIdException thrown = assertThrows(InvalidIdException.class, () -> {
            RequestEditor.getIdFromRequest(new ContentCachingRequestWrapper(request));
        });

        assertNotNull(thrown);
    }

    @Test
    public void getIdFromRequestTest_requestStringWithoutId_invalidIdException() {
        InvalidIdException thrown = assertThrows(InvalidIdException.class, () -> {
            RequestEditor.getIdFromRequest(new ContentCachingRequestWrapper(request));
        });

        assertNotNull(thrown);
    }

    @SneakyThrows
    @Test
    public void getJsonBodyFromRequest_someJson_equals() {
        String expected = "{\n" +
                "    \"userId\": 1,\n" +
                "    \"certificateId\": 1\n" +
                "}";
        request.setContent(expected.getBytes());

        String actual = RequestEditor.getJsonBodyFromRequest(new ContentCachingRequestWrapper(request));

        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    public void getJsonBodyFromRequest_emptyJson_emptyString() {
        String expected = "";
        request.setContent(expected.getBytes());

        String actual = RequestEditor.getJsonBodyFromRequest(new ContentCachingRequestWrapper(request));

        assertEquals(expected, actual);
    }

    @Test
    public void markUrlAsRedirectedTest_notYetRedirectedWithParameters_markAsRedirected() {
        StringBuffer url = new StringBuffer("localhost:8080/tags/find?id=1");

        String expected = "localhost:8080/tags/find?id=1&redirected=true";

        String actual = RequestEditor.markUrlAsRedirected(url);

        assertEquals(expected, actual);
    }

    @Test
    public void markUrlAsRedirectedTest_notYetRedirectedWithoutParameters_markAsRedirected() {
        StringBuffer url = new StringBuffer("localhost:8080/tags/delete");

        String expected = "localhost:8080/tags/delete?redirected=true";

        String actual = RequestEditor.markUrlAsRedirected(url);

        assertEquals(expected, actual);
    }

    @Test
    public void markUrlAsRedirectedTest_alreadyRedirected_sameUrl() {
        String expected = "localhost:8080/tags/delete?redirected=true";

        StringBuffer url = new StringBuffer(expected);

        String actual = RequestEditor.markUrlAsRedirected(url);

        assertEquals(expected, actual);
    }

    @Test
    public void markAsReplicatedTest_redirectedBefore_addReplicatedFlagAsNewParameter() {
        StringBuffer url = new StringBuffer("localhost:8080/tags/delete?redirected=true");
        request.setParameter("redirected", "true");

        String expected = "localhost:8080/tags/delete?redirected=true&replicated=true";

        String actual = RequestEditor.markUrlAsReplicated(url, request);

        assertEquals(expected, actual);
    }

    @Test
    public void markAsReplicatedTest_notRedirectedBefore_addReplicatedFlagAsNewParameter() {
        StringBuffer url = new StringBuffer("localhost:8080/tags/delete");

        String expected = "localhost:8080/tags/delete?replicated=true";

        String actual = RequestEditor.markUrlAsReplicated(url, request);

        assertEquals(expected, actual);
    }

    @Test
    public void markAsReplicatedTest_replicatedBefore_sameUrl() {
        String expected = "localhost:8080/tags/delete?replicated=true";

        StringBuffer url = new StringBuffer(expected);
        request.setParameter("replicated", "true");

        String actual = RequestEditor.markUrlAsReplicated(url, request);

        assertEquals(expected, actual);
    }

    @Test
    public void getPortFromUrl_urlWith8080Port_8080() {
        int expected = 8080;
        String url = String.format("localhost:%s/tags", expected);

        int actual = RequestEditor.getPortFromUrl(url);

        assertEquals(expected, actual);
    }

    @Test
    public void getPortFromUrl_urlWithoutPort_undefinedException() {
        String url = "localhost:xdxd/tags";

        UndefinedException thrown = assertThrows(UndefinedException.class, () -> {
            RequestEditor.getPortFromUrl(url);
        });

        assertNotNull(thrown);
    }
}
