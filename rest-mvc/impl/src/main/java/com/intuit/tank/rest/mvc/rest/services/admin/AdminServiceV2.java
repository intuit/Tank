/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.admin;

import com.intuit.tank.rest.mvc.rest.models.AdminGroup;
import com.intuit.tank.rest.mvc.rest.models.AdminUser;
import com.intuit.tank.rest.mvc.rest.models.AdminUserRequest;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.LogLevelSetting;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;

import java.util.List;

/**
 * The admin pages: users, groups, log files and the log level. Every method needs a signed-in admin.
 */
public interface AdminServiceV2 {

    /**
     * @param q matches the name or email
     */
    PageResponse<AdminUser> listUsers(Integer page, Integer size, String sort, String q);

    AdminUser getUser(Integer userId);

    /**
     * Creates a user. Needs a unique name, an email address and a password; groups default to the configured
     * default groups.
     */
    AdminUser createUser(AdminUserRequest request);

    /**
     * Changes a user's email, password and/or groups. Null fields are unchanged; the name cannot change.
     */
    AdminUser updateUser(Integer userId, AdminUserRequest request);

    /**
     * Anonymizes the user's data and deletes the user. Refused while the user owns projects, and for the caller.
     */
    void deleteUser(Integer userId);

    /**
     * Generates a new API token for the user, replacing any existing one. The token is only returned here.
     */
    ApiTokenResponse createApiToken(Integer userId);

    void deleteApiToken(Integer userId);

    /**
     * Resets the user's table column preferences to the defaults.
     */
    void resetPreferences(Integer userId);

    /**
     * @return the configured security groups, sorted by name
     */
    List<AdminGroup> getGroups();

    /**
     * @return the log file names on this node, Tank logs first; read one with {@code GET /v2/logs/{file}}
     */
    List<String> listLogFiles();

    LogLevelSetting getLogLevel();

    /**
     * Sets the log level of this node only.
     */
    LogLevelSetting setLogLevel(LogLevelSetting request);
}
