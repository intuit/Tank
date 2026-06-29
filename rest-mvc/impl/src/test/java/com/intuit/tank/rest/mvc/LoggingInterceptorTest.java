package com.intuit.tank.rest.mvc;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LoggingInterceptorTest {

    @Test
    void preHandle_returnsTrue() throws Exception {
        LoggingInterceptor interceptor = new LoggingInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        boolean result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    void afterCompletion_clearsThreadContext() throws Exception {
        LoggingInterceptor interceptor = new LoggingInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Should not throw
        assertDoesNotThrow(() -> interceptor.afterCompletion(request, response, new Object(), null));
    }

    @Test
    void afterCompletion_handlesExceptionParameter() throws Exception {
        LoggingInterceptor interceptor = new LoggingInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        assertDoesNotThrow(() -> interceptor.afterCompletion(request, response, new Object(), new Exception("test")));
    }
}
