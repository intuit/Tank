/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service;

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

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.seam.servlet.WebApplication;
import org.jboss.seam.servlet.event.Initialized;

import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.settings.DefaultUser;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * InitializeEnvironment
 * 
 * @author dangleton
 * 
 */
@Named
public class InitializeEnvironment {
    private static final Logger LOG = Logger.getLogger(InitializeEnvironment.class);

    @Inject
    private TankConfig tankConfig;

    // @Inject
    // private HttpConversationContext conversationContext;

    public String initialize(@Observes @Initialized WebApplication webapp) {
        // conversationContext.setDefaultTimeout(60 * 60 * 1000);//one hour
        return initData();
    }

    public String initData() {
        StringBuilder sb = new StringBuilder();
        createDefaultGroups(sb);
        createDefaultUsers(sb);
        return sb.toString();
    }

    /**
     * @param sb
     * @return
     */
    private void createDefaultUsers(StringBuilder sb) {
        UserDao dao = new UserDao();
        GroupDao groupDao = new GroupDao();
        for (DefaultUser newUser : tankConfig.getSecurityConfig().getDefaultUsers()) {
            if (dao.findByUserName(newUser.getName()) == null) {
                try {
                    User user = new User();
                    user.setName(newUser.getName());
                    user.setEmail(newUser.getEmail());
                    user.setPassword(PasswordEncoder.encodePassword(newUser.getPassword()));
                    user = dao.saveOrUpdate(user);
                    for (String g : newUser.getGroups()) {
                        Group group = groupDao.getOrCreateGroup(g);
                        user.addGroup(group);
                    }
                    for (String defaultGroup : tankConfig.getSecurityConfig().getDefaultGroups()) {
                        user.addGroup(groupDao.getOrCreateGroup(defaultGroup));
                    }
                    dao.saveOrUpdate(user);
                    LOG.info("Created user " + user.getName());
                    sb.append("Created user " + user.getName() + "<br/>");
                } catch (Exception e) {
                    LOG.info("Error creating user: " + e, e);
                }
            }
        }
    }

    /**
     * @param sb
     * @return
     */
    private void createDefaultGroups(StringBuilder sb) {
        GroupDao groupDao = new GroupDao();
        for (String g : tankConfig.getSecurityConfig().getGroups()) {
            try {
                Group group = groupDao.findByName(g);
                if (group == null) {
                    groupDao.saveOrUpdate(new Group(g));
                    sb.append("Created Group " + g + "<br/>");
                    LOG.info("Created Group " + g);
                }
            } catch (Exception e) {
                LOG.error("Error creating default Group: " + e, e);
            }
        }

    }
}
