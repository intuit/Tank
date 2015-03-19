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

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.tool.hbm2x.StringUtils;
import org.jboss.seam.security.Identity;

import com.intuit.tank.admin.UserAdmin;
import com.intuit.tank.auth.Security;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.project.User;
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
    Identity identity;

    public boolean isOwnable(Object obj) {
        return obj instanceof OwnableEntity;
    }

    public List<User> getOwnerList() {
        return userAdmin.getEntityList(ViewFilterType.ALL);
    }

    public boolean canChangeOwner(Object obj) {
        boolean retVal = true;
        if (isOwnable(obj)) {
            OwnableEntity entity = (OwnableEntity) obj;
            if (StringUtils.isEmpty(entity.getCreator())) {
                entity.setCreator(identity.getUser().getId());
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
