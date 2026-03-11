/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.clients.util;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

public class ClientException extends RuntimeException {

    private static final Logger LOGGER = LogManager.getLogger(ClientException.class);
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
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
            Map<String, String> map = JSON_MAPPER.readValue(this.message, Map.class);
            return map.get("message");
        } catch (JacksonException e) {
            LOGGER.error("ClientException error processing JSON error message");
            return message;
        }
    }
}
