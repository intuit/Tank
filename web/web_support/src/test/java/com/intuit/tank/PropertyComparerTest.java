package com.intuit.tank;

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

import com.intuit.tank.project.User;
import org.junit.jupiter.api.*;

import com.intuit.tank.PropertyComparer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>PropertyComparerTest</code> contains tests for the class <code>{@link PropertyComparer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class PropertyComparerTest {

    @Test
    public void testPropertyComparer_1() throws Exception {
        PropertyComparer result = new PropertyComparer("");
        assertNotNull(result);
    }

    @Test
    public void testPropertyComparer_2() throws Exception {
        PropertyComparer result = new PropertyComparer("", PropertyComparer.SortOrder.ASCENDING);
        assertNotNull(result);
    }

    @Test
    public void testCompare_BothNull_ReturnsZero() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        assertEquals(0, comparer.compare(null, null));
    }

    @Test
    public void testCompare_SrcNotNullTgtNull_ReturnsPositive() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User user = new User();
        user.setName("alice");
        assertTrue(comparer.compare(user, null) > 0);
    }

    @Test
    public void testCompare_SrcNullTgtNotNull_ReturnsNegative() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User user = new User();
        user.setName("alice");
        assertTrue(comparer.compare(null, user) < 0);
    }

    @Test
    public void testCompare_BothNotNull_SameValue_ReturnsZero() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User u1 = new User();
        u1.setName("alice");
        User u2 = new User();
        u2.setName("alice");
        assertEquals(0, comparer.compare(u1, u2));
    }

    @Test
    public void testCompare_BothNotNull_SrcLessThanTgt_ReturnsNegative() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User u1 = new User();
        u1.setName("alice");
        User u2 = new User();
        u2.setName("bob");
        assertTrue(comparer.compare(u1, u2) < 0);
    }

    @Test
    public void testCompare_BothNotNull_SrcGreaterThanTgt_ReturnsPositive() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User u1 = new User();
        u1.setName("bob");
        User u2 = new User();
        u2.setName("alice");
        assertTrue(comparer.compare(u1, u2) > 0);
    }

    @Test
    public void testCompare_Descending_ReversesOrder() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name", PropertyComparer.SortOrder.DESCENDING);
        User u1 = new User();
        u1.setName("alice");
        User u2 = new User();
        u2.setName("bob");
        // In descending, alice > bob should be positive
        assertTrue(comparer.compare(u1, u2) > 0);
    }

    @Test
    public void testCompare_DescendingBothNull_ReturnsZero() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name", PropertyComparer.SortOrder.DESCENDING);
        assertEquals(0, comparer.compare(null, null));
    }

    @Test
    public void testCompare_PropertyBothNull_ReturnsZero() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User u1 = new User(); // name is null
        User u2 = new User(); // name is null
        assertEquals(0, comparer.compare(u1, u2));
    }

    @Test
    public void testCompare_PropertySrcNullTgtNotNull_ReturnsNegative() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User u1 = new User(); // name is null
        User u2 = new User();
        u2.setName("bob");
        assertTrue(comparer.compare(u1, u2) < 0);
    }

    @Test
    public void testCompare_PropertySrcNotNullTgtNull_ReturnsPositive() {
        PropertyComparer<User> comparer = new PropertyComparer<>("name");
        User u1 = new User();
        u1.setName("alice");
        User u2 = new User(); // name is null
        assertTrue(comparer.compare(u1, u2) > 0);
    }

    @Test
    public void testCompare_InvalidProperty_ThrowsRuntimeException() {
        PropertyComparer<User> comparer = new PropertyComparer<>("nonExistentProperty");
        User u1 = new User();
        User u2 = new User();
        assertThrows(RuntimeException.class, () -> comparer.compare(u1, u2));
    }

    @Test
    public void testSortOrder_Values_ContainsBothValues() {
        PropertyComparer.SortOrder[] values = PropertyComparer.SortOrder.values();
        assertEquals(2, values.length);
    }
}