/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test helpers that run code as a given caller, the way the REST security filter would.
 */
public final class SecurityTestSupport {

    private SecurityTestSupport() {
    }

    public static TankPrincipal user(String name, String... roles) {
        return new TankPrincipal(name, Set.of(roles), TankPrincipal.AuthMethod.API_TOKEN);
    }

    public static void actAs(TankPrincipal principal) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RestAuthorization.PRINCIPAL_ATTRIBUTE)).thenReturn(principal);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    /**
     * Uses a configuration with the given REST security flag and right-to-group restrictions.
     */
    public static void useConfig(boolean restSecurityEnabled, Map<AccessRight, List<String>> restrictions) {
        TankConfig config = mock(TankConfig.class);
        SecurityConfig securityConfig = mock(SecurityConfig.class);
        when(config.isRestSecurityEnabled()).thenReturn(restSecurityEnabled);
        when(config.getSecurityConfig()).thenReturn(securityConfig);
        Map<String, List<String>> byName = new java.util.HashMap<>();
        restrictions.forEach((right, groups) -> byName.put(right.name(), groups));
        when(securityConfig.getRestrictionMap()).thenReturn(byName);
        RestAuthorization.setTankConfig(config);
    }

    public static void reset() {
        RequestContextHolder.resetRequestAttributes();
        RestAuthorization.setTankConfig(null);
    }
}
