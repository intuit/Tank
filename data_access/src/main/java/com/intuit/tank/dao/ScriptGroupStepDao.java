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

import java.util.List;

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroupStep;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class ScriptGroupStepDao extends BaseDao<ScriptGroupStep> {

    /**
     * @param entityClass
     */
    public ScriptGroupStepDao() {
        super();
    }

    /**
     * 
     * @param script
     * @return
     */
    public List<ScriptGroupStep> getScriptGroupsForScript(Script script) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(ScriptGroupStep.PROPERTY_SCRIPT, "s", script);
        return listWithJQL(buildQlSelect(prefix) + startWhere() + buildWhereClause(Operation.IN, prefix, parameter), parameter);
    }

}
