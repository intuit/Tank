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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
public class UpdateScriptGroupsTest {

    private ScriptGroupDao dao;
    private WorkloadDao workloadDao;
    private Map<Integer, Workload> workloadMap = new HashMap<Integer, Workload>();

    @BeforeClass
    public void configure() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        dao = new ScriptGroupDao();
        workloadDao = new WorkloadDao();
    }

    @Test(groups = { "manual" })
    public void upgrade() {
        EntityManager em = dao.getEM();
        Query query = em
                .createNativeQuery("select id, workload_id from script_group where test_plan_id is null order by position");
        @SuppressWarnings("unchecked") List<Object[]> results = query.getResultList();
        for (Object[] set : results) {
            for (Object o : set) {
                Integer id = (Integer) set[0];
                Integer workloadId = (Integer) set[1];
                System.out.print(o);
                System.out.print(", ");
                if (workloadId != null) {
                    updateScriptGroup(id, workloadId);
                } else {
                    deleteScriptGroup(id);
                }
            }
            System.out.print('\n');
        }
        for (Workload w : workloadMap.values()) {
            workloadDao.saveOrUpdate(w);
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
        testPlans.get(0).addScriptGroupAt(scriptGroup, scriptGroup.getPosition());
    }

}
