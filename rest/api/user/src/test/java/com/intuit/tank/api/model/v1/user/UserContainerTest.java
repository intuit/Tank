package com.intuit.tank.api.model.v1.user;

/*
 * #%L
 * User Rest API
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

import com.intuit.tank.api.model.v1.user.User;
import com.intuit.tank.api.model.v1.user.UserContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>UserContainerTest</code> contains tests for the class <code>{@link UserContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:41 PM
 */
public class UserContainerTest {
    /**
     * Run the UserContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testUserContainer_1()
        throws Exception {

        UserContainer result = new UserContainer();

        assertNotNull(result);
    }

    /**
     * Run the UserContainer(List<User>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testUserContainer_2()
        throws Exception {
        List<User> users = new LinkedList();

        UserContainer result = new UserContainer(users);

        assertNotNull(result);
    }

    /**
     * Run the List<User> getUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testGetUsers_1()
        throws Exception {
        UserContainer fixture = new UserContainer(new LinkedList());

        List<User> result = fixture.getUsers();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}