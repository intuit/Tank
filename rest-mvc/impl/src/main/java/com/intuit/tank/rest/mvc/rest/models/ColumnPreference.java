/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * One column of a table in the UI.
 *
 * @param colName     the column id
 * @param displayName the column heading
 * @param size        the width in pixels
 * @param visible     whether the column is shown
 * @param hideable    whether the user may hide the column
 */
public record ColumnPreference(String colName, String displayName, int size, boolean visible, boolean hideable) {
}
