package com.intuit.tank.rest.mvc.rest.controllers.errors;

/**
 * Exception thrown when an operation conflicts with the current state of a resource.
 * Maps to HTTP 409 Conflict.
 */
public class GenericServiceConflictException extends GenericServiceException {

    private static final long serialVersionUID = 1L;

    private final String service;
    private final String resource;

    public GenericServiceConflictException(String service, String resource, String message) {
        super(message);
        this.service = service;
        this.resource = resource;
    }

    public String getService() {
        return service;
    }

    public String getResource() {
        return resource;
    }
}
