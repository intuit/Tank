/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * Changes to the signed-in user's account for {@code PUT /v2/me}. Fields left null are not changed.
 *
 * @param email           a new email address
 * @param currentPassword the current password, required when changing the password
 * @param newPassword     a new password, at least 8 characters
 */
public record AccountUpdate(String email, String currentPassword, String newPassword) {

    @Override
    public String toString() {
        return "AccountUpdate[email=" + email + ", passwordChange=" + (newPassword != null) + "]";
    }
}
