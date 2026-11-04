/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.admin;

import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.dao.PagedResult;
import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.models.AdminGroup;
import com.intuit.tank.rest.mvc.rest.models.AdminUser;
import com.intuit.tank.rest.mvc.rest.models.AdminUserRequest;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.LogLevelSetting;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridgeProvider;
import com.intuit.tank.rest.mvc.rest.util.LogDirectory;
import com.intuit.tank.rest.mvc.rest.util.LogLevels;
import com.intuit.tank.rest.mvc.rest.util.PageRequests;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceV2Impl implements AdminServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(AdminServiceV2Impl.class);
    private static final String SERVICE = "admin";

    static final int MIN_PASSWORD_LENGTH = 8;
    static final int MAX_LENGTH = 255;
    static final int TOKEN_HINT_LENGTH = 4;
    /** How many owned project names a refused delete lists. */
    static final int MAX_LISTED_PROJECTS = 10;

    private static final Map<String, String> SORTABLE_FIELDS = Map.of(
            "id", BaseEntity.PROPERTY_ID,
            "name", User.PROPERTY_NAME,
            "email", User.PROPERTY_EMAIL,
            "lastLoginTs", "lastLoginTs",
            "created", BaseEntity.PROPERTY_CREATE,
            "modified", BaseEntity.PROPERTY_MODIFIED);

    @Autowired
    private WebSessionBridgeProvider webSessionBridgeProvider;

    @Override
    public PageResponse<AdminUser> listUsers(Integer page, Integer size, String sort, String q) {
        requireAdmin();
        PagedQuery query = PageRequests.toQuery(SERVICE, page, size, sort, SORTABLE_FIELDS, "name,asc", Map.of(), q,
                List.of(User.PROPERTY_NAME, User.PROPERTY_EMAIL));
        PagedResult<User> result = new UserDao().findPaged(query);
        List<AdminUser> items = result.items().stream().map(AdminServiceV2Impl::toModel).collect(Collectors.toList());
        return new PageResponse<>(items, result.total(), query.page(), query.size());
    }

    @Override
    public AdminUser getUser(Integer userId) {
        requireAdmin();
        return toModel(findUser(userId));
    }

    @Override
    public AdminUser createUser(AdminUserRequest request) {
        TankPrincipal caller = requireAdmin();
        if (request == null) {
            throw new GenericServiceBadRequestException(SERVICE, "user", "request body is required");
        }
        String name = StringUtils.trimToNull(request.name());
        if (name == null || name.length() > MAX_LENGTH) {
            throw new GenericServiceBadRequestException(SERVICE, "name",
                    "name is required and must be at most " + MAX_LENGTH + " characters");
        }
        if (request.password() == null) {
            throw new GenericServiceBadRequestException(SERVICE, "password", "password is required");
        }
        String email = requireEmail(request.email());
        String password = requirePassword(request.password());
        SecurityConfig securityConfig = new TankConfig().getSecurityConfig();
        Set<String> groups = request.groups() == null ? new LinkedHashSet<>(securityConfig.getDefaultGroups())
                : requireGroups(request.groups(), securityConfig);
        UserDao dao = new UserDao();
        if (dao.findByUserName(name) != null) {
            throw new GenericServiceConflictException(SERVICE, "A user named " + name + " already exists");
        }
        User user = User.builder().name(name).email(email).password(password).build();
        // saved before it joins its groups, as UserEdit does: the groups come from another session, and only an
        // update (a merge) takes them; persisting a new user with them fails as "detached entity passed to persist"
        user = save(dao, user, "user");
        setGroups(user, groups);
        user = save(dao, user, "user");
        LOGGER.info("{} created user {} with groups {}", caller.getName(), name, groups);
        userChanged(user);
        return toModel(user);
    }

    @Override
    public AdminUser updateUser(Integer userId, AdminUserRequest request) {
        TankPrincipal caller = requireAdmin();
        if (request == null) {
            throw new GenericServiceBadRequestException(SERVICE, "user", "request body is required");
        }
        User user = findUser(userId);
        if (request.name() != null && !request.name().trim().equals(user.getName())) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "a user's name cannot be changed");
        }
        if (request.email() != null) {
            user.setEmail(requireEmail(request.email()));
        }
        if (request.password() != null) {
            user.setPassword(requirePassword(request.password()));
        }
        if (request.groups() != null) {
            Set<String> groups = requireGroups(request.groups(), new TankConfig().getSecurityConfig());
            if (user.getName().equals(caller.getName()) && !groups.contains(TankConstants.TANK_GROUP_ADMIN)) {
                throw new GenericServiceBadRequestException(SERVICE, "groups",
                        "you cannot remove yourself from the " + TankConstants.TANK_GROUP_ADMIN + " group");
            }
            setGroups(user, groups);
        }
        user = save(new UserDao(), user, "user");
        LOGGER.info("{} updated user {} (email {}, password {}, groups {})", caller.getName(), user.getName(),
                request.email() != null ? "changed" : "unchanged", request.password() != null ? "changed" : "unchanged",
                request.groups() != null ? request.groups() : "unchanged");
        userChanged(user);
        return toModel(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        TankPrincipal caller = requireAdmin();
        User user = findUser(userId);
        String name = user.getName();
        if (name.equals(caller.getName())) {
            throw new GenericServiceBadRequestException(SERVICE, "user", "you cannot delete yourself");
        }
        List<Project> projects = new ProjectDao().listForOwner(name);
        if (!projects.isEmpty()) {
            String listed = projects.stream().limit(MAX_LISTED_PROJECTS).map(Project::getName)
                    .collect(Collectors.joining(", "));
            String more = projects.size() > MAX_LISTED_PROJECTS
                    ? " and " + (projects.size() - MAX_LISTED_PROJECTS) + " more" : "";
            throw new GenericServiceConflictException(SERVICE, "User " + name + " owns " + projects.size()
                    + " project(s) (" + listed + more + "); reassign or delete them first");
        }
        UserDao dao = new UserDao();
        // anonymize first so the user's other data keeps its references, then remove the row
        if (dao.deleteUserData(name) == 0) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "user " + userId, null);
        }
        try {
            dao.delete(user.getId());
        } catch (RuntimeException e) {
            // as in the JSF page: the user is already anonymized, which is what matters
            LOGGER.warn("User {} was anonymized but its row could not be deleted: {}", name, e.getMessage());
        }
        LOGGER.info("{} deleted user {}", caller.getName(), name);
        userChanged(user);
    }

    @Override
    public ApiTokenResponse createApiToken(Integer userId) {
        TankPrincipal caller = requireAdmin();
        User user = findUser(userId);
        user.generateApiToken();
        user = save(new UserDao(), user, "API token");
        LOGGER.info("{} generated an API token for {}", caller.getName(), user.getName());
        return new ApiTokenResponse(user.getApiToken());
    }

    @Override
    public void deleteApiToken(Integer userId) {
        TankPrincipal caller = requireAdmin();
        User user = findUser(userId);
        if (user.getApiToken() != null) {
            user.deleteApiToken();
            save(new UserDao(), user, "API token");
            LOGGER.info("{} deleted the API token of {}", caller.getName(), user.getName());
        }
    }

    @Override
    public void resetPreferences(Integer userId) {
        TankPrincipal caller = requireAdmin();
        User user = findUser(userId);
        PreferencesDao dao = new PreferencesDao();
        Preferences preferences = dao.getForOwner(user.getName());
        if (preferences != null) {
            dao.delete(preferences);
            try {
                webSessionBridgeProvider.get().preferencesChanged(preferences);
            } catch (RuntimeException e) {
                LOGGER.warn("Could not tell the web UI that preferences were reset: {}", e.getMessage());
            }
            LOGGER.info("{} reset the table preferences of {}", caller.getName(), user.getName());
        }
    }

    @Override
    public List<AdminGroup> getGroups() {
        requireAdmin();
        SecurityConfig securityConfig = new TankConfig().getSecurityConfig();
        Set<String> defaults = securityConfig.getDefaultGroups();
        return securityConfig.getGroups().stream().sorted()
                .map(g -> new AdminGroup(g, defaults.contains(g)))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> listLogFiles() {
        requireAdmin();
        return LogDirectory.listFileNames();
    }

    @Override
    public LogLevelSetting getLogLevel() {
        requireAdmin();
        return new LogLevelSetting(LogLevels.current().name(), nodeName());
    }

    @Override
    public LogLevelSetting setLogLevel(LogLevelSetting request) {
        TankPrincipal caller = requireAdmin();
        Level level = LogLevels.parse(request != null ? request.level() : null)
                .orElseThrow(() -> new GenericServiceBadRequestException(SERVICE, "level",
                        "level must be one of " + String.join(", ", LogLevels.names())));
        LogLevels.set(level);
        LOGGER.warn("{} set the log level of {} to {}", caller.getName(), nodeName(), level);
        return new LogLevelSetting(LogLevels.current().name(), nodeName());
    }

    /**
     * Admin pages are for people: the agent token and anonymous callers are refused even when REST security is off.
     */
    private static TankPrincipal requireAdmin() {
        TankPrincipal caller = RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireAdmin(SERVICE);
        return caller;
    }

    private static User findUser(Integer userId) {
        User user = userId != null ? new UserDao().findById(userId) : null;
        if (user == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "user " + userId, null);
        }
        return user;
    }

    private static String requireEmail(String requested) {
        String email = StringUtils.trimToNull(requested);
        if (email == null || email.length() > MAX_LENGTH || !email.contains("@")) {
            throw new GenericServiceBadRequestException(SERVICE, "email", "a valid email address is required");
        }
        return email;
    }

    private static String requirePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new GenericServiceBadRequestException(SERVICE, "password",
                    "the password must have at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        return PasswordEncoder.encodePassword(password);
    }

    private static Set<String> requireGroups(List<String> requested, SecurityConfig securityConfig) {
        Set<String> groups = requested.stream().filter(StringUtils::isNotBlank).map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<String> unknown = groups.stream().filter(g -> !securityConfig.getGroups().contains(g)).toList();
        if (!unknown.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "groups", "unknown groups: " + unknown
                    + "; use one of " + securityConfig.getGroups().stream().sorted().toList());
        }
        return groups;
    }

    private static void setGroups(User user, Set<String> groups) {
        GroupDao groupDao = new GroupDao();
        user.getGroups().clear();
        for (String g : groups) {
            user.addGroup(groupDao.getOrCreateGroup(g));
        }
    }

    private static User save(UserDao dao, User user, String what) {
        try {
            return dao.saveOrUpdate(user);
        } catch (RuntimeException e) {
            LOGGER.error("Error saving {} for {}: {}", what, user.getName(), e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, what, e);
        }
    }

    private void userChanged(User user) {
        try {
            webSessionBridgeProvider.get().userChanged(user);
        } catch (RuntimeException e) {
            // the JSF user list reloads on its own within five minutes
            LOGGER.warn("Could not tell the web UI that user {} changed: {}", user.getName(), e.getMessage());
        }
    }

    static AdminUser toModel(User user) {
        String token = user.getApiToken();
        String hint = token != null && token.length() > TOKEN_HINT_LENGTH
                ? token.substring(token.length() - TOKEN_HINT_LENGTH) : null;
        List<String> groups = user.getGroups().stream().map(Group::getName).sorted().collect(Collectors.toList());
        Date lastLogin = user.getLastLoginTs() != null ? Date.from(user.getLastLoginTs()) : null;
        return new AdminUser(user.getId(), user.getName(), user.getEmail(), groups, token != null, hint, lastLogin,
                user.getCreated(), user.getModified());
    }

    private static String nodeName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return null;
        }
    }
}
