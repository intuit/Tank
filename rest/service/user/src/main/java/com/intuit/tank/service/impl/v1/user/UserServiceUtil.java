/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.user;

/*
 * #%L
 * User Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.api.model.v1.user.User;
import com.intuit.tank.project.Group;

/**
 * ProjectServiceUtil
 * 
 * @author dangleton
 * 
 */
public class UserServiceUtil {

    private UserServiceUtil() {

    }

    public static User userToTransferObject(com.intuit.tank.project.User p) {
        User ret = new User();
        ret.setName(p.getName());
        ret.setEmail(p.getEmail());
        for (Group g : p.getGroups()) {
            ret.addGroup(g.getName());
        }
        return ret;
    }

}
