/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.me;

import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.AccountUpdate;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.ColumnPreference;
import com.intuit.tank.rest.mvc.rest.models.ColumnPreferenceUpdate;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.TablePreferences;
import com.intuit.tank.rest.mvc.rest.security.AccessRules;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridgeProvider;
import com.intuit.tank.rest.mvc.rest.util.TableColumnDefaults;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MeServiceV2Impl implements MeServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(MeServiceV2Impl.class);
    private static final String SERVICE = "me";

    static final int MIN_PASSWORD_LENGTH = 8;
    static final int MAX_EMAIL_LENGTH = 255;
    static final int MAX_COLUMN_SIZE = 2000;

    @Autowired
    private WebSessionBridgeProvider webSessionBridgeProvider;

    @Override
    public CurrentUser getCurrentUser() {
        return describe(RestAuthorization.requireUser(SERVICE));
    }

    @Override
    public CurrentUser describe(TankPrincipal principal) {
        User user = findUser(principal);
        SecurityConfig securityConfig = new TankConfig().getSecurityConfig();
        Map<String, Boolean> rights = new LinkedHashMap<>();
        for (AccessRight right : AccessRight.values()) {
            rights.put(right.name(), AccessRules.hasRight(right, principal::isInRole, securityConfig));
        }
        List<String> groups = principal.getRoles().stream().sorted().collect(Collectors.toList());
        Date lastLogin = user.getLastLoginTs() != null ? Date.from(user.getLastLoginTs()) : null;
        return new CurrentUser(user.getName(), user.getEmail(), groups, AccessRules.isAdmin(principal::isInRole),
                rights, user.getApiToken() != null, lastLogin);
    }

    @Override
    public CurrentUser updateAccount(AccountUpdate update) {
        TankPrincipal principal = RestAuthorization.requireUser(SERVICE);
        if (update == null) {
            throw new GenericServiceBadRequestException(SERVICE, "account", "request body is required");
        }
        User user = findUser(principal);
        if (update.email() != null) {
            String email = update.email().trim();
            if (email.isEmpty() || email.length() > MAX_EMAIL_LENGTH || !email.contains("@")) {
                throw new GenericServiceBadRequestException(SERVICE, "email", "a valid email address is required");
            }
            user.setEmail(email);
        }
        if (update.newPassword() != null) {
            if (update.newPassword().length() < MIN_PASSWORD_LENGTH) {
                throw new GenericServiceBadRequestException(SERVICE, "password",
                        "the new password must have at least " + MIN_PASSWORD_LENGTH + " characters");
            }
            if (StringUtils.isEmpty(update.currentPassword())
                    || !PasswordEncoder.validatePassword(update.currentPassword(), user.getPassword())) {
                LOGGER.warn("Rejected password change for {}: current password is incorrect", user.getName());
                throw new GenericServiceBadRequestException(SERVICE, "password", "the current password is incorrect");
            }
            user.setPassword(PasswordEncoder.encodePassword(update.newPassword()));
        }
        new UserDao().saveOrUpdate(user);
        LOGGER.info("Updated account for {}", user.getName());
        return describe(principal);
    }

    @Override
    public ApiTokenResponse createApiToken() {
        User user = findUser(RestAuthorization.requireUser(SERVICE));
        user.generateApiToken();
        user.setTokenDisplayed(true);
        user = new UserDao().saveOrUpdate(user);
        LOGGER.info("Generated API token for {}", user.getName());
        return new ApiTokenResponse(user.getApiToken());
    }

    @Override
    public void deleteApiToken() {
        User user = findUser(RestAuthorization.requireUser(SERVICE));
        if (user.getApiToken() != null) {
            user.deleteApiToken();
            new UserDao().saveOrUpdate(user);
            LOGGER.info("Deleted API token for {}", user.getName());
        }
    }

    @Override
    public TablePreferences getPreferences() {
        return toModel(loadPreferences(RestAuthorization.requireUser(SERVICE).getName()));
    }

    @Override
    public TablePreferences updateTablePreferences(String table, List<ColumnPreferenceUpdate> updates) {
        String owner = RestAuthorization.requireUser(SERVICE).getName();
        TableColumnDefaults.Table target = Arrays.stream(TableColumnDefaults.Table.values())
                .filter(t -> t.name().equals(table))
                .findFirst()
                .orElseThrow(() -> new GenericServiceResourceNotFoundException(SERVICE, "table " + table, null));
        if (updates == null) {
            throw new GenericServiceBadRequestException(SERVICE, "columns", "request body is required");
        }
        Preferences preferences = loadPreferences(owner);
        Map<String, ColumnPreferences> columns = target.columnsOf(preferences).stream()
                .collect(Collectors.toMap(ColumnPreferences::getColName, Function.identity(), (a, b) -> a));
        for (ColumnPreferenceUpdate update : updates) {
            ColumnPreferences column = update != null ? columns.get(update.colName()) : null;
            if (column == null) {
                throw new GenericServiceBadRequestException(SERVICE, "columns",
                        "unknown column " + (update != null ? update.colName() : null) + " for table " + table);
            }
            if (Boolean.FALSE.equals(update.visible()) && !column.isHideable()) {
                throw new GenericServiceBadRequestException(SERVICE, "columns",
                        "column " + column.getColName() + " cannot be hidden");
            }
            if (update.size() != null && (update.size() <= 0 || update.size() > MAX_COLUMN_SIZE)) {
                throw new GenericServiceBadRequestException(SERVICE, "columns",
                        "column size must be between 1 and " + MAX_COLUMN_SIZE);
            }
        }
        for (ColumnPreferenceUpdate update : updates) {
            ColumnPreferences column = columns.get(update.colName());
            if (update.size() != null) {
                column.setSize(update.size());
            }
            if (update.visible() != null) {
                column.setVisible(update.visible());
            }
        }
        preferences = new PreferencesDao().saveOrUpdate(preferences);
        webSessionBridgeProvider.get().preferencesChanged(preferences);
        return toModel(preferences);
    }

    @Override
    public void resetPreferences() {
        String owner = RestAuthorization.requireUser(SERVICE).getName();
        PreferencesDao dao = new PreferencesDao();
        Preferences preferences = dao.getForOwner(owner);
        if (preferences != null) {
            dao.delete(preferences);
            webSessionBridgeProvider.get().preferencesChanged(preferences);
            LOGGER.info("Reset table preferences for {}", owner);
        }
    }

    private Preferences loadPreferences(String owner) {
        PreferencesDao dao = new PreferencesDao();
        TableColumnDefaults.Result result = TableColumnDefaults.ensureDefaults(dao.getForOwner(owner), owner);
        return result.changed() ? dao.saveOrUpdate(result.preferences()) : result.preferences();
    }

    private static TablePreferences toModel(Preferences preferences) {
        Map<String, List<ColumnPreference>> tables = new LinkedHashMap<>();
        for (TableColumnDefaults.Table table : TableColumnDefaults.Table.values()) {
            tables.put(table.name(), table.columnsOf(preferences).stream()
                    .map(c -> new ColumnPreference(c.getColName(), c.getDisplayName(), c.getSize(),
                            c.isVisible(), c.isHideable()))
                    .collect(Collectors.toList()));
        }
        return new TablePreferences(tables);
    }

    private static User findUser(TankPrincipal principal) {
        return Optional.ofNullable(new UserDao().findByUserName(principal.getName()))
                .orElseThrow(() -> new GenericServiceUnauthorizedException(SERVICE, "User no longer exists"));
    }
}
