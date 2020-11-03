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

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The class <code>TankDefaultRevisionEntityTest</code> contains tests for the class <code>{@link TankDefaultRevisionEntity}</code>.
 */
public class TankDefaultRevisionEntityTest {

    @Test
    public void testTankDefaultRevisionEntity_1() {
        TankDefaultRevisionEntity result = new TankDefaultRevisionEntity();
        result.setId(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void testTankDefaultRevisionEntity_2() {
        TankDefaultRevisionEntity result = new TankDefaultRevisionEntity();
        result.setTimestamp(20);

        assertEquals(20, result.getTimestamp());
        assertEquals(new Date(20), result.getRevisionDate());
    }

    @Test
    public void testTankDefaultRevisionEntity_3() {
        TankDefaultRevisionEntity result = new TankDefaultRevisionEntity();

        assertTrue(result.equals(result));
        assertTrue(result.equals(new TankDefaultRevisionEntity()));
        assertNotNull(result.hashCode());
        assertNotNull(result.toString());
    }
}