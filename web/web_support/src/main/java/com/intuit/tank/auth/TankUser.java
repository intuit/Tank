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

import com.intuit.tank.project.User;

import java.io.Serializable;

/**
 * TankUser
 * 
 * @author dangleton
 * 
 */
public class TankUser extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    private com.intuit.tank.project.User userEntity;

    /**
     * @param userEntity
     */
    public TankUser(User userEntity) {
        this.userEntity = userEntity;
    }

    /**
     * @inheritDoc
     */
    public String getKey() {
        return Integer.toString(userEntity.getId());
    }


    /**
     * @return the userEntity
     */
    public com.intuit.tank.project.User getUserEntity() {
        return userEntity;
    }

    /**
     * @param userEntity
     *            the userEntity to set
     */
    public void setUserEntity(com.intuit.tank.project.User userEntity) {
        this.userEntity = userEntity;
    }

}
