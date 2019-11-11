package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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

import java.util.List;

import com.intuit.tank.api.model.v1.cloud.UserDetail;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The class <code>UserTrackerTest</code> contains tests for the class <code>{@link UserTracker}</code>.
 * 
 * @generatedBy CodePro at 9/15/14 4:18 PM
 */
public class UserTrackerTest {
    /**
     * Run the UserTracker() constructor test.
     * 
     * @generatedBy CodePro at 9/15/14 4:18 PM
     */
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testUserTracker_1()
            throws Exception {
        UserTracker result = new UserTracker();
        assertNotNull(result);
    }

    /**
     * Run the void add(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:18 PM
     */
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testAdd_1()
            throws Exception {
        UserTracker fixture = new UserTracker();
        String script = "test";

        fixture.add(script);
        List<UserDetail> snapshot = fixture.getSnapshot();
        assertEquals(1, snapshot.size());
        assertEquals(script, snapshot.get(0).getScript());
        assertEquals(1, snapshot.get(0).getUsers().intValue());
        fixture.add(script);
        snapshot = fixture.getSnapshot();
        assertEquals(1, snapshot.size());
        assertEquals(2, snapshot.get(0).getUsers().intValue());

    }

    /**
     * Run the void remove(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:18 PM
     */
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRemove_1()
            throws Exception {
        UserTracker fixture = new UserTracker();
        String script = "test";

        fixture.add(script);
        List<UserDetail> snapshot = fixture.getSnapshot();
        assertEquals(1, snapshot.size());
        assertEquals(script, snapshot.get(0).getScript());
        assertEquals(1, snapshot.get(0).getUsers().intValue());
        fixture.add(script);
        snapshot = fixture.getSnapshot();
        assertEquals(1, snapshot.size());
        assertEquals(2, snapshot.get(0).getUsers().intValue());

        fixture.remove(script);
        snapshot = fixture.getSnapshot();
        assertEquals(1, snapshot.size());
        assertEquals(script, snapshot.get(0).getScript());
        assertEquals(1, snapshot.get(0).getUsers().intValue());

        fixture.remove(script);
        snapshot = fixture.getSnapshot();
        assertEquals(1, snapshot.size());
        assertEquals(script, snapshot.get(0).getScript());
        assertEquals(0, snapshot.get(0).getUsers().intValue());

    }

}