/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.Map;
import java.util.List;

/**
 * The signed-in user's column settings for each table, keyed by table name
 * ({@code projects}, {@code scripts}, {@code scriptSteps}, {@code datafiles}, {@code jobs}).
 */
public record TablePreferences(Map<String, List<ColumnPreference>> tables) {
}
