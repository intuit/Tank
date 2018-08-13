package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;

/**
 * The class <code>UserTest</code> contains tests for the class <code>{@link User}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class UserTest {
    /**
     * Run the User() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testUser_1()
        throws Exception {

        User result = new User();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(null, result.getPassword());
        assertEquals(null, result.getEmail());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the void addGroup(Group) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddGroup_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        Group group = new Group();

        fixture.addGroup(group);

    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        Object obj = new User();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        Object obj = new User();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getEmail() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetEmail_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");

        String result = fixture.getEmail();

        assertEquals("", result);
    }

    /**
     * Run the Set<Group> getGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetGroups_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");

        Set<Group> result = fixture.getGroups();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getPassword() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPassword_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");

        String result = fixture.getPassword();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void setEmail(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetEmail_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        String email = "";

        fixture.setEmail(email);

    }

    /**
     * Run the void setGroups(Set<Group>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetGroups_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        Set<Group> groups = new HashSet();

        fixture.setGroups(groups);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setPassword(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPassword_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");
        String password = "";

        fixture.setPassword(password);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        User fixture = new User();
        fixture.setGroups(new HashSet());
        fixture.setEmail("");
        fixture.setPassword("");
        fixture.setName("");

        String result = fixture.toString();

    }
}