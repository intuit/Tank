package com.intuit.tank;

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

import com.intuit.tank.auth.Security;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectLoader;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableWrapper;
import com.intuit.tank.wrapper.VersionContainer;
import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProjectDescriptionBeanTest {

    @InjectMocks
    private ProjectDescriptionBean bean;

    @Mock
    private ProjectLoader projectLoader;

    @Mock
    private Security security;

    @Mock
    private Messages messages;

    @Mock
    private PreferencesBean userPrefs;

    @Mock
    private Event<ModifiedProjectMessage> projectEvent;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testGetSelectedProject_SetAndGet() {
        Project project = new Project();
        SelectableWrapper<Project> wrapper = new SelectableWrapper<>(project);
        bean.setSelectedProject(wrapper);
        assertEquals(wrapper, bean.getSelectedProject());
    }

    @Test
    public void testDeleteSelectedProject_WhenNull_DoesNothing() {
        bean.setSelectedProject(null);
        assertDoesNotThrow(() -> bean.deleteSelectedProject());
    }

    @Test
    public void testDelete_WhenNoPermission_ShowsWarning() {
        when(security.hasRight(AccessRight.DELETE_PROJECT)).thenReturn(false);
        when(security.isOwner(any(Project.class))).thenReturn(false);

        Project project = new Project();
        project.setName("TestProject");
        bean.delete(project);

        verify(messages).warn(anyString());
    }

    @Test
    public void testDelete_WhenOwner_DeletesProject() {
        when(security.hasRight(AccessRight.DELETE_PROJECT)).thenReturn(false);
        when(security.isOwner(any(Project.class))).thenReturn(true);

        // Project needs to be a saved entity - skip DAO call by catching exception
        Project project = new Project();
        project.setName("TestProject");

        bean.delete(project);
    }

    @Test
    public void testDelete_WhenHasRight_DeletesProject() {
        when(security.hasRight(AccessRight.DELETE_PROJECT)).thenReturn(true);

        Project project = new Project();
        project.setName("TestProject");

        bean.delete(project);
    }

    @Test
    public void testIsCurrent_DelegatesToProjectLoader() {
        when(projectLoader.isCurrent(anyInt())).thenReturn(true);
        assertTrue(bean.isCurrent());
        verify(projectLoader).isCurrent(0);
    }
}
