package com.intuit.tank.api.model.v1.project;

/*
 * #%L
 * Project Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.project.ProjectContainer;
import com.intuit.tank.api.model.v1.project.ProjectTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ProjectContainerTest</code> contains tests for the class <code>{@link ProjectContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:08 PM
 */
public class ProjectContainerTest {
    /**
     * Run the ProjectContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testProjectContainer_1()
        throws Exception {

        ProjectContainer result = new ProjectContainer();

        assertNotNull(result);
    }

    /**
     * Run the ProjectContainer(List<ProjectTO>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testProjectContainer_2()
        throws Exception {
        List<ProjectTO> projects = new LinkedList();

        ProjectContainer result = new ProjectContainer(projects);

        assertNotNull(result);
    }

    /**
     * Run the List<ProjectTO> getProjects() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetProjects_1()
        throws Exception {
        ProjectContainer fixture = new ProjectContainer(new LinkedList());

        List<ProjectTO> result = fixture.getProjects();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setProjects(List<ProjectTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetProjects_1()
        throws Exception {
        ProjectContainer fixture = new ProjectContainer(new LinkedList());
        List<ProjectTO> projects = new LinkedList();

        fixture.setProjects(projects);

    }
}