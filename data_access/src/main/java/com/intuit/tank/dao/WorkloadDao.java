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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class WorkloadDao extends BaseDao<Workload> {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(WorkloadDao.class);

    /**
     * @param entityClass
     */
    public WorkloadDao() {
        super();
        setReloadEntities(true);
    }
    
    
    /**
     * This is an override of the BaseEntity to initiate eager loading when needed.
     * 
     * @param id
     *            the primary key
     * @return the entity or null
     */
    @Nullable
    @Override
    public Workload findById(@Nonnull Integer id) {
    	Workload workload = null;
    	try {
//   		begin();
    		workload = getEntityManager().find(Workload.class, id);
    		if(workload != null) {
    			workload.getJobConfiguration();
    			workload.getTestPlans();
    		}
//    		commit();
		} finally {
			cleanup();
		}
		return workload;
    }

    public Workload loadScriptsForWorkload(Workload workload) {
        ScriptDao sd = new ScriptDao();
        for (TestPlan testPlan : workload.getTestPlans()) {
            List<ScriptGroup> scriptGroups = testPlan.getScriptGroups();
            for (ScriptGroup scriptGroup : scriptGroups) {
                List<ScriptGroupStep> scriptGroupSteps = scriptGroup.getScriptGroupSteps();
                for (ScriptGroupStep scriptGroupStep : scriptGroupSteps) {
                    Script script = scriptGroupStep.getScript();
                    sd.loadScriptSteps(script);
                }
            }
        }
        return workload;
    }

}
