package com.intuit.tank.rest.mvc.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DataFileClientTest {
    MockWebServer mockWebServer;

    DataFileClient dataFileClient;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String serviceUrl = mockWebServer.url("/").toString();
        dataFileClient = new DataFileClient(serviceUrl, null, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testDataFileClientDefaultConstructor_SetsFieldsCorrectly()  {
        String serviceUrl = "http://localhost:8080";
        DataFileClient agentClient = new DataFileClient(serviceUrl);
        assertEquals("/v2/datafiles", agentClient.getServiceBaseUrl());
    }

    @Test
    void testGetDatafiles_WhenSuccess_ReturnsDataFileDescriptorContainer() throws JsonProcessingException {
        DataFileDescriptor dataFileDescriptor = new DataFileDescriptor();
        dataFileDescriptor.setName("testName");
        List<DataFileDescriptor> list = List.of(dataFileDescriptor);
        DataFileDescriptorContainer expectedContainer = new DataFileDescriptorContainer(list);
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedContainer))
                        .addHeader("Content-Type", "application/json")
        );

        DataFileDescriptorContainer response = dataFileClient.getDatafiles();
        assertEquals(expectedContainer.getDataFiles().get(0).getName(), response.getDataFiles().get(0).getName());
    }

    @Test
    void testGetDatafiles_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> dataFileClient.getDatafiles()
        );
        assertTrue(exception.getMessage().contains(errorBody));

    }

    @Test
    void testGetDatafile_WhenSuccess_ReturnsDataFileDescriptor() throws JsonProcessingException {
        Integer datafileId = 17;
        DataFileDescriptor expectedDescriptor = new DataFileDescriptor();
        expectedDescriptor.setId(datafileId);
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedDescriptor))
                        .addHeader("Content-Type", "application/json")
        );

        DataFileDescriptor response = dataFileClient.getDatafile(datafileId);
        assertEquals(expectedDescriptor.getId(), response.getId());
    }

    @Test
    void testGetDatafile_WhenServerError_ThrowsClientException() {
        Integer datafileId = 18;
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> dataFileClient.getDatafile(datafileId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetDatafileContent_WhenSuccess_ReturnsContentString() {
        Integer datafileId = 19;
        String expectedContent = "Test File Content";
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedContent)
                        .addHeader("Content-Type", "text/plain")
        );

        String response = dataFileClient.getDatafileContent(datafileId);
        assertEquals(expectedContent, response);
    }

    @Test
    void testGetDatafileContent_WhenServerError_ThrowsClientException() {
        Integer datafileId = 20;
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> dataFileClient.getDatafileContent(datafileId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDownloadDatafile_WhenSuccess_ReturnsDataBuffer() {
        Integer datafileId = 32;
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        DataBuffer expectedDataBuffer = dataBufferFactory.wrap("Test Datafile Content".getBytes());
        mockWebServer.enqueue(new MockResponse().setBody("Test Datafile Content").setHeader("Content-Type", "application/octet-stream"));
        Mono<DataBuffer> responseMono = dataFileClient.downloadDatafile(datafileId);

        StepVerifier.create(responseMono)
                .assertNext(dataBuffer -> {
                    assertEquals(expectedDataBuffer.readableByteCount(), dataBuffer.readableByteCount());
                    assertEquals(expectedDataBuffer.asByteBuffer().compareTo(dataBuffer.asByteBuffer()), 0);
                })
                .verifyComplete();
    }

    @Test
    void testDownloadDatafile_WhenServerError_ThrowsClientException() {
        Integer datafileId = 32;
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));
        Mono<DataBuffer> responseMono = dataFileClient.downloadDatafile(datafileId);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof ClientException
                        && throwable.getMessage().contains(errorBody))
                .verify();
    }

    @Test
    void testUploadDatafile_WhenSuccess_ReturnsMap() throws JsonProcessingException {
        Integer id = 45;
        MultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "test data".getBytes());

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Datafile with new datafile ID" + id + "has been uploaded");
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );
        Mono<ResponseEntity<Map<String, String>>> response = dataFileClient.uploadDatafile(id, file);

        StepVerifier.create(response.map(ResponseEntity::getBody))
                .consumeNextWith(actual -> assertEquals(expectedResponse, actual))
                .expectComplete()
                .verify();
    }

    @Test
    void uploadDatafile_WhenServerError_ThrowsClientException() {
        Integer id = 46;
        MultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "test data".getBytes());

        String errorBody = "Internal Server Error";
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Mono<ResponseEntity<Map<String, String>>> response = dataFileClient.uploadDatafile(id, file);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof ClientException
                        && throwable.getMessage().equals(errorBody))
                .verify();
    }

    @Test
    void testDeleteDatafile_WhenSuccess_ReturnsString() {
        Integer datafileId = 50;
        String expectedResponse = "Datafile deleted successfully";
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain")
        );

        String serviceUrl = mockWebServer.url("/").toString();
        DataFileClient dataFileClient = new DataFileClient(serviceUrl, null, null);
        String response = dataFileClient.deleteDatafile(datafileId);

        assertEquals(expectedResponse, response);
    }

    @Test
    void testDeleteDatafile_WhenServerError_ThrowsClientException() {
        Integer datafileId = 51;
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        String serviceUrl = mockWebServer.url("/").toString();
        DataFileClient dataFileClient = new DataFileClient(serviceUrl, null, null);

        Exception exception = assertThrows(
                ClientException.class,
                () -> dataFileClient.deleteDatafile(datafileId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }
}
