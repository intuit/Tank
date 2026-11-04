/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.Data;

/**
 * This is the body of the every error response (status codes 400 - 599) that is
 * sent from the REST API. OpenAPI documents it as {@code ErrorResponse} on every error response
 * (see {@link com.intuit.tank.rest.mvc.rest.docs.ErrorResponsesOpenApiCustomizer}).
 */
@Data
@Schema(name = "ErrorResponse", description = "The body of a JSON error response")
public class SimpleErrorResponseBody {
    @Schema(description = "What went wrong, for showing to a user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "The stack trace, when the server is set to include debug info; otherwise null",
            types = { "string", "null" }, requiredMode = Schema.RequiredMode.REQUIRED)
    private String debugInfo;

    // No status or cause provided, default to 500
    public SimpleErrorResponseBody(String message, Throwable cause, boolean scrubSensitiveData) {
        this.message = message;
        this.debugInfo = (scrubSensitiveData || cause == null ? null : ExceptionUtils.getStackTrace(cause));
    }
}
