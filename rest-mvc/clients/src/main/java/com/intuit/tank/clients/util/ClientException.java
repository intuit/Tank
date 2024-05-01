/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.clients.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ClientException extends RuntimeException {

    private static final Logger LOGGER = LogManager.getLogger(ClientException.class);

    private static final long serialVersionUID = 1L;

    private int statusCode;

    private String message;

    public ClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(this.message, Map.class);
            return map.get("message");
        } catch (JsonProcessingException e) {
            LOGGER.error("ClientException error processing JSON error message");
            return message;
        }
    }
}
