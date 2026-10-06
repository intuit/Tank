/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.util.Optional;

/**
 * Authorization checks for REST services, based on the {@link TankPrincipal} that the REST security
 * filter attaches to each request.
 *
 * <p>Rules, in order:</p>
 * <ol>
 *     <li>The agent token may do anything (agents and controller internals).</li>
 *     <li>Admins may do anything.</li>
 *     <li>Otherwise the caller needs the {@link AccessRight} through a group, or must own the entity.</li>
 * </ol>
 *
 * <p>Anonymous callers are only possible when {@code rest-security-enabled} is false. In that mode the
 * checks allow the call, which preserves the behaviour of existing unauthenticated deployments; enable
 * REST security to enforce permissions for every caller.</p>
 *
 * <p>Checks throw {@link GenericServiceForbiddenAccessException} (HTTP 403). Call them before any
 * {@code try/catch(Exception)} block in a service so the exception is not re-wrapped.</p>
 */
public final class RestAuthorization {

    private static final Logger LOGGER = LogManager.getLogger(RestAuthorization.class);

    /** Request attribute under which the REST security filter stores the {@link TankPrincipal}. */
    public static final String PRINCIPAL_ATTRIBUTE = TankPrincipal.class.getName();

    private static volatile TankConfig tankConfig;

    private RestAuthorization() {
    }

    public static Optional<TankPrincipal> currentPrincipal() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) {
            return Optional.empty();
        }
        return principalOf(((ServletRequestAttributes) attributes).getRequest());
    }

    public static Optional<TankPrincipal> principalOf(HttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        Object attribute = request.getAttribute(PRINCIPAL_ATTRIBUTE);
        if (attribute instanceof TankPrincipal) {
            return Optional.of((TankPrincipal) attribute);
        }
        Principal principal = request.getUserPrincipal();
        return principal instanceof TankPrincipal ? Optional.of((TankPrincipal) principal) : Optional.empty();
    }

    /**
     * @return the name to record as creator of new entities: the caller's user name, or
     *         {@code System} for the agent token and anonymous callers
     */
    public static String currentUserName() {
        return currentPrincipal()
                .filter(p -> !p.isAgent())
                .map(TankPrincipal::getName)
                .orElse(TankConstants.TANK_USER_SYSTEM);
    }

    public static boolean isAdmin() {
        return currentPrincipal().map(RestAuthorization::isPrivileged).orElseGet(RestAuthorization::isAnonymousAllowed);
    }

    public static boolean hasRight(AccessRight right) {
        return currentPrincipal()
                .map(p -> isPrivileged(p) || AccessRules.hasRight(right, p::isInRole, getTankConfig().getSecurityConfig()))
                .orElseGet(RestAuthorization::isAnonymousAllowed);
    }

    public static boolean isOwner(OwnableEntity entity) {
        return currentPrincipal()
                .map(p -> !p.isAgent() && AccessRules.isOwner(p.getName(), entity))
                .orElse(false);
    }

    public static boolean isOwner(String creator) {
        return currentPrincipal()
                .map(p -> !p.isAgent() && creator != null && creator.equals(p.getName()))
                .orElse(false);
    }

    public static void requireRight(AccessRight right, String service) {
        if (!hasRight(right)) {
            deny(service, right.getDisplay());
        }
    }

    public static void requireRightOrOwner(AccessRight right, OwnableEntity entity, String service) {
        if (!hasRight(right) && !isOwner(entity)) {
            deny(service, right.getDisplay());
        }
    }

    public static void requireRightOrOwner(AccessRight right, String creator, String service) {
        if (!hasRight(right) && !isOwner(creator)) {
            deny(service, right.getDisplay());
        }
    }

    /**
     * Requires a signed-in user, whatever the {@code rest-security-enabled} setting: endpoints about
     * "the current user" have no meaning for anonymous callers or the agent token.
     *
     * @return the caller
     * @throws GenericServiceUnauthorizedException (401) for anonymous callers
     * @throws GenericServiceForbiddenAccessException (403) for the agent token
     */
    public static TankPrincipal requireUser(String service) {
        TankPrincipal principal = currentPrincipal()
                .orElseThrow(() -> new GenericServiceUnauthorizedException(service, "Authentication required"));
        if (principal.isAgent()) {
            deny(service, "user resources");
        }
        return principal;
    }

    public static void requireAdmin(String service) {
        if (!isAdmin()) {
            deny(service, "admin resources");
        }
    }

    /**
     * Restricts an agent-facing endpoint (settings, registration, status reporting) to the agent token or
     * to users holding {@code right}. Standalone agents are started with a user's API token, so a
     * user path is still needed.
     */
    public static void requireAgentOrRight(AccessRight right, String service) {
        if (!hasRight(right)) {
            deny(service, "agent resources");
        }
    }

    private static boolean isPrivileged(TankPrincipal principal) {
        return principal.isAgent() || AccessRules.isAdmin(principal::isInRole);
    }

    private static boolean isAnonymousAllowed() {
        return !getTankConfig().isRestSecurityEnabled();
    }

    private static void deny(String service, String resource) {
        LOGGER.warn("Denied {} access to {} for {}", service, resource,
                currentPrincipal().map(TankPrincipal::getName).orElse("anonymous"));
        throw new GenericServiceForbiddenAccessException(service, resource);
    }

    private static TankConfig getTankConfig() {
        TankConfig config = tankConfig;
        if (config == null) {
            config = new TankConfig();
            tankConfig = config;
        }
        return config;
    }

    /** Overrides the configuration source; for tests. */
    static void setTankConfig(TankConfig config) {
        tankConfig = config;
    }
}
