package com.intuit.tank.rest.mvc.rest.clients.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RestUrlBuilderTest {

    RestUrlBuilder builder;

    @BeforeEach
    public void setUp() {
        builder = new RestUrlBuilder("http://localhost:8080");
    }

    @Test
    public void testBuildUrl_WithMethodName_ReturnsCorrectUrl() {
        String actualUrl = builder.buildUrl("v2/ping");
        assertEquals("http://localhost:8080/v2/ping", actualUrl);
    }

    @Test
    public void testBuildUrl_WithMethodNameAndParameter_ReturnsCorrectUrl() {
        String actualUrl = builder.buildUrl("v2/data", "status");
        assertEquals("http://localhost:8080/v2/data/status", actualUrl);
    }

    @Test
    public void testBuildUrl_WithNullMethodName_ReturnsBaseUrl() {
        String actualUrl = builder.buildUrl(null);
        assertEquals("http://localhost:8080", actualUrl);
    }

    @Test
    public void testBuildUrl_WithMethodNameAndNullParameter_ReturnsUrlWithoutParameter() {
        String actualUrl = builder.buildUrl("v2/data", (Object[]) null);
        assertEquals("http://localhost:8080/v2/data", actualUrl);
    }

    @Test
    public void testBuildUrl_WithMethodNameAndEmptyParameter_IgnoreEmptyParameter() {
        String actualUrl = builder.buildUrl("v2/data", "");
        assertEquals("http://localhost:8080/v2/data/", actualUrl);
    }

    @Test
    public void testBuildUrl_WithEmptyMethodName_ReturnsBaseUrl() {
        String actualUrl = builder.buildUrl("");
        assertEquals("http://localhost:8080/", actualUrl);
    }
}

