package ru.clevertec.ecl.interceptor;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.interceptor.common.RequestEditor;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WrapperFilter.class)
public class WrapperFilterTest {

    @Autowired
    private WrapperFilter filter;

    @SneakyThrows
    @Test
    public void wrapperFilterTest_request_sameWrappedRequest() {
        MockHttpServletRequest originalReq = new MockHttpServletRequest();
        originalReq.setParameter("param", "value");
        originalReq.setContent("body".getBytes());
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setParameter("param", "value");
        req.setContent(originalReq.getContentAsByteArray());
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertTrue(req.getParameter("param").equals(originalReq.getParameter("param"))
        && req.getContentAsByteArray() == originalReq.getContentAsByteArray());
    }
}
