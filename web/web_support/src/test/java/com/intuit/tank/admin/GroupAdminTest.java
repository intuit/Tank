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

import com.intuit.tank.admin.GroupAdmin;
import com.intuit.tank.project.Group;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * The class <code>GroupAdminTest</code> contains tests for the class <code>{@link GroupAdmin}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class GroupAdminTest {
    /**
     * Run the GroupAdmin() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGroupAdmin_1()
        throws Exception {
        GroupAdmin result = new GroupAdmin();
        assertNotNull(result);
    }

    /**
     * Run the void begin() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testBegin_1()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());

        fixture.begin();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
    }

    /**
     * Run the void delete(Group) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_1()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());
        Group group = new Group();

        fixture.delete(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
    }

    /**
     * Run the void delete(Group) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_2()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());
        Group group = new Group();

        fixture.delete(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
    }

    /**
     * Run the List<SelectableWrapper<Group>> getGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetGroups_1()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());

        List<SelectableWrapper<Group>> result = fixture.getGroups();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
        assertNotNull(result);
    }

    /**
     * Run the List<SelectableWrapper<Group>> getGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetGroups_2()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());

        List<SelectableWrapper<Group>> result = fixture.getGroups();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
        assertNotNull(result);
    }

    /**
     * Run the List<SelectableWrapper<Group>> getGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetGroups_3()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());

        List<SelectableWrapper<Group>> result = fixture.getGroups();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
        assertNotNull(result);
    }

    /**
     * Run the List<SelectableWrapper<Group>> getGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetGroups_4()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());

        List<SelectableWrapper<Group>> result = fixture.getGroups();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
        assertNotNull(result);
    }

    /**
     * Run the void select(Group) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSelect_1()
        throws Exception {
        GroupAdmin fixture = new GroupAdmin();
        fixture.select(new Group());
        Group group = new Group();

        fixture.select(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Group.<init>(Group.java:37)
    }
}