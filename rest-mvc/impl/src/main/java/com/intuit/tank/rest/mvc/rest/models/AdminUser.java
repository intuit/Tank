/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.Date;
import java.util.List;

/**
 * A user as the admin pages show it. Never includes the password hash, and the API token only as a hint.
 *
 * @param apiTokenHint the last characters of the API token, or null when the user has none
 */
public record AdminUser(int id, String name, String email, List<String> groups, boolean hasApiToken,
                        String apiTokenHint, Date lastLoginTs, Date created, Date modified) {
}
