package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.netty.http.client.HttpClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InterruptedIOException;

class BaseClientTest {

    @Mock
    HttpClient httpClient;

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
    void testConstructorWithProxy() {

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
    void testConstructorWithoutProxy() {
        BaseClient baseClient = new BaseClient("http://localhost:8080", null, null) {
            @Override
            protected String getServiceBaseUrl() {
                return "/v2/data";
            }
        };
        assertEquals("http://localhost:8080/v2/data", baseClient.baseUrl);
        assertNotNull(baseClient.client);
    }

    @Test
    void testSetBaseUrl() {
        BaseClient baseClient = new BaseClient("http://localhost:8080", null, null) {
            @Override
            protected String getServiceBaseUrl() {
                return "/v2/data";
            }
        };
        baseClient.setBaseUrl("https://prod-instance.com");
        assertEquals("https://prod-instance.com/v2/data", baseClient.baseUrl);
    }


    @Test
    void testPingSuccess() throws InterruptedException {
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
    void testPingFailure() {
        String serviceUrl = mockWebServer.url("/").toString();
        BaseClient baseClient = new BaseClient(serviceUrl, null, null) {
            @Override
            protected String getServiceBaseUrl() {
                return "/";
            }
        };
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("error message from service"));
        assertThrows(ClientException.class, baseClient::ping);
    }
}

