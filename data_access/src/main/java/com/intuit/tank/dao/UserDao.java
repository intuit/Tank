/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intuit.tank.project.User;
import com.intuit.tank.vm.common.PasswordEncoder;

/**
 * UserDao
 * 
 * @author dangleton
 * 
 */
public class UserDao extends BaseDao<User> {

    /**
     * @param entityClass
     */
    public UserDao() {
        super();
    }

    /**
     * Authenticats the user with the given credentials.
     * 
     * @param userName
     *            the userName
     * @param password
     *            the raw password
     * @return the user if authentication succeeds, null if not.
     */
    @Nullable
    public User authenticate(@Nonnull String userName, @Nonnull String password) {
        User user = findByUserName(userName);
        User result = null;
        if (user != null) {
            if (PasswordEncoder.validatePassword(password, user.getPassword())) {
                result = user;
            }
        }
        return result;
    }

    /**
     * finds the user by the userName.
     * 
     * @param userName
     *            the name to search
     * @return the user or null if no user with the name found.
     */
    public User findByUserName(@Nonnull String userName) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(User.PROPERTY_NAME, "name", userName);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.EQUALS, prefix, parameter);
        return super.findOneWithJQL(sb, parameter);
    }
    /**
     * finds the user by the apiToken.
     * 
     * @param apiToken
     *            the api token
     * @return the user or null if no user with the apiToken.
     */
    public User findByApiToken(@Nonnull String apiToken) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(User.PROPERTY_TOKEN, "token", apiToken);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.EQUALS, prefix, parameter);
        return super.findOneWithJQL(sb, parameter);
    }

}
