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
import java.util.Calendar;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.view.filter.ViewFilterType;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
public class ScriptDaoTest {

    private ScriptDao dao;

    @DataProvider(name = "validations")
    private Object[][] violationData() {
        return new Object[][] {
                { Script.builderFrom(DaoTestUtil.createScript()).name(DaoTestUtil.generateStringOfLength(256)).build(),
                        "name",
                        "length must be between" },
                { Script.builderFrom(DaoTestUtil.createScript()).name(null).build(), "name", "may not be empty" },
                {
                        Script.builderFrom(DaoTestUtil.createScript())
                                .comments(DaoTestUtil.generateStringOfLength(1025)).build(), "comments",
                        "length must be between" },
                {
                        Script.builderFrom(DaoTestUtil.createScript())
                                .productName(DaoTestUtil.generateStringOfLength(256)).build(), "productName",
                        "length must be between" },

        };

    }

    @BeforeClass
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new ScriptDao();
    }

    @Test(groups = { "functional" })
    public void testChildOrder() throws Exception {
        Script entity = DaoTestUtil.createScript();
        entity.addStep(DaoTestUtil.createScriptStep());
        entity.addStep(DaoTestUtil.createScriptStep());
        entity.addStep(DaoTestUtil.createScriptStep());
        Script persisted = dao.saveOrUpdate(entity);
        Integer id = persisted.getId();
        try {
            List<ScriptStep> children = persisted.getScriptSteps();
            ArrayList<ScriptStep> originalOrder = new ArrayList<ScriptStep>(children);
            ScriptStep removed = children.remove(2);
            children.add(0, removed);
            persisted = dao.saveOrUpdate(persisted);
            persisted = dao.findById(id);
            Assert.assertFalse(persisted.getScriptSteps().get(0).equals(originalOrder.get(0)));
            Assert.assertTrue(persisted.getScriptSteps().get(0).equals(originalOrder.get(2)));
            Assert.assertTrue(persisted.getScriptSteps().get(1).equals(originalOrder.get(0)));
            Assert.assertTrue(persisted.getScriptSteps().get(2).equals(originalOrder.get(1)));

            originalOrder = new ArrayList<ScriptStep>(persisted.getScriptSteps());
            persisted.getScriptSteps().remove(2);
            persisted.getScriptSteps().remove(0);
            persisted = dao.saveOrUpdate(persisted);
            persisted = dao.findById(id);
            Assert.assertTrue(persisted.getScriptSteps().get(0).equals(originalOrder.get(1)));
            Assert.assertEquals(1, persisted.getScriptSteps().size());

        } finally {
            // delete it
            dao.delete(id);
        }

    }

    @Test(groups = { "functional" })
    public void testFilter() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Script first = DaoTestUtil.createScript();
        first = dao.saveOrUpdate(first);

        Script third = DaoTestUtil.createScript();
        calendar.add(Calendar.DAY_OF_YEAR, -10); // 10 days ago
        third.setForceCreateDate(calendar.getTime());
        third = dao.saveOrUpdate(third);

        Script second = DaoTestUtil.createScript();
        calendar.add(Calendar.DAY_OF_YEAR, 7); // 3 days ago
        second.setForceCreateDate(calendar.getTime());
        second = dao.saveOrUpdate(second);

        Script fourth = DaoTestUtil.createScript();
        calendar.add(Calendar.MONTH, -1); // 1 month and 3 days ago
        fourth.setForceCreateDate(calendar.getTime());
        fourth = dao.saveOrUpdate(fourth);

        List<Script> list = dao.findAll();
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(first.getId(), list.get(0).getId());
        Assert.assertEquals(second.getId(), list.get(2).getId());
        Assert.assertEquals(third.getId(), list.get(1).getId());
        Assert.assertEquals(fourth.getId(), list.get(3).getId());

        // run our queries
        list = dao.findFiltered(ViewFilterType.LAST_TWO_WEEKS);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(first.getId(), list.get(0).getId());
        Assert.assertEquals(second.getId(), list.get(1).getId());

        list = dao.findFiltered(ViewFilterType.ALL);
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(first.getId(), list.get(0).getId());
        Assert.assertEquals(second.getId(), list.get(1).getId());
        Assert.assertEquals(third.getId(), list.get(2).getId());
        Assert.assertEquals(fourth.getId(), list.get(3).getId());
    }

    @Test(groups = { "functional" }, dataProvider = "validations")
    public void testValidation(Script entity, String property, String messageContains) throws Exception {
        try {
            dao.saveOrUpdate(entity);
            Assert.fail("Should have failed validation.");
        } catch (ConstraintViolationException e) {
            // expected validation
            DaoTestUtil.checkConstraintViolation(e, property, messageContains);
        }
    }

    @Test(groups = { "functional" })
    public void testBasicCreateUpdateDelete() throws Exception {
        List<Script> all = dao.findAll();
        int originalSize = all.size();
        Script entity = DaoTestUtil.createScript();
        entity.addStep(DaoTestUtil.createScriptStep());
        Script persisted = dao.saveOrUpdate(entity);

        validate(entity, persisted, false);
        entity = dao.findById(persisted.getId());
        entity = dao.loadScriptSteps(entity);
        entity.setName("New Workload Name");
        persisted = dao.saveOrUpdate(entity);
        validate(entity, persisted, true);

        all = dao.findAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(originalSize + 1, all.size());

        all = dao.findAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(originalSize + 1, all.size());

        // delete it
        dao.delete(persisted);
        entity = dao.findById(entity.getId());
        Assert.assertNull(entity);
        all = dao.findAll();
        Assert.assertEquals(originalSize, all.size());
    }

    private void validate(Script entity1, Script entity2, boolean checkCreateAttributes) {
        if (checkCreateAttributes) {
            Assert.assertEquals(entity1.getId(), entity2.getId());
            Assert.assertEquals(entity1.getCreated(), entity2.getCreated());
            Assert.assertNotSame(entity1.getModified(), entity2.getModified());
        } else {
            Assert.assertNotNull(entity2.getId());
            Assert.assertNotNull(entity2.getCreated());
            Assert.assertNotNull(entity2.getModified());
        }
        Assert.assertEquals(entity1.getName(), entity2.getName());
        Assert.assertEquals(entity1.getProductName(), entity2.getProductName());
        Assert.assertEquals(entity1.getComments(), entity2.getComments());
        Assert.assertEquals(entity1.getCreator(), entity2.getCreator());
        Assert.assertEquals(entity1.getRuntime(), entity2.getRuntime());
        // Assert.assertEquals(entity1.getScriptSteps().size(), entity2.getScriptSteps().size());
    }

}
