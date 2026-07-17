/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.logs;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Streaming log response and the byte range represented by its body.
 */
public record LogFileResponse(
        StreamingResponseBody body,
        long totalLength,
        long startOffset) {
}
