package com.intuit.tank.admin;

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

import java.util.LinkedList;
import java.util.List;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.util.Messages;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.VersionContainer;
import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.intuit.tank.admin.UserAdmin;
import com.intuit.tank.project.User;
import com.intuit.tank.wrapper.SelectableWrapper;

public class UserAdminTest {

    @InjectMocks
    private UserAdmin userAdmin;

    @Mock
    private UserLoader userLoader;

    @Mock
    private Messages messages;

    @Mock
    private Event<ModifiedUserMessage> userEvent;

    @Mock
    private Event<Preferences> deletedPrefsEvent;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        // tablePrefs and tableState are protected fields in SelectableBean (different package)
        java.lang.reflect.Field tablePrefsField = com.intuit.tank.wrapper.SelectableBean.class.getDeclaredField("tablePrefs");
        tablePrefsField.setAccessible(true);
        tablePrefsField.set(userAdmin, new TablePreferences(new LinkedList<>()));
        java.lang.reflect.Field tableStateField = com.intuit.tank.wrapper.SelectableBean.class.getDeclaredField("tableState");
        tableStateField.setAccessible(true);
        tableStateField.set(userAdmin, new TableViewState());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testGetSelectedUser_SetAndGet() {
        User user = new User();
        SelectableWrapper<User> wrapper = new SelectableWrapper<>(user);
        userAdmin.setSelectedUser(wrapper);
        assertEquals(wrapper, userAdmin.getSelectedUser());
    }

    @Test
    public void testIsCurrent_WhenCurrent_ReturnsTrue() {
        when(userLoader.isCurrent(anyInt())).thenReturn(true);
        assertTrue(userAdmin.isCurrent());
    }

    @Test
    public void testIsCurrent_WhenNotCurrent_ReturnsFalse() {
        when(userLoader.isCurrent(anyInt())).thenReturn(false);
        assertFalse(userAdmin.isCurrent());
    }

    @Test
    public void testGetEntityList_ReturnsUsers() {
        VersionContainer<User> container = mock(VersionContainer.class);
        when(container.getVersion()).thenReturn(1);
        when(container.getEntities()).thenReturn(List.of(new User()));
        when(userLoader.getVersionContainer()).thenReturn(container);

        List<User> result = userAdmin.getEntityList(ViewFilterType.ALL);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testDelete_WhenUserNotInDB_ShowsError() {
        User user = new User();
        user.setName("nonexistentuser");
        // With H2 DB: no projects owned, no scripts, etc.
        // deleteUserData returns 0 for user not in DB → messages.error called
        userAdmin.delete(user);
        verify(messages).error(anyString());
    }

    @Test
    public void testResetPreferences_WhenNoPreferences_DoesNothing() {
        User user = new User();
        user.setName("noprefuser");
        // PreferencesDao.getForOwner returns null for user without prefs
        assertDoesNotThrow(() -> userAdmin.resetPreferences(user));
        // deletedPrefsEvent should NOT be fired
        verify(deletedPrefsEvent, never()).fire(any());
    }

    @Test
    public void testDelete_WhenUserOwnsProjects_ShowsErrorWithProjectNames() {
        // Create a project owned by a specific user so that getUserOwnedResources returns non-empty
        String username = "testowner_" + System.nanoTime();
        com.intuit.tank.project.Project project = new com.intuit.tank.project.Project();
        project.setName("OwnedProject");
        project.setCreator(username);
        new com.intuit.tank.dao.ProjectDao().saveOrUpdate(project);

        User user = new User();
        user.setName(username);

        userAdmin.delete(user);

        // Should show error about owned resources
        verify(messages).error(anyString());
    }

    @Test
    public void testDelete_WhenUserInDB_AnonymizesAndDeletes() {
        // Create and persist a user with no owned resources
        // Note: findByUserName uses INNER JOIN on groups, so user must have a group to be found
        // Create user with a group - group is cascade-persisted with the user
        String groupName = "testgroup_" + System.nanoTime();
        com.intuit.tank.project.Group group = new com.intuit.tank.project.Group(groupName);

        String username = "deluser_" + System.nanoTime();
        User user = new User();
        user.setName(username);
        user.setEmail(username + "@test.com");
        user.setPassword("testpassword");
        user.addGroup(group);
        new com.intuit.tank.dao.UserDao().saveOrUpdate(user);

        // deleteUserData finds user via INNER JOIN on groups, anonymizes, returns > 0
        userAdmin.delete(user);

        // Should call info (deleted successfully) - not error about owned resources
        verify(messages, never()).error(contains("owns the following resources"));
        verify(messages).info(anyString());
    }

    @Test
    public void testResetPreferences_WhenPreferencesExist_FiresDeleteEvent() {
        // Create preferences for a user in H2
        String username = "prefuser_" + System.nanoTime();
        com.intuit.tank.project.Preferences prefs = new com.intuit.tank.project.Preferences();
        prefs.setCreator(username);
        new com.intuit.tank.dao.PreferencesDao().saveOrUpdate(prefs);

        User user = new User();
        user.setName(username);

        userAdmin.resetPreferences(user);

        // deletedPrefsEvent should be fired
        verify(deletedPrefsEvent).fire(any(Preferences.class));
    }
}