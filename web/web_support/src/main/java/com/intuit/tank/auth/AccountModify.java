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

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;

import com.intuit.tank.admin.Deleted;
import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.common.PasswordEncoder;

/**
 * RequestUser
 * 
 * @author dangleton
 * 
 */
@Named
@RequestScoped
public class AccountModify implements Serializable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountModify.class);
    private static final long serialVersionUID = 1L;

    @Inject
    private Identity identity;

    private String passwordConfirm;

    private String password;
    private boolean succeeded;
    private User user;

    @Inject
    private Messages messages;

    @Inject
    @Deleted
    private Event<Preferences> deletedPrefsEvent;

    @PostConstruct
    public void init() {
        try {
            if (identity.isLoggedIn()) {
                UserDao userDao = new UserDao();
                user = userDao.findById(Integer.parseInt(identity.getUser().getKey()));
            }
        } catch (Exception e) {
            LOG.error("Error getting user: " + e, e);
        }
    }

    /**
     * @return the passwordConfirm
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * @return the succeeded
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * @param passwordConfirm
     *            the passwordConfirm to set
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * 
     * @param user
     */
    public void resetPreferences() {
        try {
            PreferencesDao dao = new PreferencesDao();
            Preferences prefs = dao.getForOwner(user.getName());
            if (prefs != null) {
                dao.delete(prefs);
                deletedPrefsEvent.fire(prefs);
            }
            messages.info("Preferences have been reset.");
        } catch (HibernateException e) {
            LOG.error("Error resetting preferences: " + e, e);
            messages.error("Error resetting preferences: " + e);
        }
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * 
     */
    public void generateApiToken() {
        if (user.getApiToken() == null) {
            user.generateApiToken();
            user = new UserDao().saveOrUpdate(user);
        }
    }

    public String save() {
        if (user == null) {
            messages.error("Cannot find user. Please log in again.");
        } else {
            if (StringUtils.isNotBlank(password)) {
                messages.error("Password is required.");
                if (!password.equals(passwordConfirm)) {
                    messages.error("Passwords do not match.");
                    return null;
                } else {
                    user.setPassword(PasswordEncoder.encodePassword(passwordConfirm));
                }
            }
            try {
                UserDao userDao = new UserDao();
                userDao.saveOrUpdate(user);
                succeeded = true;
                messages.info("Your account has been updated.");
            } catch (HibernateException e) {
                LOG.error("Error saving user: " + e, e);
            }
        }

        return null;
    }

}
