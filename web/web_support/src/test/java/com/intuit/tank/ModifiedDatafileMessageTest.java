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

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.project.DataFile;

/**
 * The class <code>ModifiedDatafileMessageTest</code> contains tests for the class <code>{@link ModifiedDatafileMessage}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ModifiedDatafileMessageTest {
    /**
     * Run the ModifiedDatafileMessage(DataFile,Object) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testModifiedDatafileMessage_1()
        throws Exception {
        DataFile modified = new DataFile();
        Object modifier = new Object();

        ModifiedDatafileMessage result = new ModifiedDatafileMessage(modified, modifier);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ModifiedEntityMessage.<init>(ModifiedEntityMessage.java:26)
        //       at com.intuit.tank.ModifiedDatafileMessage.<init>(ModifiedDatafileMessage.java:21)
        assertNotNull(result);
    }
}