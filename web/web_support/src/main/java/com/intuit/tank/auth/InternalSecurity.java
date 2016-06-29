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

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.deltaspike.security.api.authorization.Secures;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.User;

import com.intuit.tank.config.Admin;
import com.intuit.tank.config.DepricatedView;
import com.intuit.tank.config.Owner;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.qualifier.Current;
import com.intuit.tank.vm.common.TankConstants;

import static org.picketlink.idm.model.basic.BasicModel.*;

/**
 * AdminChecker
 * 
 * @author dangleton
 * 
 */
public class InternalSecurity {
	
    @Inject 
    private IdentityManager identityManager;
    

    @Inject
    private RelationshipManager relationshipManager;

    @Secures
    @Admin
    public boolean adminChecker(Identity identity) {
    	return hasRole(relationshipManager, identity.getAccount(), getRole(identityManager, TankConstants.TANK_GROUP_ADMIN));
    }

    @Secures
    @TsLoggedIn
    public boolean loginChecker(Identity identity) {
        return identity.isLoggedIn();
    }

    @Secures
    @Owner
    public boolean ownerChecker(Identity identity, @Current OwnableEntity item) {
        return identity.isLoggedIn()
                && (StringUtils.isEmpty(item.getCreator()) || identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName().equals(item.getCreator()));
    }
    
    @Secures
    @DepricatedView
    public boolean depricate(Identity identity) {
        return false;
    }
    

}
