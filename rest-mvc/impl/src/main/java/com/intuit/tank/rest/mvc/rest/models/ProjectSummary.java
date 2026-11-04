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
 * A row of the projects table.
 */
public record ProjectSummary(Integer id, String name, String productName, String comments, String owner,
                             Date created, Date modified) {
}
