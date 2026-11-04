/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.vm.common.TankConstants;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Identity of the caller of a {@code /v2} REST request, attached to the request by the
 * REST security filter. Carries the user's group names as roles and records how the caller
 * authenticated so that session-only protections (CSRF) and agent-only endpoints can be enforced.
 */
public final class TankPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = 1L;

    public enum AuthMethod {
        /** Logged in through the web UI (form login or SSO); authenticated by session cookie. */
        SESSION,
        /** A user's personal API token sent as a bearer token. */
        API_TOKEN,
        /** The shared agent token used by load agents and controller internals. */
        AGENT_TOKEN
    }

    private final String name;
    private final Set<String> roles;
    private final AuthMethod authMethod;

    public TankPrincipal(String name, Set<String> roles, AuthMethod authMethod) {
        this.name = Objects.requireNonNull(name, "name");
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(roles));
        this.authMethod = Objects.requireNonNull(authMethod, "authMethod");
    }

    public static TankPrincipal agent() {
        return new TankPrincipal(TankConstants.TANK_USER_SYSTEM, Collections.emptySet(), AuthMethod.AGENT_TOKEN);
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean isInRole(String role) {
        return roles.contains(role);
    }

    public AuthMethod getAuthMethod() {
        return authMethod;
    }

    public boolean isAgent() {
        return authMethod == AuthMethod.AGENT_TOKEN;
    }

    @Override
    public String toString() {
        return "TankPrincipal[" + name + ", " + authMethod + "]";
    }
}
