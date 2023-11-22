/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank;

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

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.admin.UserAdmin;
import com.intuit.tank.auth.Security;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.view.filter.ViewFilterType;

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@Named
@SessionScoped
public class OwnerUtilBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    UserAdmin userAdmin;
    
    @Inject
    Security security;
    
    @Inject
    TankSecurityContext securityContext;

    public boolean isOwnable(Object obj) {
        return obj instanceof OwnableEntity;
    }

    public List<com.intuit.tank.project.User> getOwnerList() {
        return userAdmin.getEntityList(ViewFilterType.ALL);
    }

    public boolean canChangeOwner(Object obj) {
        boolean retVal = true;
        if (isOwnable(obj)) {
            OwnableEntity entity = (OwnableEntity) obj;
            if ((entity.getCreator()).isEmpty()) {
                entity.setCreator(securityContext.getCallerPrincipal().getName());
            }
            if (security.isOwner((OwnableEntity) entity) || security.isAdmin()) {
                retVal = true;
            } else {
                retVal = false;
            }
        } else {
            retVal = false;
        }
        return retVal;
    }

}
