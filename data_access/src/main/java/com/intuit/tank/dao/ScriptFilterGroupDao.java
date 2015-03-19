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

import java.util.Arrays;
import java.util.List;

import com.intuit.tank.project.ScriptFilterGroup;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class ScriptFilterGroupDao extends BaseDao<ScriptFilterGroup> {

    /**
     * @param entityClass
     */
    public ScriptFilterGroupDao() {
        super();
    }

    public List<ScriptFilterGroup> getScriptFilterGroupForFilter(Integer... ids) {

        String q = "select sfg FROM ScriptFilterGroup sfg JOIN sfg.filters f where f.id in (:ids)";
        NamedParameter np = new NamedParameter("", "ids", Arrays.asList(ids));
        return listWithJQL(q, np);
    }

}
