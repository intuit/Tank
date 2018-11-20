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

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.user.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>UserTest</code> contains tests for the class <code>{@link User}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:41 PM
 */
public class UserTest {
    /**
     * Run the User() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testUser_1()
        throws Exception {
        User result = new User();
        assertNotNull(result);
    }

    /**
     * Run the void addGroup(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testAddGroup_1()
        throws Exception {
        User fixture = new User();
        fixture.setEmail("");
        fixture.setGroups(new HashSet());
        fixture.setName("");
        String group = "";

        fixture.addGroup(group);

    }

    /**
     * Run the String getEmail() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testGetEmail_1()
        throws Exception {
        User fixture = new User();
        fixture.setEmail("");
        fixture.setGroups(new HashSet());
        fixture.setName("");

        String result = fixture.getEmail();

        assertEquals("", result);
    }

    /**
     * Run the Set<String> getGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testGetGroups_1()
        throws Exception {
        User fixture = new User();
        fixture.setEmail("");
        fixture.setGroups(new HashSet());
        fixture.setName("");

        Set<String> result = fixture.getGroups();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        User fixture = new User();
        fixture.setEmail("");
        fixture.setGroups(new HashSet());
        fixture.setName("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the void setEmail(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testSetEmail_1()
        throws Exception {
        User fixture = new User();
        fixture.setEmail("");
        fixture.setGroups(new HashSet());
        fixture.setName("");
        String email = "";

        fixture.setEmail(email);

    }

    /**
     * Run the void setGroups(Set<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testSetGroups_1()
        throws Exception {
        User fixture = new User();
        fixture.setEmail("");
        fixture.setGroups(new HashSet());
        fixture.setName("");
        Set<String> groups = new HashSet();

        fixture.setGroups(groups);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        User fixture = new User();
        fixture.setEmail("");
        fixture.setGroups(new HashSet());
        fixture.setName("");
        String name = "";

        fixture.setName(name);

    }
}