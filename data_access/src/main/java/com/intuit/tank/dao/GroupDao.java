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

import com.intuit.tank.project.Group;

/**
 * GroupDao
 * 
 * @author dangleton
 * 
 */
public class GroupDao extends BaseDao<Group> {

    /**
     * @param entityClass
     */
    public GroupDao() {
        super();
    }

    /**
     * finds the group by the name.
     * 
     * @param name
     *            the name to search
     * @return the group or null if no group with the name found.
     */
    public Group findByName(@Nonnull String name) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(Group.PROPERTY_NAME, "name", name);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.EQUALS, prefix, parameter);
        return super.findOneWithJQL(sb, parameter);
    }

    /**
     * @param g
     * @return
     */
    public synchronized Group getOrCreateGroup(String g) {
        Group existing = findByName(g);
        if (existing == null) {
            existing = new Group(g);
            existing = saveOrUpdate(existing);
        }
        return existing;
    }

}
