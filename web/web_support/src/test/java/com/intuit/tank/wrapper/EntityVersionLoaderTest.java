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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.BaseEntity;
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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

    /**
     * Concrete test implementation of EntityVersionLoader that doesn't need CDI.
     */
    private static class TestEntityVersionLoader extends EntityVersionLoader<Project, Object> {
        private final List<Project> entities;

        TestEntityVersionLoader(List<Project> entities) {
            this.entities = entities;
        }

        @Override
        protected List<Project> getEntities() {
            return entities;
        }
    }

    @Test
    public void testGetVersionContainer_WithConcreteLoader_ReturnsNonNull() {
        List<Project> projects = new ArrayList<>();
        Project p = new Project();
        p.setName("TestProject");
        projects.add(p);
        TestEntityVersionLoader loader = new TestEntityVersionLoader(projects);

        VersionContainer<Project> container = loader.getVersionContainer();
        assertNotNull(container);
        assertNotNull(container.getEntities());
        assertEquals(1, container.getEntities().size());
    }

    @Test
    public void testGetVersionContainer_WithViewFilter_All() {
        List<Project> projects = new ArrayList<>();
        Project p = new Project();
        p.setName("P1");
        projects.add(p);
        TestEntityVersionLoader loader = new TestEntityVersionLoader(projects);

        VersionContainer<Project> container = loader.getVersionContainer(ViewFilterType.ALL);
        assertNotNull(container);
        assertEquals(1, container.getEntities().size());
    }

    @Test
    public void testGetVersionEntities_ReturnsAllForViewFilterAll() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project());
        projects.add(new Project());
        TestEntityVersionLoader loader = new TestEntityVersionLoader(projects);

        List<Project> result = loader.getVersionEntities();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetVersionEntities_WithViewFilter() {
        List<Project> projects = new ArrayList<>();
        Project recent = new Project();
        recent.setCreated(new Date());
        projects.add(recent);
        TestEntityVersionLoader loader = new TestEntityVersionLoader(projects);

        // ViewFilterType.ALL returns all
        List<Project> result = loader.getVersionEntities(ViewFilterType.ALL);
        assertEquals(1, result.size());
    }

    @Test
    public void testObserveEvents_Invalidates() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project());
        TestEntityVersionLoader loader = new TestEntityVersionLoader(projects);

        // Load once to set version
        VersionContainer<Project> c1 = loader.getVersionContainer();
        int v1 = c1.getVersion();

        // Observe event → invalidates → next getVersionContainer reloads and increments version
        loader.observeEvents(new Object());
        VersionContainer<Project> c2 = loader.getVersionContainer();
        assertTrue(c2.getVersion() > v1);
    }

    @Test
    public void testIsCurrent_AfterReload_VersionMismatches() {
        TestEntityVersionLoader loader = new TestEntityVersionLoader(new ArrayList<>());
        loader.invalidate(); // version = 1

        // version=1 matches
        assertTrue(loader.isCurrent(1));
        // version=0 doesn't match
        assertFalse(loader.isCurrent(0));
    }
}