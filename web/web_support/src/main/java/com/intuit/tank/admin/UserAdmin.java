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
import java.util.List;

import javax.enterprise.event.Event;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.User;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;
import com.intuit.tank.wrapper.VersionContainer;

/**
 * UserAdmin
 * 
 * @author dangleton
 * 
 */
@Named
@ViewScoped
public class UserAdmin extends SelectableBean<User> implements Serializable, Multiselectable<User> {

    private static final long serialVersionUID = 1L;

    private int version;

    @Inject
    private UserLoader userLoader;

    private SelectableWrapper<User> selectedUser;

    @Inject
    @Modified
    private Event<ModifiedUserMessage> userEvent;

    @Inject
    @Deleted
    private Event<Preferences> deletedPrefsEvent;

    /**
     * @return the users
     */
    public List<User> getEntityList(ViewFilterType viewFilter) {
        VersionContainer<User> container = userLoader.getVersionContainer();
        this.version = container.getVersion();
        return container.getEntities();
    }

    public void resetPreferences(User user) {
        PreferencesDao dao = new PreferencesDao();
        Preferences prefs = dao.getForOwner(user.getName());
        if (prefs != null) {
            dao.delete(prefs);
            deletedPrefsEvent.fire(prefs);
        }
    }

    /**
     * @return the selectedUser
     */
    public SelectableWrapper<User> getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser
     *            the selectedUser to set
     */
    public void setSelectedUser(SelectableWrapper<User> selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public boolean isCurrent() {
        return userLoader.isCurrent(version);
    }

    /**
     * 
     * @param user
     */
    public void delete(User user) {
        new UserDao().delete(user);
        userEvent.fire(new ModifiedUserMessage(user, this));
    }

}
