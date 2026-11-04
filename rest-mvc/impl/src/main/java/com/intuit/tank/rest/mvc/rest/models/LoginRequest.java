/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * Username and password for {@code POST /v2/auth/login}.
 */
public record LoginRequest(String username, String password) {

    @Override
    public String toString() {
        return "LoginRequest[username=" + username + "]";
    }
}
