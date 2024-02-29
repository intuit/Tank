package com.intuit.tank.rest.mvc.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.filters.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilterClientTest {

    MockWebServer mockWebServer;

    FilterClient filterClient;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String serviceUrl = mockWebServer.url("/").toString();
        filterClient = new FilterClient(serviceUrl, null, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testFilterClientDefaultConstructor_SetsFieldsCorrectly()  {
        String serviceUrl = "http://localhost:8080";
        FilterClient filterClient = new FilterClient(serviceUrl);
        assertEquals("/v2/filters", filterClient.getServiceBaseUrl());
    }

    @Test
    void testGetFilters_WhenSuccess_ReturnsFilterContainer() throws JsonProcessingException {
        FilterTO filter = new FilterTO();
        filter.setId(23);
        List<FilterTO> filters = List.of(filter);
        FilterContainer expectedContainer = new FilterContainer(filters);
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedContainer))
                        .addHeader("Content-Type", "application/json")
        );

        FilterContainer response = filterClient.getFilters();

        assertEquals(expectedContainer.getFilters().get(0).getId(), response.getFilters().get(0).getId());
    }

    @Test
    void testGetFilters_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                filterClient::getFilters
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetFilterGroups_WhenSuccess_ReturnsFilterGroupContainer() throws JsonProcessingException {
        FilterGroupTO filterGroup = new FilterGroupTO();
        filterGroup.setId(23);
        List<FilterGroupTO> filterGroups = List.of(filterGroup);
        FilterGroupContainer expectedContainer = new FilterGroupContainer(filterGroups);
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedContainer))
                        .addHeader("Content-Type", "application/json")
        );

        FilterGroupContainer response = filterClient.getFilterGroups();

        assertEquals(expectedContainer.getFilterGroups().get(0).getId(), response.getFilterGroups().get(0).getId());
    }

    @Test
    void testGetFilterGroups_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                filterClient::getFilterGroups
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetFilter_WhenSuccess_ReturnsFilterTO() throws JsonProcessingException {
        Integer filterId = 60;
        FilterTO filter = new FilterTO();
        filter.setId(filterId);
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(filter))
                        .addHeader("Content-Type", "application/json")
        );

        FilterTO response = filterClient.getFilter(filterId);

        assertEquals(filter.getId(), response.getId());
    }

    @Test
    void testGetFilter_WhenServerError_ThrowsClientException() {
        Integer filterId = 61;
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> filterClient.getFilter(filterId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetFilterGroup_WhenSuccess_ReturnsFilterGroupTO() throws JsonProcessingException {
        Integer filterGroupId = 61;
        FilterGroupTO filterGroup = new FilterGroupTO();
        filterGroup.setId(filterGroupId);
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(filterGroup))
                        .addHeader("Content-Type", "application/json")
        );

        FilterGroupTO response = filterClient.getFilterGroup(filterGroupId);

        assertEquals(filterGroup.getId(), response.getId());
    }

    @Test
    void testGetFilterGroup_WhenServerError_ThrowsClientException() {
        Integer filterGroupId = 65;
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> filterClient.getFilterGroup(filterGroupId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testApplyFilters_WhenSuccess_ReturnsString() {
        Integer scriptId = 67;
        ApplyFiltersRequest request = new ApplyFiltersRequest();
        String expectedResponse = "Filters applied successfully";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain")
        );

        String response = filterClient.applyFilters(scriptId, request);

        assertEquals(expectedResponse, response);
    }

    @Test
    void testApplyFilters_WhenServerError_ThrowsClientException() {
        Integer scriptId = 72;
        ApplyFiltersRequest request = new ApplyFiltersRequest();
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> filterClient.applyFilters(scriptId, request)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDeleteFilter_WhenSuccess_ReturnsString() {
        Integer filterId = 15; 
        String expectedResponse = "Filter deleted successfully";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain")
        );
        String response = filterClient.deleteFilter(filterId);

        assertEquals(expectedResponse, response);
    }


    @Test
    void testDeleteFilter_WhenServerError_ThrowsClientException() {
        Integer filterId = 17; 
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> filterClient.deleteFilter(filterId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }


    @Test
    void testDeleteFilterGroup_WhenSuccess_ReturnsString() {
        Integer filterGroupId = 11; 
        String expectedResponse = "Filter group deleted successfully";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain")
        );
        String response = filterClient.deleteFilterGroup(filterGroupId);
        
        assertEquals(expectedResponse, response);
    }


    @Test
    void testDeleteFilterGroup_WhenServerError_ThrowsClientException() {
        Integer filterGroupId = 12; 
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> filterClient.deleteFilterGroup(filterGroupId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }
}
