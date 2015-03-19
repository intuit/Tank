package com.intuit.tank.vm.common.util;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.vm.common.util.MessageObject;

import static org.junit.Assert.*;

/**
 * The class <code>MessageObjectCpTest</code> contains tests for the class <code>{@link MessageObject}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class MessageObjectCpTest {

    /**
     * Run the Object getObject() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetObject_1()
            throws Exception {
        MessageObject fixture = new MessageObject(new Object());
        fixture.setObject(new Object());

        Object result = fixture.getObject();

        assertNotNull(result);
    }

    /**
     * Run the void setObject(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetObject_1()
            throws Exception {
        MessageObject fixture = new MessageObject(new Object());
        fixture.setObject(new Object());
        Object o = new Object();

        fixture.setObject(o);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        MessageObject fixture = new MessageObject(new Object());
        fixture.setObject(new Object());

        String result = fixture.toString();

        assertNotNull(result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testToString_2()
            throws Exception {
        MessageObject fixture = new MessageObject(new Object());
        fixture.setObject((Object) null);

        String result = fixture.toString();

        assertNotNull(result);
    }
}