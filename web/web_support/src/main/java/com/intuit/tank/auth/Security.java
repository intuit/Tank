/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.auth;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.rest.mvc.rest.security.AccessRules;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * Security
 *
 * @author dangleton
 *
 */
@Named
@RequestScoped
public class Security implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TankSecurityContext securityContext;

    @Inject
    private TankConfig tankConfig;

    /**
     *
     * @param entity
     * @return
     */
    public boolean isOwner(OwnableEntity entity) {
        return securityContext.getCallerPrincipal() != null
                && AccessRules.isOwner(securityContext.getCallerPrincipal().getName(), entity);
    }

    /**
     *
     * @return
     */
    public boolean isAdmin() {
        return AccessRules.isAdmin(securityContext::isCallerInRole);
    }

    public boolean hasRight(AccessRight right) {
        return AccessRules.hasRight(right, securityContext::isCallerInRole, tankConfig.getSecurityConfig());
    }

    public String getName() {
        return securityContext.getCallerPrincipal() != null ?
                securityContext.getCallerPrincipal().getName() :
                "";
    }
}