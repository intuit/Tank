/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.List;

/**
 * What a job queued from the project now would run, without queueing it.
 *
 * @param name             the job name that would be used
 * @param valid            true when the job can be queued
 * @param errors           why it cannot be queued
 * @param warnings         best-practice and variable usage issues; they do not block queueing
 * @param totalUsers       users across all regions
 * @param rampTimeMs       evaluated ramp time
 * @param simulationTimeMs evaluated simulation time; 0 when the scripts set the length
 * @param executionTimeMs  estimated time for the longest test plan to run once
 * @param detailsHtml      the job details shown before queueing in the web UI (HTML)
 */
public record JobPreview(String name, boolean valid, List<String> errors, List<String> warnings, int totalUsers,
                         long rampTimeMs, long simulationTimeMs, long executionTimeMs, String detailsHtml) {
}
