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
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
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
        if ( StringUtils.isNotEmpty(entity.getCreator()) &&
                securityContext.getCallerPrincipal() != null ) {
            return entity.getCreator().equals(securityContext.getCallerPrincipal().getName());
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isAdmin() {
        return securityContext.isCallerInRole(TankConstants.TANK_GROUP_ADMIN);
    }

    public boolean hasRight(AccessRight right) {
        if (isAdmin()) {
            return true;
        }
        SecurityConfig config = tankConfig.getSecurityConfig();
        List<String> associatedGroups = config.getRestrictionMap().get(right.name());
        if (associatedGroups != null) {
            return associatedGroups.stream().anyMatch(role -> securityContext.isCallerInRole(role));
        }
        return false;
    }

    public String getName() {
        return securityContext.getCallerPrincipal() != null ?
                securityContext.getCallerPrincipal().getName() :
                "";
    }
}