package com.intuit.tank.rest.mvc.rest.clients.util;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RestUrlBuilderTest {

    @Test
    public void testBaseUrlWithMethodName() {
        RestUrlBuilder builder = new RestUrlBuilder("http://localhost:8080");
        String actualUrl = builder.buildUrl("v2/ping");
        String expectedUrl = "http://localhost:8080/v2/ping";
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testBaseUrlWithMethodNameAndParameter() {
        RestUrlBuilder builder = new RestUrlBuilder("http://localhost:8080");
        String actualUrl = builder.buildUrl("v2/data", "status");
        String expectedUrl = "http://localhost:8080/v2/data/status";
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testBaseUrlWithNullMethodName() {
        RestUrlBuilder builder = new RestUrlBuilder("http://localhost:8080");
        String actualUrl = builder.buildUrl(null);
        String expectedUrl = "http://localhost:8080";
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testBaseUrlWithMethodNameAndNullParameter() {
        RestUrlBuilder builder = new RestUrlBuilder("http://localhost:8080");
        String actualUrl = builder.buildUrl("v2/data", (Object[]) null);
        String expectedUrl = "http://localhost:8080/v2/data";
        assertEquals(expectedUrl, actualUrl);
    }
}

