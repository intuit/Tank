/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.Map;

/**
 * A logic step script to try, with made-up inputs, for {@code POST /v2/scripts/logic/test}.
 *
 * @param scriptId        the script the logic step belongs to; its owner may test without {@code EDIT_SCRIPT}
 * @param script          the logic step's JavaScript, without Tank's standard prefix and suffix
 * @param variables       variable values the script sees
 * @param requestBody     the previous request's body
 * @param responseBody    the previous response's body
 */
public record LogicTestRequest(Integer scriptId, String script, Map<String, String> variables, String requestBody,
                               Map<String, String> requestHeaders, String responseBody,
                               Map<String, String> responseHeaders) {
}
