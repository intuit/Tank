package com.intuit.tank.http.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PlainTextRequestTest {

    private PlainTextRequest request;

    @BeforeEach
    public void init() {
        request = new PlainTextRequest(null, null);
    }

    @Test
    public void testPlainTextRequest() {
        assertEquals("", request.getBody());
        assertNull(request.getKey(""));
        request.setKey("testKey", "testValue");
        request.setNamespace("testNameKey", "testNameValue");
        assertEquals("", request.getBody());
    }
}
