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
import java.util.stream.Stream;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import com.intuit.tank.test.TestGroups;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import com.intuit.tank.dao.ScriptGroupDao;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import org.junit.jupiter.api.BeforeEach;
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
public class ScriptGroupDaoTest {

    private ScriptGroupDao dao;

    static Stream<Arguments> validations() {
        return Stream.of(
                Arguments.of(ScriptGroup.builderFrom(DaoTestUtil.createScriptGroup(10))
                        .name(DaoTestUtil.generateStringOfLength(256)).build(), "name",
                "length must be between" ) );

    }

    @BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new ScriptGroupDao();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
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
            assertNotEquals(persisted.getScriptGroupSteps().get(0), originalOrder.get(0));
            assertEquals(persisted.getScriptGroupSteps().get(0), originalOrder.get(2));
            assertEquals(persisted.getScriptGroupSteps().get(1), originalOrder.get(0));
            assertEquals(persisted.getScriptGroupSteps().get(2), originalOrder.get(1));

            originalOrder = new ArrayList<ScriptGroupStep>(persisted.getScriptGroupSteps());
            persisted.getScriptGroupSteps().remove(2);
            persisted.getScriptGroupSteps().remove(0);
            persisted = dao.saveOrUpdate(persisted);
            persisted = dao.findById(id);
            assertEquals(persisted.getScriptGroupSteps().get(0), originalOrder.get(1));
            assertEquals(1, persisted.getScriptGroupSteps().size());

        } finally {
            // delete it
            dao.delete(id);
        }

    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("validations")
    public void testValidation(ScriptGroup entity, String property, String messageContains) throws Exception {
        try {
            dao.saveOrUpdate(entity);
            fail("Should have failed validation.");
        } catch (ConstraintViolationException e) {
            // expected validation
            DaoTestUtil.checkConstraintViolation(e, property, messageContains);
        } catch (PersistenceException e) {
            assertTrue(e.getCause().getCause().getMessage().startsWith("Value too long for column "));
        }
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
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

    private void validateScriptGroup(ScriptGroup entity1, ScriptGroup entity2, boolean checkCreateAttributes) {
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
        assertEquals(entity1.getLoop(), entity2.getLoop());
        assertEquals(entity1.getScriptGroupSteps().size(), entity2.getScriptGroupSteps().size());
    }

}
