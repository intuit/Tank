/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * A VM instance type that agents can run on.
 *
 * @param value         the instance type name
 * @param label         the description shown in the UI
 * @param usersPerAgent the default number of virtual users per agent of this type
 * @param isDefault     whether this type is selected by default
 */
public record InstanceTypeOption(String value, String label, int usersPerAgent, boolean isDefault) {
}
