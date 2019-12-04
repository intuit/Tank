/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.admin;

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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.wrapper.EntityVersionLoader;

/**
 * ProjectModifiedObserver
 * 
 * @author dangleton
 * 
 */
@Dependent
public class UserLoader extends EntityVersionLoader<User, ModifiedUserMessage> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    UserDao userDao;

    /**
     * @inheritDoc
     */
    @Override
    protected List<User> getEntities() {
        return userDao.findAll();
    }

}
