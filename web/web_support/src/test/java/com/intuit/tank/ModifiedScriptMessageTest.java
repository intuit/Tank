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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.project.Script;

/**
 * The class <code>ModifiedScriptMessageTest</code> contains tests for the class <code>{@link ModifiedScriptMessage}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ModifiedScriptMessageTest {
    /**
     * Run the ModifiedScriptMessage(Script,Object) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testModifiedScriptMessage_1()
        throws Exception {
        Script modified = new Script();
        Object modifier = new Object();

        ModifiedScriptMessage result = new ModifiedScriptMessage(modified, modifier);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ModifiedEntityMessage.<init>(ModifiedEntityMessage.java:26)
        //       at com.intuit.tank.ModifiedScriptMessage.<init>(ModifiedScriptMessage.java:21)
        assertNotNull(result);
    }
}