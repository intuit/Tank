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

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.admin.UserLoader;
import com.intuit.tank.project.User;

import javax.inject.Inject;

/**
 * The class <code>UserLoaderTest</code> contains tests for the class <code>{@link UserLoader}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
@EnableAutoWeld
public class UserLoaderTest {
    
    @Inject
    private UserLoader userLoader;
    
    /**
     * Run the UserLoader() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testUserLoader_1()
        throws Exception {
        assertNotNull(userLoader);
    }

    /**
     * Run the List<User> getEntities() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    @Disabled
    public void testGetEntities_1()
        throws Exception {

        List<User> result = userLoader.getEntities();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.UserLoader.getEntities(UserLoader.java:32)
        assertNotNull(result);
    }
}