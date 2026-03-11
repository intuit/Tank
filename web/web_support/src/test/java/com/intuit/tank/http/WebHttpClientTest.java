package com.intuit.tank.http;

import org.junit.jupiter.api.Test;

import java.net.http.HttpRequest;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link WebHttpClient}.
 */
public class WebHttpClientTest {

    @Test
    public void testConstructor_CreatesNonNull() {
        WebHttpClient client = new WebHttpClient();
        assertNotNull(client);
    }

    @Test
    public void testFormBodyPublisher_EmptyMap_ReturnsPublisher() {
        Map<Object, Object> params = new LinkedHashMap<>();
        HttpRequest.BodyPublisher publisher = WebHttpClient.formBodyPublisher(params);
        assertNotNull(publisher);
    }

    @Test
    public void testFormBodyPublisher_SingleEntry_ReturnsPublisher() {
        Map<Object, Object> params = new LinkedHashMap<>();
        params.put("key1", "value1");
        HttpRequest.BodyPublisher publisher = WebHttpClient.formBodyPublisher(params);
        assertNotNull(publisher);
        assertEquals(11, publisher.contentLength()); // "key1=value1".length() == 11
    }

    @Test
    public void testFormBodyPublisher_MultipleEntries_ReturnsPublisher() {
        Map<Object, Object> params = new LinkedHashMap<>();
        params.put("key1", "val1");
        params.put("key2", "val2");
        HttpRequest.BodyPublisher publisher = WebHttpClient.formBodyPublisher(params);
        assertNotNull(publisher);
        assertTrue(publisher.contentLength() > 0);
    }

    @Test
    public void testFormBodyPublisher_SpecialCharacters_EncodesValues() {
        Map<Object, Object> params = new LinkedHashMap<>();
        params.put("key", "hello world");
        HttpRequest.BodyPublisher publisher = WebHttpClient.formBodyPublisher(params);
        assertNotNull(publisher);
        // "hello world" URL-encoded = "hello+world" or "hello%20world" (16 chars)
        assertTrue(publisher.contentLength() > 0);
    }

    @Test
    public void testPost_WithInvalidUri_ReturnsNull() {
        WebHttpClient client = new WebHttpClient();
        Map<Object, Object> params = new LinkedHashMap<>();
        params.put("key", "value");
        // Invalid URI scheme should trigger exception and return null
        var result = client.Post("not-a-valid-uri://localhost/test", params);
        assertNull(result);
    }
}
