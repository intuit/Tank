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

import jakarta.enterprise.event.Event;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.User;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.Messages;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;
import com.intuit.tank.wrapper.VersionContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOG = LogManager.getLogger(UserAdmin.class);

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

    @Inject
    private Messages messages;

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
     * Checks if user owns any resources and returns detailed information
     * @param userName the user name to check
     * @return resources owned by the user
     */
    private java.util.Map<String, java.util.List<String>> getUserOwnedResources(String userName) {
        java.util.Map<String, java.util.List<String>> ownedResources = new java.util.HashMap<>();

        // The main check is for projects since they're the top-level resource
        com.intuit.tank.dao.ProjectDao projectDao = new com.intuit.tank.dao.ProjectDao();
        java.util.List<com.intuit.tank.project.Project> projects = projectDao.listForOwner(userName);
        if (!projects.isEmpty()) {
            java.util.List<String> projectNames = projects.stream()
                .map(com.intuit.tank.project.Project::getName)
                .collect(java.util.stream.Collectors.toList());
            ownedResources.put("projects", projectNames);
        }

        return ownedResources;
    }

    /**
     * Deletes a user by anonymizing their personal data while preserving referential integrity.
     * The user's name becomes "deleted_user_[id]", email becomes "deleted_users@deleted.com",
     * and all resources they own have their creator field updated to the anonymized name.
     * 
     * @param user the user to delete/anonymize
     */
    public void delete(User user) {
        try {
            java.util.Map<String, java.util.List<String>> ownedResources = getUserOwnedResources(user.getName());
            if (!ownedResources.isEmpty()) {

                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Cannot delete user '").append(user.getName()).append("' because they own the following resources:  ");

                for (java.util.Map.Entry<String, java.util.List<String>> entry : ownedResources.entrySet()) {
                    String resourceType = entry.getKey();
                    java.util.List<String> resourceNames = entry.getValue();
                    errorMsg.append("> ").append(resourceType).append(": ");
                    errorMsg.append(String.join(", ", resourceNames)).append("  ");
                }

                errorMsg.append("Please reassign or delete those resources first.");
                messages.error(errorMsg.toString());
                LOG.warn("User {} owns resources and cannot be deleted: {}", user.getName(), ownedResources);
                return;
            }

            // Use UserDao's anonymization method instead of hard delete
            // This preserves referential integrity while removing user data
            UserDao userDao = new UserDao();
            long result = userDao.deleteUserData(user.getName());

            if (result > 0) {
                userEvent.fire(new ModifiedUserMessage(user, this));
                messages.info("User " + user.getName() + " has been deleted successfully.");
            } else {
                messages.error("Failed to delete/anonymize user '" + user.getName() + "'. User may not exist or an error occurred.");
                return;
            }
        } catch (Exception e) {
            String errorMsg = "Failed to delete/anonymize user '" + user.getName() +
                "'. Error: " + e.getMessage();
            messages.error(errorMsg);
            LOG.error("Failed to delete/anonymize user " + user.getName(), e);
        }
    }

}
