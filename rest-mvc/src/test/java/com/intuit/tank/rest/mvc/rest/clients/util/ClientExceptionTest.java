package com.intuit.tank.rest.mvc.rest.clients.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClientExceptionTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private ClientException clientException;

    @BeforeEach
    public void setUp() {
        clientException = new ClientException("some detailed client error message", 404);
    }

    @Test
    public void testGetStatusCode_ReturnsCorrectStatusCode() {
        assertEquals(404, clientException.getStatusCode());
    }

    @Test
    public void testGetMessage_ReturnsCorrectMessage() {
        assertEquals("some detailed client error message", clientException.getMessage());
    }

    @Test
    public void testGetErrorMessage_WithValidJson_ReturnsCorrectMessage() throws JsonProcessingException {
        Map<String, String> errorMap = Map.of("message", "some detailed client error message");
        String stringError = objectMapper.writeValueAsString(errorMap);
        clientException = new ClientException(stringError, 404);
        assertEquals("some detailed client error message", clientException.getErrorMessage());
    }

    @Test
    public void testGetErrorMessage_WithInvalidJson_ReturnsSameMessage() {
        String invalidJson = "not a json error messsage";
        clientException = new ClientException(invalidJson, 404);
        assertEquals(invalidJson, clientException.getErrorMessage());
    }

    @Test
    public void testGetMessage_WithNullInput_ReturnsNull() {
        clientException = new ClientException(null, 404);
        assertNull(clientException.getMessage());
    }

    @Test
    public void testGetMessage_WithEmptyString_ReturnsEmptyString() {
        clientException = new ClientException("", 404);
        assertEquals("", clientException.getMessage());
    }
}
