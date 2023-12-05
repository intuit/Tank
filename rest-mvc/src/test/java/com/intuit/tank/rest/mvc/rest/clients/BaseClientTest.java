package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class BaseClientTest {

    MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testConstructor_WithProxy_SetsCorrectBaseUrlAndCreatesClient() {

        BaseClient baseClient = new BaseClient("http://localhost:8080", "proxy.test.com", 8080) {
            @Override
            protected String getServiceBaseUrl() {
                return "/v2/data";
            }
        };
        assertEquals("http://localhost:8080/v2/data", baseClient.baseUrl);
        assertNotNull(baseClient.client);
    }

    @Test
    void testConstructor_WithoutProxy_SetsCorrectBaseUrlAndCreatesClient() {
        BaseClient baseClient = new BaseClient("http://localhost:8080", null, null) {
            @Override
            protected String getServiceBaseUrl() {
                return "/v2/ping";
            }
        };
        assertEquals("http://localhost:8080/v2/ping", baseClient.baseUrl);
        assertNotNull(baseClient.client);
    }

    @Test
    void testSettingBaseUrl_ChangesBaseUrl() {
        BaseClient baseClient = new BaseClient("http://localhost:8080", null, null) {
            @Override
            protected String getServiceBaseUrl() {
                return "/v2/ping";
            }
        };
        baseClient.setBaseUrl("https://test-instance.com");
        assertEquals("https://test-instance.com/v2/ping", baseClient.baseUrl);
    }


    @Test
    void testPing_Success_ReturnsOk() throws InterruptedException {
        String serviceUrl = mockWebServer.url("/").toString();
        BaseClient baseClient = new BaseClient(serviceUrl, null, null) {
            @Override
            protected String getServiceBaseUrl() {
                return "/";
            }
        };
        mockWebServer.enqueue(new MockResponse().setBody("OK"));
        baseClient.ping();
        assertEquals("GET /ping HTTP/1.1", mockWebServer.takeRequest().getRequestLine());
    }


    @Test
    void testPing_Failure_ThrowsClientException() {
        String serviceUrl = mockWebServer.url("/").toString();
        BaseClient baseClient = new BaseClient(serviceUrl, null, null) {
            @Override
            protected String getServiceBaseUrl() {
                return "/";
            }
        };
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Interval Service Error"));
        Exception exception = assertThrows(ClientException.class, baseClient::ping);
        assertTrue(exception.getMessage().contains("Interval Service Error"));
    }
}

