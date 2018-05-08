/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.intuit.tank.dao.ScriptGroupDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
@Named
public class UpdateScriptGroups {

    private ScriptGroupDao dao = new ScriptGroupDao();
    private WorkloadDao workloadDao = new WorkloadDao();
    private Map<Integer, Workload> workloadMap = new HashMap<Integer, Workload>();
    private Map<Integer, List<ScriptGroup>> scriptGroupMap = new HashMap<Integer, List<ScriptGroup>>();

    private static boolean running = false;

    public boolean isDisabled() {
        return running;
    }

    public synchronized void upgrade() {
        if (!running) {
            // running = true;
            EntityManager em = dao.getEM();
            Query query = em
                    .createNativeQuery("select id, workload_id from script_group where test_plan_id is null order by workload_id, position");
            @SuppressWarnings("unchecked") List<Object[]> results = query.getResultList();
            for (Object[] set : results) {
                Integer id = (Integer) set[0];
                Integer workloadId = (Integer) set[1];
                if (workloadId != null) {
                    updateScriptGroup(id, workloadId);
                } else {
                    deleteScriptGroup(id);
                }
            }
            for (Workload w : workloadMap.values()) {
                workloadDao.saveOrUpdate(w);
            }
        }
    }

    private void deleteScriptGroup(Integer id) {
        dao.delete(id);

    }

    private void updateScriptGroup(Integer id, Integer workloadId) {
        Workload workload = workloadMap.get(workloadId);
        if (workload == null) {
            workload = workloadDao.findById(workloadId);
            if (workload == null) {
                deleteScriptGroup(id);
                return;
            }
            workloadMap.put(workloadId, workload);
        }
        List<TestPlan> testPlans = workload.getTestPlans();

        if (testPlans.isEmpty()) {
            // create a testPlan
            TestPlan plan = TestPlan.builder().name("Main").usersPercentage(100).build();
            workload.addTestPlan(plan);
        }
        ScriptGroup scriptGroup = dao.findById(id);
        testPlans.get(0).addScriptGroup(copyGroup(scriptGroup));
        deleteScriptGroup(id);
    }

    private ScriptGroup copyGroup(ScriptGroup copyFrom) {
        ScriptGroup sg = ScriptGroup.builder().name(copyFrom.getName()).loop(copyFrom.getLoop()).build();
        for (ScriptGroupStep step : copyFrom.getScriptGroupSteps()) {
            sg.addScriptGroupStep(copyStep(step));
        }
        return sg;
    }

    private ScriptGroupStep copyStep(ScriptGroupStep copyFrom) {
        return ScriptGroupStep.builder().loop(copyFrom.getLoop()).script(copyFrom.getScript()).build();
    }

}
