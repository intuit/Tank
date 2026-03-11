package com.intuit.tank.project;

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

import com.intuit.tank.ProjectBean;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationsEditorTest {

    @InjectMocks
    private NotificationsEditor notificationsEditor;

    @Mock
    private ProjectBean projectBean;

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
    public void testConstructor() {
        assertNotNull(notificationsEditor);
    }

    @Test
    public void testGetNotificationEventList_ReturnsAllValues() {
        JobLifecycleEvent[] events = notificationsEditor.getNotificationEventList();
        assertNotNull(events);
        assertTrue(events.length > 0);
    }

    @Test
    public void testGetSetCurrentNotification() {
        JobNotification notification = new JobNotification();
        notificationsEditor.setCurrentNotification(notification);
        assertEquals(notification, notificationsEditor.getCurrentNotification());
    }

    @Test
    public void testInit_WithEmptyNotifications() {
        JobConfiguration jc = new JobConfiguration();
        jc.setNotifications(new HashSet<>());
        when(projectBean.getJobConfiguration()).thenReturn(jc);

        notificationsEditor.init();

        assertNotNull(notificationsEditor.getNotifications());
        assertTrue(notificationsEditor.getNotifications().isEmpty());
    }

    @Test
    public void testInit_WithExistingNotifications() {
        JobConfiguration jc = new JobConfiguration();
        Set<JobNotification> existing = new HashSet<>();
        JobNotification n = new JobNotification();
        existing.add(n);
        jc.setNotifications(existing);
        when(projectBean.getJobConfiguration()).thenReturn(jc);

        notificationsEditor.init();

        assertEquals(1, notificationsEditor.getNotifications().size());
    }

    @Test
    public void testAddNotification_AddsToList() {
        JobConfiguration jc = new JobConfiguration();
        jc.setNotifications(new HashSet<>());
        when(projectBean.getJobConfiguration()).thenReturn(jc);

        notificationsEditor.init();
        notificationsEditor.addNotification();

        assertEquals(1, notificationsEditor.getNotifications().size());
    }

    @Test
    public void testDelete_RemovesFromList() {
        JobConfiguration jc = new JobConfiguration();
        jc.setNotifications(new HashSet<>());
        when(projectBean.getJobConfiguration()).thenReturn(jc);

        notificationsEditor.init();
        notificationsEditor.addNotification();
        notificationsEditor.addNotification();

        JobNotification toDelete = notificationsEditor.getNotifications().get(0);
        notificationsEditor.delete(toDelete);

        assertEquals(1, notificationsEditor.getNotifications().size());
    }

    @Test
    public void testGetInplaceLabel_WhenEmpty_ReturnsClickToAdd() {
        String result = notificationsEditor.getInplaceLabel("");
        assertEquals("<Click to Add Recipients>", result);
    }

    @Test
    public void testGetInplaceLabel_WhenNull_ReturnsClickToAdd() {
        String result = notificationsEditor.getInplaceLabel(null);
        assertEquals("<Click to Add Recipients>", result);
    }

    @Test
    public void testGetInplaceLabel_WhenNotEmpty_ReturnsValue() {
        String result = notificationsEditor.getInplaceLabel("user@example.com");
        assertEquals("user@example.com", result);
    }

    @Test
    public void testSave_WithEmptyNotifications_DoesNotThrow() {
        JobConfiguration jc = new JobConfiguration();
        jc.setNotifications(new HashSet<>());
        Workload workload = Workload.builder().name("test").build();
        workload.setJobConfiguration(jc);
        when(projectBean.getJobConfiguration()).thenReturn(jc);
        when(projectBean.getWorkload()).thenReturn(workload);

        notificationsEditor.init();
        assertDoesNotThrow(() -> notificationsEditor.save());
    }

    @Test
    public void testCopyTo_CopiesNotifications() {
        JobConfiguration jc = new JobConfiguration();
        jc.setNotifications(new HashSet<>());
        when(projectBean.getJobConfiguration()).thenReturn(jc);

        notificationsEditor.init();
        notificationsEditor.addNotification();
        notificationsEditor.getNotifications().get(0).setSubject("Test Subject");

        Project project = new Project();
        Workload targetWorkload = Workload.builder().name("target").project(project).build();
        targetWorkload.setJobConfiguration(new JobConfiguration());

        notificationsEditor.copyTo(targetWorkload);

        assertNotNull(targetWorkload.getJobConfiguration().getNotifications());
        assertEquals(1, targetWorkload.getJobConfiguration().getNotifications().size());
    }
}
