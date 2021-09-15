/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.util.CreateDateComparator;
import com.intuit.tank.util.CreateDateComparator.SortOrder;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CreateDateComparatorTest
 * 
 * @author dangleton
 * 
 */
public class CreateDateComparatorTest {

    private List<TestBaseEntity> entities;

    @BeforeEach
    public void init() {
        entities = new ArrayList<TestBaseEntity>();
        Calendar cal = Calendar.getInstance();
        entities.add(new TestBaseEntity(1, cal.getTime()));
        cal.add(Calendar.DAY_OF_YEAR, 10);
        entities.add(new TestBaseEntity(2, cal.getTime()));
        cal.add(Calendar.DAY_OF_YEAR, -20);
        entities.add(new TestBaseEntity(0, cal.getTime()));

    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testAscending() {
        CreateDateComparator comp = new CreateDateComparator(SortOrder.ASCENDING);
        List<TestBaseEntity> copied = new ArrayList<CreateDateComparatorTest.TestBaseEntity>(entities);
        Collections.sort(copied, comp);
        assertEquals(copied.get(0).position, 0);
        assertEquals(copied.get(1).position, 1);
        assertEquals(copied.get(2).position, 2);

    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testDescending() {
        CreateDateComparator comp = new CreateDateComparator(SortOrder.DESCENDING);
        List<TestBaseEntity> copied = new ArrayList<CreateDateComparatorTest.TestBaseEntity>(entities);
        Collections.sort(copied, comp);
        assertEquals(copied.get(0).position, 2);
        assertEquals(copied.get(1).position, 1);
        assertEquals(copied.get(2).position, 0);

    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testAscending_1() {
        CreateDateComparator comp = new CreateDateComparator();
        List<TestBaseEntity> copied = new ArrayList<CreateDateComparatorTest.TestBaseEntity>(entities);
        Collections.sort(copied, comp);
        assertEquals(copied.get(0).position, 0);
        assertEquals(copied.get(1).position, 1);
        assertEquals(copied.get(2).position, 2);

    }

    /**
     * CreateDateComparatorTest TestBaseEntity
     * 
     * @author dangleton
     * 
     */
    static class TestBaseEntity extends BaseEntity {
        private static final long serialVersionUID = 1L;
        private int position;

        /**
         * @param name
         */
        public TestBaseEntity(int position, Date crateDate) {
            super();
            this.position = position;
            setCreated(crateDate);
        }

        /**
         * @return the position
         */
        public int getPosition() {
            return position;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            return Integer.toString(position);
        }

    }

}
