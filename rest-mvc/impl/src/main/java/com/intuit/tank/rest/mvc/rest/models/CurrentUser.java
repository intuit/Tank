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
import java.util.Map;

/**
 * The signed-in user and what they may do.
 *
 * @param name        the user name, also recorded as the creator of what they make
 * @param email       the email address
 * @param groups      the user's group names, sorted
 * @param admin       whether the user is in the admin group
 * @param rights      every {@code AccessRight} name mapped to whether the user holds it; ownership of an
 *                    entity grants edit and delete rights on it in addition to these
 * @param hasApiToken whether the user has an API token
 * @param lastLoginTs when the user last signed in or used their API token
 */
public record CurrentUser(String name, String email, List<String> groups, boolean admin,
                          Map<String, Boolean> rights, boolean hasApiToken, Date lastLoginTs) {
}
