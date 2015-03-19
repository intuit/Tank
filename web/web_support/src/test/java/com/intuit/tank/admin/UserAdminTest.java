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

import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.admin.UserAdmin;
import com.intuit.tank.project.User;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * The class <code>UserAdminTest</code> contains tests for the class <code>{@link UserAdmin}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class UserAdminTest {
    /**
     * Run the UserAdmin() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testUserAdmin_1()
        throws Exception {
        UserAdmin result = new UserAdmin();
        assertNotNull(result);
    }

    /**
     * Run the SelectableWrapper<User> getSelectedUser() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectedUser_1()
        throws Exception {
        UserAdmin fixture = new UserAdmin();
        fixture.setSelectedUser(new SelectableWrapper((Object) null));

        SelectableWrapper<User> result = fixture.getSelectedUser();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.admin.UserAdmin.<init>(UserAdmin.java:35)
        assertNotNull(result);
    }

 
    /**
     * Run the void resetPreferences(User) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testResetPreferences_1()
        throws Exception {
        UserAdmin fixture = new UserAdmin();
        fixture.setSelectedUser(new SelectableWrapper((Object) null));
        User user = new User();
        user.setName("");

        fixture.resetPreferences(user);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
    }

    /**
     * Run the void resetPreferences(User) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testResetPreferences_2()
        throws Exception {
        UserAdmin fixture = new UserAdmin();
        fixture.setSelectedUser(new SelectableWrapper((Object) null));
        User user = new User();
        user.setName("");

        fixture.resetPreferences(user);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
    }

    /**
     * Run the void resetPreferences(User) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testResetPreferences_3()
        throws Exception {
        UserAdmin fixture = new UserAdmin();
        fixture.setSelectedUser(new SelectableWrapper((Object) null));
        User user = new User();
        user.setName("");

        fixture.resetPreferences(user);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
    }

    /**
     * Run the void setSelectedUser(SelectableWrapper<User>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelectedUser_1()
        throws Exception {
        UserAdmin fixture = new UserAdmin();
        fixture.setSelectedUser(new SelectableWrapper((Object) null));
        SelectableWrapper<User> selectedUser = new SelectableWrapper((Object) null);

        fixture.setSelectedUser(selectedUser);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.admin.UserAdmin.<init>(UserAdmin.java:35)
    }
}