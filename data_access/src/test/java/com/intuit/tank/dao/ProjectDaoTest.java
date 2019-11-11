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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.hibernate.PropertyValueException;

import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.test.TestGroups;
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
public class ProjectDaoTest {

    private ProjectDao dao;

    static Stream<Arguments> validations() {
        return Stream.of(
                Arguments.of(Project.builderFrom(DaoTestUtil.createProject()).name(DaoTestUtil.generateStringOfLength(256))
                                .build(), "name",
                        "length must be between" ),
                Arguments.of( Project.builderFrom(DaoTestUtil.createProject()).name(null).build(), "name", "may not be empty" ),
                Arguments.of( Project.builderFrom(DaoTestUtil.createProject()).scriptDriver(null).build(), "scriptDriver",
                        "may not be null" ),
                Arguments.of(Project.builderFrom(DaoTestUtil.createProject())
                                .productName(DaoTestUtil.generateStringOfLength(257)).build(), "productName",
                        "length must be between" ),
                Arguments.of(Project.builderFrom(DaoTestUtil.createProject())
                                .comments(DaoTestUtil.generateStringOfLength(1025)).build(), "comments",
                        "length must be between")
                );
    }

    @BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new ProjectDao();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testWorkloadOrder() throws Exception {
        Project project = DaoTestUtil.createProject();
        project.addWorkload(DaoTestUtil.createWorkload());
        project.addWorkload(DaoTestUtil.createWorkload());
        project.addWorkload(DaoTestUtil.createWorkload());
        Project persistedProject = dao.saveOrUpdate(project);
        Integer projectId = persistedProject.getId();
        try {
            List<Workload> workloads = persistedProject.getWorkloads();
            ArrayList<Workload> originalOrder = new ArrayList<Workload>(workloads);
            Workload removed = workloads.remove(2);
            workloads.add(0, removed);
            persistedProject = dao.saveOrUpdate(persistedProject);
            persistedProject = dao.findById(projectId);

            assertNotEquals(persistedProject.getWorkloads().get(0), originalOrder.get(0));
            assertEquals(persistedProject.getWorkloads().get(0), originalOrder.get(2));
            assertEquals(persistedProject.getWorkloads().get(1), originalOrder.get(0));
            assertEquals(persistedProject.getWorkloads().get(2), originalOrder.get(1));

            originalOrder = new ArrayList<Workload>(persistedProject.getWorkloads());
            persistedProject.getWorkloads().remove(2);
            persistedProject.getWorkloads().remove(0);
            persistedProject = dao.saveOrUpdate(persistedProject);
            persistedProject = dao.findById(projectId);
            assertEquals(persistedProject.getWorkloads().get(0), originalOrder.get(1));
            assertEquals(1, persistedProject.getWorkloads().size());

        } finally {
            // delete it
            dao.delete(projectId);
        }

    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("validations")
    public void testValidation(Project project, String property, String messageContains) throws Exception {
        try {
            dao.saveOrUpdate(project);
            fail("Should have failed validation.");
        } catch (ConstraintViolationException e) {
            // expected validation
            DaoTestUtil.checkConstraintViolation(e, property, messageContains);
        } catch (PersistenceException e) {
            if (e.getCause() instanceof PropertyValueException) {
                assertTrue(e.getCause().getMessage().startsWith("not-null property references a null or transient value"));
                return;
            }
            assertTrue(e.getCause().getCause().getMessage().startsWith("Value too long for column "));
        }
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @Disabled
    public void testBasicCreateUpdateDelete() throws Exception {
        List<Project> all = dao.findAll();
        int originalSize = all.size();
        Project project = DaoTestUtil.createProject();
        project.addWorkload(DaoTestUtil.createWorkload());
        project.addWorkload(DaoTestUtil.createWorkload());
        Project persistedProject = dao.saveOrUpdate(project);

        validateProject(project, persistedProject, false);
        project = dao.findById(persistedProject.getId());
        project.setComments("new Comments");
        persistedProject = dao.saveOrUpdate(project);
        validateProject(project, persistedProject, false);

        all = dao.findAll();
        assertNotNull(all);
        assertEquals(originalSize + 1, all.size());

        all = dao.findAll();
        assertNotNull(all);
        assertEquals(originalSize + 1, all.size());

        // delete it
        dao.delete(persistedProject);
        project = dao.findById(project.getId());
        assertNull(project);
        all = dao.findAll();
        assertEquals(originalSize, all.size());
    }

    private void validateProject(Project project, Project persistedProject, boolean checkCreateAttributes) {
        if (checkCreateAttributes) {
            assertEquals(project.getId(), persistedProject.getId());
            assertEquals(project.getCreated(), persistedProject.getCreated());
            assertNotSame(project.getModified(), persistedProject.getModified());
        } else {
            assertNotNull(persistedProject.getId());
            assertNotNull(persistedProject.getCreated());
            assertNotNull(persistedProject.getModified());
        }
        assertEquals(project.getComments(), persistedProject.getComments());
        assertEquals(project.getCreator(), persistedProject.getCreator());
        assertEquals(project.getProductName(), persistedProject.getProductName());
        assertEquals(project.getName(), persistedProject.getName());
        assertEquals(project.getScriptDriver(), persistedProject.getScriptDriver());
        assertEquals(project.getWorkloads().size(), persistedProject.getWorkloads().size());
    }

    

}
