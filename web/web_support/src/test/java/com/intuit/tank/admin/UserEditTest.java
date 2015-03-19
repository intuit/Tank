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

import org.junit.*;

import static org.junit.Assert.*;

import org.primefaces.model.DualListModel;

import com.intuit.tank.admin.UserEdit;
import com.intuit.tank.project.User;

/**
 * The class <code>UserEditTest</code> contains tests for the class <code>{@link UserEdit}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class UserEditTest {
    /**
     * Run the UserEdit() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testUserEdit_1()
            throws Exception {
        UserEdit result = new UserEdit();
        assertNotNull(result);
    }

    /**
     * Run the String cancel() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCancel_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());

        String result = fixture.cancel();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
        assertNotNull(result);
    }

    /**
     * Run the String edit(User) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEdit_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());
        User user = new User();

        String result = fixture.edit(user);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
        assertNotNull(result);
    }

    /**
     * Run the String getPassword() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetPassword_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());

        String result = fixture.getPassword();
    }

    /**
     * Run the String getPasswordConfirm() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetPasswordConfirm_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());

        String result = fixture.getPasswordConfirm();
    }

    /**
     * Run the DualListModel<String> getSelectionModel() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectionModel_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());

        DualListModel<String> result = fixture.getSelectionModel();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
        assertNotNull(result);
    }

    /**
     * Run the User getUser() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetUser_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());

        User result = fixture.getUser();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
        assertNotNull(result);
    }

    /**
     * Run the String newUser() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testNewUser_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());

        String result = fixture.newUser();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
        assertNotNull(result);
    }

    /**
     * Run the void setPassword(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetPassword_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());
        String password = "";

        fixture.setPassword(password);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
    }

    /**
     * Run the void setPasswordConfirm(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetPasswordConfirm_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());
        String passwordConfirm = "";

        fixture.setPasswordConfirm(passwordConfirm);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
    }

    /**
     * Run the void setSelectionModel(DualListModel<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelectionModel_1()
            throws Exception {
        UserEdit fixture = new UserEdit();
        fixture.setSelectionModel(new DualListModel());
        fixture.edit(new User());
        DualListModel<String> selectionModel = new DualListModel();

        fixture.setSelectionModel(selectionModel);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.admin.UserEdit.setSelectionModel(UserEdit.java:92)
    }
}