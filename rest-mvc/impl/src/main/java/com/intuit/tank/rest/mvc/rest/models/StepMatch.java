/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * One match of a step search.
 *
 * @param position the step's zero-based position in the list searched
 * @param key      the data key, for key/value sections such as headers and variables
 * @param value    the value that matched (for key sections, the value of the matching key)
 */
public record StepMatch(String uuid, int position, String section, String key, String value) {
}
