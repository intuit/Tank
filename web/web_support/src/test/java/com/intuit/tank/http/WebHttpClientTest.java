package com.intuit.tank.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WebHttpClient
 *
 * @author Shawn Park
 */
public class WebHttpClientTest {
    @Mock
    private HttpClient _httpClientMock;
    @Mock
    private HttpResponse<String> _httpResponseMock;

    private Map<Object, Object> _requestParametersStub;

    private final String BODY_RESPONSE_STUB = "Body-Response-Stub";
    private final String REQUEST_URL_STUB = "https://www.test-url.com";

    @InjectMocks
    private WebHttpClient _sut;

    private AutoCloseable closeable;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);

        _requestParametersStub = new HashMap<>();
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void Post_Given_Request_Calls_Http_Client_Request() throws IOException, InterruptedException {
        // Arrange
        when(_httpResponseMock.body()).thenReturn(BODY_RESPONSE_STUB);
        when(_httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(_httpResponseMock);

        // Act
        _sut.Post(REQUEST_URL_STUB, _requestParametersStub);

        // Assert
        verify(_httpClientMock, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    public void Post_Given_Request_Returns_Body_Response() throws IOException, InterruptedException {
        // Arrange
        when(_httpResponseMock.body()).thenReturn(BODY_RESPONSE_STUB);
        when(_httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(_httpResponseMock);

        // Act
        HttpResponse<String> httpResponse = _sut.Post(REQUEST_URL_STUB, _requestParametersStub);

        // Assert
        assertEquals(BODY_RESPONSE_STUB, httpResponse.body());
    }

}
