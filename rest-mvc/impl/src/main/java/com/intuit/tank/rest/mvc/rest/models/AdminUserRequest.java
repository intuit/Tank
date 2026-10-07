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
 * Creates or updates a user from the admin pages. On update, null fields are left unchanged and the name
 * cannot change.
 *
 * @param password the new password; required on create
 * @param groups   the user's groups, each one of the configured groups; null on create means the default groups
 */
public record AdminUserRequest(String name, String email, String password, List<String> groups) {

    @Override
    public String toString() {
        return "AdminUserRequest[name=" + name + ", email=" + email + ", groups=" + groups + "]";
    }
}
