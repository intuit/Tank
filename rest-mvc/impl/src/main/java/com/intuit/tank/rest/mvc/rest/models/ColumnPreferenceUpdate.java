/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * A change to one column for {@code PUT /v2/me/preferences/tables/{table}}. Null fields are not changed.
 */
public record ColumnPreferenceUpdate(String colName, Integer size, Boolean visible) {
}
