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
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
public class WorkloadDaoTest {

    private WorkloadDao dao;

    @DataProvider(name = "validations")
    private Object[][] violationData() {
        return new Object[][] {
                {
                        Workload.builderFrom(DaoTestUtil.createWorkload())
                                .name(DaoTestUtil.generateStringOfLength(256)).build(), "name",
                        "length must be between" },
                { Workload.builderFrom(DaoTestUtil.createWorkload()).name(null).build(), "name",
                        "may not be empty" } };

    }

    @BeforeClass
    public void configure() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        dao = new WorkloadDao();
    }

    @Test(groups = { "functional" })
    public void testScriptGroupOrder() throws Exception {
        Workload workload = DaoTestUtil.createWorkload();
        TestPlan tp = TestPlan.builder().name("default test plan").usersPercentage(100).build();
        workload.addTestPlan(tp);
        tp.addScriptGroup(DaoTestUtil.createScriptGroup(10));
        tp.addScriptGroup(DaoTestUtil.createScriptGroup(10));
        tp.addScriptGroup(DaoTestUtil.createScriptGroup(10));
        Workload persistedWorkload = dao.saveOrUpdate(workload);
        Integer id = persistedWorkload.getId();
        try {
            List<ScriptGroup> scriptGroups = persistedWorkload.getTestPlans().get(0).getScriptGroups();
            ArrayList<ScriptGroup> originalOrder = new ArrayList<ScriptGroup>(scriptGroups);
            ScriptGroup removed = scriptGroups.remove(2);
            scriptGroups.add(0, removed);
            persistedWorkload = dao.saveOrUpdate(persistedWorkload);
            persistedWorkload = dao.findById(id);
            Assert.assertFalse(persistedWorkload.getTestPlans().get(0).getScriptGroups().get(0)
                    .equals(originalOrder.get(0)));
            Assert.assertTrue(persistedWorkload.getTestPlans().get(0).getScriptGroups().get(0)
                    .equals(originalOrder.get(2)));
            Assert.assertTrue(persistedWorkload.getTestPlans().get(0).getScriptGroups().get(1)
                    .equals(originalOrder.get(0)));
            Assert.assertTrue(persistedWorkload.getTestPlans().get(0).getScriptGroups().get(2)
                    .equals(originalOrder.get(1)));

            originalOrder = new ArrayList<ScriptGroup>(persistedWorkload.getTestPlans().get(0).getScriptGroups());
            persistedWorkload.getTestPlans().get(0).getScriptGroups().remove(2);
            persistedWorkload.getTestPlans().get(0).getScriptGroups().remove(0);
            persistedWorkload = dao.saveOrUpdate(persistedWorkload);
            persistedWorkload = dao.findById(id);
            Assert.assertTrue(persistedWorkload.getTestPlans().get(0).getScriptGroups().get(0)
                    .equals(originalOrder.get(1)));
            Assert.assertEquals(1, persistedWorkload.getTestPlans().size());

        } finally {
            // delete it
            dao.delete(id);
        }

    }

    @Test(groups = { "functional" }, dataProvider = "validations")
    public void testValidation(Workload workload, String property, String messageContains) throws Exception {
        try {
            dao.saveOrUpdate(workload);
            Assert.fail("Should have failed validation.");
        } catch (ConstraintViolationException e) {
            // expected validation
            DaoTestUtil.checkConstraintViolation(e, property, messageContains);
        }
    }

    @Test(groups = { "functional" })
    public void testBasicCreateUpdateDelete() throws Exception {
        List<Workload> all = dao.findAll();
        int originalSize = all.size();
        Workload workload = DaoTestUtil.createWorkload();
        TestPlan tp = TestPlan.builder().name("default test plan").usersPercentage(100).build();
        workload.addTestPlan(tp);
        tp.addScriptGroup(DaoTestUtil.createScriptGroup(10));
        Workload persistedWorkload = dao.saveOrUpdate(workload);

        validateWorkload(workload, persistedWorkload, false);
        workload = dao.findById(persistedWorkload.getId());
        workload.setName("New Workload Name");
        persistedWorkload = dao.saveOrUpdate(workload);
        validateWorkload(workload, persistedWorkload, true);

        all = dao.findAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(originalSize + 1, all.size());

        all = dao.findAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(originalSize + 1, all.size());

        // delete it
        dao.delete(persistedWorkload);
        workload = dao.findById(workload.getId());
        Assert.assertNull(workload);
        all = dao.findAll();
        Assert.assertEquals(originalSize, all.size());
    }

    private void validateWorkload(Workload workload1, Workload workload2, boolean checkCreateAttributes) {
        if (checkCreateAttributes) {
            Assert.assertEquals(workload1.getId(), workload2.getId());
            Assert.assertEquals(workload1.getCreated(), workload2.getCreated());
            Assert.assertNotSame(workload1.getModified(), workload2.getModified());
        } else {
            Assert.assertNotNull(workload2.getId());
            Assert.assertNotNull(workload2.getCreated());
            Assert.assertNotNull(workload2.getModified());
        }
        Assert.assertEquals(workload1.getName(), workload2.getName());

        Assert.assertEquals(workload1.getTestPlans().size(), workload2.getTestPlans().size());
    }

}
