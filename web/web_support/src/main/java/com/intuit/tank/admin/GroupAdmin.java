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
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.PropertyComparer;
import com.intuit.tank.config.Admin;
import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * UserAdmin
 * 
 * @author dangleton
 * 
 */
@ConversationScoped
@Named
public class GroupAdmin implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SelectableWrapper<Group>> groups;
    
    @Inject
    private Conversation conversation;

    @SuppressWarnings("unused")
    private Group current;

    public void begin() {
    	conversation.begin();
    }

    /**
     * @return the users
     */
    public List<SelectableWrapper<Group>> getGroups() {
        if (groups == null) {
            List<Group> grp = new GroupDao().findAll();
            groups = new ArrayList<SelectableWrapper<Group>>(grp.size());
            grp.sort(new PropertyComparer<Group>(Group.PROPERTY_NAME));
            for (Group group : grp) {
                groups.add(new SelectableWrapper<Group>(group));
            }
        }
        return groups;
    }

    /**
     * 
     * @param group
     */
    public void select(Group group) {
        current = group;
    }

    /**
     * 
     * @param group
     */
    @Admin
    public void delete(Group group) {
        new GroupDao().delete(group);
    }

}
