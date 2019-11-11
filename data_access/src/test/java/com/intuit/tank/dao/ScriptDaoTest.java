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
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import com.intuit.tank.test.TestGroups;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.view.filter.ViewFilterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
public class ScriptDaoTest {

    private ScriptDao dao;

    static Stream<Arguments> validations() {
        return Stream.of(
                Arguments.of(Script.builderFrom(DaoTestUtil.createScript()).name(DaoTestUtil.generateStringOfLength(256)).build(),
                        "name",
                        "length must be between" ),
                Arguments.of( Script.builderFrom(DaoTestUtil.createScript()).name(null).build(), "name", "may not be empty" ),
                Arguments.of(
                        Script.builderFrom(DaoTestUtil.createScript())
                                .comments(DaoTestUtil.generateStringOfLength(1025)).build(), "comments",
                        "length must be between" ),
                Arguments.of(
                        Script.builderFrom(DaoTestUtil.createScript())
                                .productName(DaoTestUtil.generateStringOfLength(256)).build(), "productName",
                        "length must be between" )
        );

    }

    @BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new ScriptDao();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @Disabled
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
            assertFalse(persisted.getScriptSteps().get(0).equals(originalOrder.get(0)));
            assertTrue(persisted.getScriptSteps().get(0).equals(originalOrder.get(2)));
            assertTrue(persisted.getScriptSteps().get(1).equals(originalOrder.get(0)));
            assertTrue(persisted.getScriptSteps().get(2).equals(originalOrder.get(1)));

            originalOrder = new ArrayList<ScriptStep>(persisted.getScriptSteps());
            persisted.getScriptSteps().remove(2);
            persisted.getScriptSteps().remove(0);
            persisted = dao.saveOrUpdate(persisted);
            persisted = dao.findById(id);
            assertTrue(persisted.getScriptSteps().get(0).equals(originalOrder.get(1)));
            assertEquals(1, persisted.getScriptSteps().size());

        } finally {
            // delete it
            dao.delete(id);
        }

    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
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
        assertEquals(4, list.size());
        assertEquals(first.getId(), list.get(0).getId());
        assertEquals(second.getId(), list.get(2).getId());
        assertEquals(third.getId(), list.get(1).getId());
        assertEquals(fourth.getId(), list.get(3).getId());

        // run our queries
        list = dao.findFiltered(ViewFilterType.LAST_TWO_WEEKS);
        assertEquals(3, list.size());
        assertEquals(first.getId(), list.get(0).getId());
        assertEquals(second.getId(), list.get(1).getId());

        list = dao.findFiltered(ViewFilterType.ALL);
        assertEquals(4, list.size());
        assertEquals(first.getId(), list.get(0).getId());
        assertEquals(second.getId(), list.get(1).getId());
        assertEquals(third.getId(), list.get(2).getId());
        assertEquals(fourth.getId(), list.get(3).getId());
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("validations")
    @Disabled
    public void testValidation(Script entity, String property, String messageContains) throws Exception {
        try {
            dao.saveOrUpdate(entity);
            fail("Should have failed validation.");
        } catch (ConstraintViolationException e) {
            // expected validation
            DaoTestUtil.checkConstraintViolation(e, property, messageContains);
        }
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @Disabled
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
        assertNotNull(all);
        assertEquals(originalSize + 1, all.size());

        all = dao.findAll();
        assertNotNull(all);
        assertEquals(originalSize + 1, all.size());

        // delete it
        dao.delete(persisted);
        entity = dao.findById(entity.getId());
        assertNull(entity);
        all = dao.findAll();
        assertEquals(originalSize, all.size());
    }

    private void validate(Script entity1, Script entity2, boolean checkCreateAttributes) {
        if (checkCreateAttributes) {
            assertEquals(entity1.getId(), entity2.getId());
            assertEquals(entity1.getCreated(), entity2.getCreated());
            assertNotSame(entity1.getModified(), entity2.getModified());
        } else {
            assertNotNull(entity2.getId());
            assertNotNull(entity2.getCreated());
            assertNotNull(entity2.getModified());
        }
        assertEquals(entity1.getName(), entity2.getName());
        assertEquals(entity1.getProductName(), entity2.getProductName());
        assertEquals(entity1.getComments(), entity2.getComments());
        assertEquals(entity1.getCreator(), entity2.getCreator());
        assertEquals(entity1.getRuntime(), entity2.getRuntime());
        // assertEquals(entity1.getScriptSteps().size(), entity2.getScriptSteps().size());
    }

}
