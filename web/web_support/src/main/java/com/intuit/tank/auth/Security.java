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
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.security.Identity;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.Role;

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
public class Security implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Identity identity;

    /**
     * 
     * @param entity
     * @return
     */
    public boolean isOwner(OwnableEntity entity) {
        return StringUtils.isEmpty(entity.getCreator()) || identity.getUser().getId().equals(entity.getCreator());
    }

    /**
     * 
     * @param entity
     * @return
     */
    public boolean isAdmin() {
        return identity.hasRole(TankConstants.TANK_GROUP_ADMIN, TankConstants.TANK_GROUP_ADMIN,
                TankConstants.TANK_GROUP_TYPE);
    }

    /**
     * 
     * @param entity
     * @return
     */
    public boolean hasRole(String role) {
        return identity.hasRole(role, role, TankConstants.TANK_GROUP_TYPE);
    }

    public boolean hasRight(AccessRight right) {
        if (isAdmin()) {
            return true;
        }

        SecurityConfig config = new TankConfig().getSecurityConfig();
        List<String> associatedGroups = config.getRestrictionMap().get(right.name());
        // Set<Role> userRoles = identity.getRoles();
        Set<Group> userGroups = identity.getGroups();
        if (associatedGroups != null) {
            for (Group group : userGroups) {
                if (associatedGroups.contains(group.getName())) {
                    return true;
                }
            }
        }

        return false;

    }

}
