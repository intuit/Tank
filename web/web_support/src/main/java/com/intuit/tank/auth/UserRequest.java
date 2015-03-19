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

import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.validation.InputField;
import org.jboss.seam.international.status.Messages;

import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * RequestUser
 * 
 * @author dangleton
 * 
 */
@Named
public class UserRequest {

    private User user = new User();

    @Inject
    @InputField("passwordConfirm")
    private String passwordConfirm;

    @Inject
    private Messages messages;

    @Inject
    @Modified
    private Event<User> userEvent;

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the passwordConfirm
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * @param passwordConfirm
     *            the passwordConfirm to set
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public void request() {
        if (!user.getPassword().equals(passwordConfirm)) {
            messages.error("Passwords do not match.").targets("password");
        } else {
            UserDao userDao = new UserDao();
            GroupDao groupDao = new GroupDao();
            user.setPassword(PasswordEncoder.encodePassword(passwordConfirm));
            userDao.saveOrUpdate(user);
            Set<String> defaultGroups = new TankConfig().getSecurityConfig().getDefaultGroups();
            for (String groupName : defaultGroups) {
                user.addGroup(groupDao.getOrCreateGroup(groupName));
            }
            user = userDao.saveOrUpdate(user);
            userEvent.fire(user);
        }
    }

}
