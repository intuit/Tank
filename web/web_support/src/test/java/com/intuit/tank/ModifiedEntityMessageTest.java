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

import org.junit.jupiter.api.*;

import com.intuit.tank.ModifiedEntityMessage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ModifiedEntityMessageTest</code> contains tests for the class <code>{@link ModifiedEntityMessage}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ModifiedEntityMessageTest {
    /**
     * Run the ModifiedEntityMessage(T,Object) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testModifiedEntityMessage_1()
        throws Exception {
        Object modifier = new Object();

        ModifiedEntityMessage result = new ModifiedEntityMessage(null, modifier);

        assertNotNull(result);
    }

    /**
     * Run the Object getModified() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        ModifiedEntityMessage fixture = new ModifiedEntityMessage("", "");

        Object result = fixture.getModified();
        assertNotNull(result);
    }

    /**
     * Run the Object getModifier() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetModifier_1()
        throws Exception {
        ModifiedEntityMessage fixture = new ModifiedEntityMessage("", "");

        Object result = fixture.getModifier();
        assertNotNull(result);
    }
}