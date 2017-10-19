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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.test.TestGroups;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
public class ProjectDaoTest {

    private ProjectDao dao;

    @DataProvider(name = "validations")
    private Object[][] violationData() {
        return new Object[][] {
                {
                        Project.builderFrom(DaoTestUtil.createProject()).name(DaoTestUtil.generateStringOfLength(256))
                                .build(), "name",
                        "length must be between" },
                { Project.builderFrom(DaoTestUtil.createProject()).name(null).build(), "name", "may not be empty" },
                { Project.builderFrom(DaoTestUtil.createProject()).scriptDriver(null).build(), "scriptDriver",
                        "may not be null" },
                {
                        Project.builderFrom(DaoTestUtil.createProject())
                                .productName(DaoTestUtil.generateStringOfLength(257)).build(), "productName",
                        "length must be between" },
                {
                        Project.builderFrom(DaoTestUtil.createProject())
                                .comments(DaoTestUtil.generateStringOfLength(1025)).build(), "comments",
                        "length must be between" }, };

    }

    @BeforeClass
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new ProjectDao();
    }

    @Test(groups = { TestGroups.FUNCTIONAL })
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

            Assert.assertFalse(persistedProject.getWorkloads().get(0).equals(originalOrder.get(0)));
            Assert.assertTrue(persistedProject.getWorkloads().get(0).equals(originalOrder.get(2)));
            Assert.assertTrue(persistedProject.getWorkloads().get(1).equals(originalOrder.get(0)));
            Assert.assertTrue(persistedProject.getWorkloads().get(2).equals(originalOrder.get(1)));

            originalOrder = new ArrayList<Workload>(persistedProject.getWorkloads());
            persistedProject.getWorkloads().remove(2);
            persistedProject.getWorkloads().remove(0);
            persistedProject = dao.saveOrUpdate(persistedProject);
            persistedProject = dao.findById(projectId);
            Assert.assertTrue(persistedProject.getWorkloads().get(0).equals(originalOrder.get(1)));
            Assert.assertEquals(1, persistedProject.getWorkloads().size());

        } finally {
            // delete it
            dao.delete(projectId);
        }

    }

    @Test(groups = { TestGroups.FUNCTIONAL }, dataProvider = "validations")
    public void testValidation(Project project, String property, String messageContains) throws Exception {
        try {
            dao.saveOrUpdate(project);
            Assert.fail("Should have failed validation.");
        } catch (ConstraintViolationException e) {
            // expected validation
            DaoTestUtil.checkConstraintViolation(e, property, messageContains);
        }
    }

//    @Test(groups = { TestGroups.FUNCTIONAL })
//    public void testBasicCreateUpdateDelete() throws Exception {
//        List<Project> all = dao.findAll();
//        int originalSize = all.size();
//        Project project = DaoTestUtil.createProject();
//        project.addWorkload(DaoTestUtil.createWorkload());
//        project.addWorkload(DaoTestUtil.createWorkload());
//        Project persistedProject = dao.saveOrUpdate(project);
//
//        validateProject(project, persistedProject, false);
//        project = dao.findById(persistedProject.getId());
//        project.setComments("new Comments");
//        persistedProject = dao.saveOrUpdate(project);
//        validateProject(project, persistedProject, false);
//
//        all = dao.findAll();
//        Assert.assertNotNull(all);
//        Assert.assertEquals(originalSize + 1, all.size());
//
//        all = dao.findAll();
//        Assert.assertNotNull(all);
//        Assert.assertEquals(originalSize + 1, all.size());
//
//        // delete it
//        dao.delete(persistedProject);
//        project = dao.findById(project.getId());
//        Assert.assertNull(project);
//        all = dao.findAll();
//        Assert.assertEquals(originalSize, all.size());
//    }

    private void validateProject(Project project, Project persistedProject, boolean checkCreateAttributes) {
        if (checkCreateAttributes) {
            Assert.assertEquals(project.getId(), persistedProject.getId());
            Assert.assertEquals(project.getCreated(), persistedProject.getCreated());
            Assert.assertNotSame(project.getModified(), persistedProject.getModified());
        } else {
            Assert.assertNotNull(persistedProject.getId());
            Assert.assertNotNull(persistedProject.getCreated());
            Assert.assertNotNull(persistedProject.getModified());
        }
        Assert.assertEquals(project.getComments(), persistedProject.getComments());
        Assert.assertEquals(project.getCreator(), persistedProject.getCreator());
        Assert.assertEquals(project.getProductName(), persistedProject.getProductName());
        Assert.assertEquals(project.getName(), persistedProject.getName());
        Assert.assertEquals(project.getScriptDriver(), persistedProject.getScriptDriver());
        Assert.assertEquals(project.getWorkloads().size(), persistedProject.getWorkloads().size());
    }

    

}
