package com.intuit.tank.wrapper;

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

import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectLoader;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.EntityVersionLoader;
import com.intuit.tank.wrapper.VersionContainer;

/**
 * The class <code>EntityVersionLoaderTest</code> contains tests for the class <code>{@link EntityVersionLoader}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class EntityVersionLoaderTest {
    /**
     * Run the VersionContainer<com.intuit.tank.project.BaseEntity> getVersionContainer() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVersionContainer_1()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();

        VersionContainer<Project> result = fixture.getVersionContainer();
        assertNotNull(result);
    }

    /**
     * Run the VersionContainer<com.intuit.tank.project.BaseEntity> getVersionContainer(ViewFilterType) method
     * test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVersionContainer_2()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();
        ViewFilterType viewFilter = ViewFilterType.ALL;

        VersionContainer<Project> result = fixture.getVersionContainer(viewFilter);
        assertNotNull(result);
    }

    /**
     * Run the VersionContainer<com.intuit.tank.project.BaseEntity> getVersionContainer(ViewFilterType) method
     * test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVersionContainer_3()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();
        ViewFilterType viewFilter = ViewFilterType.ALL;

        VersionContainer<Project> result = fixture.getVersionContainer(viewFilter);
        assertNotNull(result);
    }

    /**
     * Run the List<com.intuit.tank.project.BaseEntity> getVersionEntities() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVersionEntities_1()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();

        List<Project> result = fixture.getVersionEntities();
        assertNotNull(result);
    }

    /**
     * Run the List<com.intuit.tank.project.BaseEntity> getVersionEntities(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVersionEntities_2()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<Project> result = fixture.getVersionEntities(viewFilter);
        assertNotNull(result);
    }

    /**
     * Run the List<com.intuit.tank.project.BaseEntity> getVersionEntities(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVersionEntities_3()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<Project> result = fixture.getVersionEntities(viewFilter);
        assertNotNull(result);
    }

    /**
     * Run the void invalidate() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testInvalidate_1()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();

        fixture.invalidate();
    }

    /**
     * Run the boolean isCurrent(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsCurrent_1()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();
        int version = 1;

        boolean result = fixture.isCurrent(version);
        assertTrue(result);
    }

    /**
     * Run the boolean isCurrent(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsCurrent_2()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();
        int version = 1;

        boolean result = fixture.isCurrent(version);
        assertTrue(result);
    }

    /**
     * Run the boolean isCurrent(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsCurrent_3()
            throws Exception {
        ProjectLoader fixture = new ProjectLoader();
        fixture.invalidate();
        int version = 1;

        boolean result = fixture.isCurrent(version);
        assertTrue(result);
    }
}