/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * Options for previewing or queueing a job from a project, for {@code POST /v2/projects/{id}/jobs[/preview]}.
 * The job takes every other setting from the saved project.
 *
 * @param name the job name; null for the default (project name, users and time)
 */
public record JobLaunchRequest(String name) {
}
