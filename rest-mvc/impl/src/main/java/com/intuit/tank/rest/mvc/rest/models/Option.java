/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * One choice in a drop-down list.
 *
 * @param value       the value to send back to the server
 * @param label       the text to show
 * @param description optional longer help text
 */
public record Option(String value, String label, String description) {

    public static Option of(String value, String label) {
        return new Option(value, label, null);
    }
}
