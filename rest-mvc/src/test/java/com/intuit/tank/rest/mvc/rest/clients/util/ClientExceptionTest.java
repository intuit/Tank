package com.intuit.tank.rest.mvc.rest.clients.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

public class ClientExceptionTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetStatusCode() {
        ClientException clientException = new ClientException("some detailed client error message", 404);
        assertEquals(404, clientException.getStatusCode());
    }


    @Test
    public void testGetMessage() {
        ClientException clientException = new ClientException("some detailed client error message", 404);
        assertEquals("some detailed client error message", clientException.getMessage());
    }


    @Test
    public void testGetErrorMessageWithValidJson() throws JsonProcessingException {
        Map<String, String> errorMap = Map.of("message", "some detailed client error message");
        String stringError = objectMapper.writeValueAsString(errorMap);
        ClientException clientException = new ClientException(stringError, 404);
        assertEquals("some detailed client error message", clientException.getErrorMessage());
        assertEquals(404, clientException.getStatusCode());
    }

    @Test
    public void testGetErrorMessageWithInvalidJson() {
        String invalidJson = "not a json error messsage";
        ClientException clientException = new ClientException(invalidJson, 404);
        assertEquals(invalidJson, clientException.getErrorMessage()); // should return same message but logs exception
    }
}
