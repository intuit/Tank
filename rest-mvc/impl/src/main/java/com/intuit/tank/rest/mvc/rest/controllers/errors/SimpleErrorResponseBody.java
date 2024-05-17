/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.Data;

/**
 * This is the body of the every error response (status codes 400 - 599) that is
 * sent from the REST API.
 */
@Data
public class SimpleErrorResponseBody {
    private String message;
    private String debugInfo;

    // No status or cause provided, default to 500
    public SimpleErrorResponseBody(String message, Throwable cause, boolean scrubSensitiveData) {
        this.message = message;
        this.debugInfo = (scrubSensitiveData || cause == null ? null : ExceptionUtils.getStackTrace(cause));
    }
}
