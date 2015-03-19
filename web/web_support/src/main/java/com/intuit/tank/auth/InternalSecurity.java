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

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;

import com.intuit.tank.config.Admin;
import com.intuit.tank.config.DepricatedView;
import com.intuit.tank.config.Owner;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.qualifier.Current;
import com.intuit.tank.vm.common.TankConstants;

/**
 * AdminChecker
 * 
 * @author dangleton
 * 
 */
public class InternalSecurity {

    @Secures
    @Admin
    public boolean adminChecker(Identity identity) {
        return identity.hasRole(TankConstants.TANK_GROUP_ADMIN, TankConstants.TANK_GROUP_ADMIN,
                TankConstants.TANK_GROUP_TYPE);
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
                && (StringUtils.isEmpty(item.getCreator()) || identity.getUser().getId().equals(item.getCreator()));
    }
    
    @Secures
    @DepricatedView
    public boolean depricate(Identity identity) {
        return false;
    }
    

}
