/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.Date;

/**
 * A job's stored details and its live totals.
 *
 * @param detailsHtml the job details recorded when it was queued (HTML)
 */
public record JobDetails(int jobId, String name, String status, String creator, Date created, Date startTime,
                         Date endTime, int activeUsers, int totalUsers, Failures failures, String detailsHtml) {
}
