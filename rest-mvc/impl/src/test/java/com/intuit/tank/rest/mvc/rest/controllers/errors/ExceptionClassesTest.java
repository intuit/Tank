package com.intuit.tank.rest.mvc.rest.controllers.errors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    // =====================================================================
    // ClientException
    // =====================================================================

    @Test
    void clientException_constructorSetsFields() {
        ClientException e = new ClientException("error msg", 400);
        assertEquals(400, e.getStatusCode());
        assertEquals("error msg", e.getMessage());
    }

    @Test
    void clientException_getErrorMessage_parsesJsonMessage() {
        ClientException e = new ClientException("{\"message\":\"parsed error\"}", 500);
        assertEquals("parsed error", e.getErrorMessage());
    }

    @Test
    void clientException_getErrorMessage_returnsRawOnInvalidJson() {
        ClientException e = new ClientException("not json", 400);
        assertEquals("not json", e.getErrorMessage());
    }

    // =====================================================================
    // GenericServiceException
    // =====================================================================

    @Test
    void genericServiceException_defaultConstructor() {
        GenericServiceException e = new GenericServiceException();
        assertNull(e.getMessage());
    }

    @Test
    void genericServiceException_messageConstructor() {
        GenericServiceException e = new GenericServiceException("error");
        assertEquals("error", e.getMessage());
    }

    @Test
    void genericServiceException_causeConstructor() {
        Exception cause = new Exception("root cause");
        GenericServiceException e = new GenericServiceException(cause);
        assertEquals(cause, e.getCause());
    }

    @Test
    void genericServiceException_messageAndCauseConstructor() {
        Exception cause = new Exception("root");
        GenericServiceException e = new GenericServiceException("msg", cause);
        assertEquals("msg", e.getMessage());
        assertEquals(cause, e.getCause());
    }

    // =====================================================================
    // GenericServiceForbiddenAccessException
    // =====================================================================

    @Test
    void forbiddenAccessException_setsFields() {
        GenericServiceForbiddenAccessException e = new GenericServiceForbiddenAccessException("svc", "res");
        assertEquals("svc", e.getService());
        assertEquals("res", e.getResource());
    }

    // =====================================================================
    // GenericServiceInternalServerException
    // =====================================================================

    @Test
    void internalServerException_setsFields() {
        Exception cause = new Exception("db");
        GenericServiceInternalServerException e = new GenericServiceInternalServerException("svc", "res", cause);
        assertEquals("svc", e.getService());
        assertEquals("res", e.getResource());
        assertTrue(e.getMessage().contains("svc"));
        assertTrue(e.getMessage().contains("res"));
    }

    // =====================================================================
    // GenericServiceBadRequestException
    // =====================================================================

    @Test
    void badRequestException_setsFieldsWithMessage() {
        GenericServiceBadRequestException e = new GenericServiceBadRequestException("svc", "res", "bad input");
        assertEquals("svc", e.getService());
        assertEquals("res", e.getResource());
        assertTrue(e.getMessage().contains("bad input"));
    }

    // =====================================================================
    // GenericServiceResourceNotFoundException
    // =====================================================================

    @Test
    void resourceNotFoundException_setsFields() {
        GenericServiceResourceNotFoundException e = new GenericServiceResourceNotFoundException("svc", "res", null);
        assertEquals("svc", e.getService());
        assertEquals("res", e.getResource());
    }

    // =====================================================================
    // GenericServiceDeleteException
    // =====================================================================

    @Test
    void deleteException_setsFields() {
        GenericServiceDeleteException e = new GenericServiceDeleteException("svc", "res", new Exception("err"));
        assertEquals("svc", e.getService());
        assertEquals("res", e.getResource());
    }

    // =====================================================================
    // GenericServiceCreateOrUpdateException
    // =====================================================================

    @Test
    void createOrUpdateException_setsFields() {
        GenericServiceCreateOrUpdateException e = new GenericServiceCreateOrUpdateException("svc", "res", new Exception());
        assertEquals("svc", e.getService());
        assertEquals("res", e.getResource());
    }
}
