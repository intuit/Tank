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

import com.intuit.tank.dao.ScriptGroupDao;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
public class ScriptGroupDaoTest {

    private ScriptGroupDao dao;

    @SuppressWarnings("unused")
    @DataProvider(name = "validations")
    private Object[][] violationData() {
        return new Object[][] { {
                ScriptGroup.builderFrom(DaoTestUtil.createScriptGroup(10))
                        .name(DaoTestUtil.generateStringOfLength(256)).build(), "name",
                "length must be between" } };

    }

    @BeforeClass
    public void configure() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        dao = new ScriptGroupDao();
    }

    @Test(groups = { "functional" })
    public void testWorkloadOrder() throws Exception {
        ScriptGroup group = DaoTestUtil.createScriptGroup(10);
        group.addScriptGroupStep(DaoTestUtil.createScriptGroupStep(10));
        group.addScriptGroupStep(DaoTestUtil.createScriptGroupStep(10));
        group.addScriptGroupStep(DaoTestUtil.createScriptGroupStep(10));
        ScriptGroup persisted = dao.saveOrUpdate(group);
        Integer id = persisted.getId();
        try {
            List<ScriptGroupStep> children = persisted.getScriptGroupSteps();
            ArrayList<ScriptGroupStep> originalOrder = new ArrayList<ScriptGroupStep>(children);
            ScriptGroupStep removed = children.remove(2);
            children.add(0, removed);
            persisted = dao.saveOrUpdate(persisted);
            persisted = dao.findById(id);
            Assert.assertFalse(persisted.getScriptGroupSteps().get(0).equals(originalOrder.get(0)));
            Assert.assertTrue(persisted.getScriptGroupSteps().get(0).equals(originalOrder.get(2)));
            Assert.assertTrue(persisted.getScriptGroupSteps().get(1).equals(originalOrder.get(0)));
            Assert.assertTrue(persisted.getScriptGroupSteps().get(2).equals(originalOrder.get(1)));

            originalOrder = new ArrayList<ScriptGroupStep>(persisted.getScriptGroupSteps());
            persisted.getScriptGroupSteps().remove(2);
            persisted.getScriptGroupSteps().remove(0);
            persisted = dao.saveOrUpdate(persisted);
            persisted = dao.findById(id);
            Assert.assertTrue(persisted.getScriptGroupSteps().get(0).equals(originalOrder.get(1)));
            Assert.assertEquals(1, persisted.getScriptGroupSteps().size());

        } finally {
            // delete it
            dao.delete(id);
        }

    }

    @Test(groups = { "functional" }, dataProvider = "validations")
    public void testValidation(ScriptGroup entity, String property, String messageContains) throws Exception {
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
        List<ScriptGroup> all = dao.findAll();
        int originalSize = all.size();
        ScriptGroup entity = DaoTestUtil.createScriptGroup(10);
        entity.addScriptGroupStep(DaoTestUtil.createScriptGroupStep(10));
        ScriptGroup persisted = dao.saveOrUpdate(entity);

        validateScriptGroup(entity, persisted, false);
        entity = dao.findById(persisted.getId());
        entity.setName("New Name");
        persisted = dao.saveOrUpdate(entity);
        validateScriptGroup(entity, persisted, true);

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

    private void validateScriptGroup(ScriptGroup entity1, ScriptGroup entity2, boolean checkCreateAttributes) {
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
        Assert.assertEquals(entity1.getLoop(), entity2.getLoop());
        Assert.assertEquals(entity1.getScriptGroupSteps().size(), entity2.getScriptGroupSteps().size());
    }

}
