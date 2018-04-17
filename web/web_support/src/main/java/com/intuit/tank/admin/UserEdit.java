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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;
import org.primefaces.model.DualListModel;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.TsConversationManager;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * UserEdit
 * 
 * @author dangleton
 * 
 */
@Named
@ConversationScoped
public class UserEdit implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(UserEdit.class);

    private String password;

    private String passwordConfirm;

    @Inject
    @Modified
    private Event<ModifiedUserMessage> userEvent;

    @Inject
    private Messages messages;
    
    @Inject
    private Conversation conversation;

    private User user;

    private DualListModel<String> selectionModel;

    /**
     * @return the current
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the selectionModel
     */
    public DualListModel<String> getSelectionModel() {
        return selectionModel;
    }

    /**
     * @param selectionModel
     *            the selectionModel to set
     */
    public void setSelectionModel(DualListModel<String> selectionModel) {
        this.selectionModel = selectionModel;
    }
    
    public void generateApiToken() {
        if (user.getApiToken() == null) {
            user.generateApiToken();
            user = new UserDao().saveOrUpdate(user);
        }
    }

    private void initSelectionModel() {
        GroupDao groupDao = new GroupDao();
        selectionModel = new DualListModel<String>();
        Set<Group> groups = user.getGroups();
        if (groups.isEmpty()) {
            Set<String> defaultGroups = new TankConfig().getSecurityConfig().getDefaultGroups();
            for (String s : defaultGroups) {
                Group g = groupDao.getOrCreateGroup(s);
                selectionModel.getTarget().add(g.getName());
            }
        } else {
            for (Group g : groups) {
                selectionModel.getTarget().add(g.getName());
            }
        }
        Set<String> allGroups = new TankConfig().getSecurityConfig().getGroups();
        for (String s : allGroups) {
            if (!selectionModel.getTarget().contains(s)) {
                Group g = groupDao.getOrCreateGroup(s);
                selectionModel.getSource().add(g.getName());
            }
        }

        List<SelectItem> ret = groups.stream().map(g -> new SelectItem(g.getName())).collect(Collectors.toList());
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password.trim();
    }

    /**
     * 
     * @param user
     * @return
     */
    public String edit(User user) {
    	conversation.begin();
        clear();
        this.user = user;
        initSelectionModel();
        return "success";
    }

    /**
     * 
     * @return
     */
    public String newUser() {
    	conversation.begin();
        clear();
        this.user = new User();
        initSelectionModel();
        return "success";
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
        this.passwordConfirm = passwordConfirm.trim();
    }

    /**
     * Saves the user
     * 
     * @return
     */
    public String save() {
        boolean isNew = user.getId() == 0;
        if (!StringUtils.isBlank(password)) {
            if (!password.equals(passwordConfirm)) {
                LOG.warn("Password '" + password + "' does not match '" + passwordConfirm + "'");
                messages.error("Passwords do not match.");
                return null;
            }
            user.setPassword(PasswordEncoder.encodePassword(password));
        } else if (isNew) {
            messages.error("Passwords is Required.");
            return null;
        }
        UserDao userDao = new UserDao();
        user = userDao.saveOrUpdate(user);
        user.getGroups().clear();
        for (String g : selectionModel.getTarget()) {
            user.addGroup(new GroupDao().getOrCreateGroup(g));
        }
        user = userDao.saveOrUpdate(user);
        userEvent.fire(new ModifiedUserMessage(user, this));
        messages.info("User " + user.getName() + " has been " + (isNew ? "created" : "modified") + ".");
        conversation.end();
        clear();
        return "success";
    }

    /**
     * 
     */
    private void clear() {
        user = null;
        password = null;
        passwordConfirm = null;
        selectionModel = null;
    }

    public String cancel() {
    	conversation.end();
        clear();
        return "success";
    }
}
