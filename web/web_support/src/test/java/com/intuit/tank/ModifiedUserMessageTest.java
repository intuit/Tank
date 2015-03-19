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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.project.User;

/**
 * The class <code>ModifiedUserMessageTest</code> contains tests for the class <code>{@link ModifiedUserMessage}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:51 PM
 */
public class ModifiedUserMessageTest {
    /**
     * Run the ModifiedUserMessage(User,Object) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testModifiedUserMessage_1()
        throws Exception {
        User modified = new User();
        Object modifier = new Object();

        ModifiedUserMessage result = new ModifiedUserMessage(modified, modifier);
        assertNotNull(result);
    }
}