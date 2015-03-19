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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.EntityVersion;

/**
 * The class <code>EntityVersionTest</code> contains tests for the class <code>{@link EntityVersion}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class EntityVersionTest {
    /**
     * Run the EntityVersion() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEntityVersion_1()
        throws Exception {

        EntityVersion result = new EntityVersion();

        assertNotNull(result);
        assertEquals(0, result.getVersionId());
        assertEquals(0, result.getObjectId());
        assertEquals(null, result.getObjectClass());
    }

    /**
     * Run the EntityVersion(int,int,Class<? extends BaseEntity>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEntityVersion_2()
        throws Exception {
        int objectId = 1;
        int versionId = 1;
        Class<? extends BaseEntity> objectClass = BaseEntity.class;

        EntityVersion result = new EntityVersion(objectId, versionId, objectClass);

        assertNotNull(result);
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
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);
        Object obj = new Object();

        boolean result = fixture.equals(obj);

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
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);
        Object obj = new EntityVersion();

        boolean result = fixture.equals(obj);

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
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);
        Object obj = new EntityVersion();

        boolean result = fixture.equals(obj);

    }

    /**
     * Run the String getObjectClass() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetObjectClass_1()
        throws Exception {
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);

        String result = fixture.getObjectClass();

        assertNotNull(result);
    }

    /**
     * Run the int getObjectId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetObjectId_1()
        throws Exception {
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);

        int result = fixture.getObjectId();

    }

    /**
     * Run the int getVersionId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVersionId_1()
        throws Exception {
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);

        int result = fixture.getVersionId();

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
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);

        int result = fixture.hashCode();

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
        EntityVersion fixture = new EntityVersion(1, 1, BaseEntity.class);

        String result = fixture.toString();

        assertNotNull(result);
    }
}