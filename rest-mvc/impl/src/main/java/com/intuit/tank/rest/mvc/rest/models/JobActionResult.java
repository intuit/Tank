/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * The outcome of a job or agent action.
 *
 * @param id     the job id or agent instance id
 * @param action the action requested
 * @param status the status after the request was sent; actions are asynchronous, so this may not yet
 *               reflect the action
 */
public record JobActionResult(String id, String action, String status) {
}
