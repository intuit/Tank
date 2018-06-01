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

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.Role;

import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;

import static org.picketlink.idm.model.basic.BasicModel.*;


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
    
    @Inject
    private IdentityManager identityManager;

    @Inject
    private RelationshipManager relationshipManager;

    @Inject
    private TankConfig tankConfig;

    /**
     * 
     * @param entity
     * @return
     */
    public boolean isOwner(OwnableEntity entity) {
    	if ( StringUtils.isNotEmpty(entity.getCreator()) && getUser(identityManager,entity.getCreator()) != null && identity.getAccount() != null ) {
    			return getUser(identityManager,entity.getCreator()).getId().equals(identity.getAccount().getId());
    	}
    	return false;
    }

    /**
     * 
     * @param entity
     * @return
     */
    public boolean isAdmin() {
    	Role adminRole;
    	if ( identity.isLoggedIn() && 
    			identity.getAccount() != null && 
    			(adminRole = getRole(identityManager, TankConstants.TANK_GROUP_ADMIN)) != null ) {
    			return org.picketlink.idm.model.basic.BasicModel.hasRole(relationshipManager, identity.getAccount(), adminRole );
    	}
    	return false;
    }

    /**
     * 
     * @param entity
     * @return
     */
    public boolean hasRole(String roleString) {
    	Role role;
    	if (StringUtils.isNotEmpty(roleString)) {
    		if ( identity.isLoggedIn() && 
    				identity.getAccount() != null && 
    				(role = getRole(identityManager, roleString)) != null ) {
    			return org.picketlink.idm.model.basic.BasicModel.hasRole(relationshipManager, identity.getAccount(), role);
    		}
    	}
    	return false;
    }

    public boolean hasRight(AccessRight right) {
        if (isAdmin()) {
            return true;
        }
        SecurityConfig config = tankConfig.getSecurityConfig();
        List<String> associatedGroups = config.getRestrictionMap().get(right.name());
        if (associatedGroups != null) {
            return associatedGroups.stream().anyMatch(this::hasRole);
        }
        return false;
    }

}
