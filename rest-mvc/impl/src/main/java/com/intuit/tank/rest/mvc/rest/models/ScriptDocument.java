/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import com.intuit.tank.script.models.ScriptStepTO;

import java.util.Date;
import java.util.List;

/**
 * A script with its full step list, for {@code GET} and {@code PUT /v2/scripts/{id}/steps}.
 *
 * <p>A {@code PUT} replaces the header and every step with the body, in list order. Steps are
 * renumbered and relabelled on save, steps without a {@code uuid} get one, and a timer whose end comes
 * before its start is put back in order. {@code id}, {@code owner}, {@code created} and {@code permissions}
 * are ignored on input. {@code modified} must be the value from the {@code GET}; if the script was saved
 * since, the {@code PUT} is rejected with 409.</p>
 *
 * <p>Recorded responses are not included; fetch one with {@code GET .../steps/{uuid}/response}. They are
 * kept on save for steps whose {@code uuid} is unchanged. Authentication step passwords are returned as
 * {@value #MASKED_PASSWORD}; send that value back to keep the stored password.</p>
 */
public record ScriptDocument(Integer id,
                             String name,
                             String productName,
                             String comments,
                             String owner,
                             Date created,
                             Date modified,
                             List<ScriptStepTO> steps,
                             Permissions permissions) {

    public static final String MASKED_PASSWORD = "********";

    public record Permissions(boolean edit, boolean delete) {
    }
}
