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

import javax.persistence.EntityManager;

import com.intuit.tank.project.ScriptGroup;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class ScriptGroupDao extends BaseDao<ScriptGroup> {

    /**
     * @param entityClass
     */
    public ScriptGroupDao() {
        super();
    }

    public EntityManager getEM() {
        return getEntityManager();
    }

}
