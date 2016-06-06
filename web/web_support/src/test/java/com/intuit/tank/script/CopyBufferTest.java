package com.intuit.tank.script;

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

import java.util.List;

import org.junit.*;
import org.picketlink.idm.model.basic.User;

import static org.junit.Assert.*;

import com.intuit.tank.auth.TankUser;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.CopyBuffer;

/**
 * The class <code>CopyBufferTest</code> contains tests for the class <code>{@link CopyBuffer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class CopyBufferTest {
    /**
     * Run the CopyBuffer() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCopyBuffer_1()
        throws Exception {
        CopyBuffer result = new CopyBuffer();
        assertNotNull(result);
    }

    /**
     * Run the void clear() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testClear_1()
        throws Exception {
        CopyBuffer fixture = new CopyBuffer();

        fixture.clear();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.CopyBuffer.clear(CopyBuffer.java:39)
    }

    /**
     * Run the List<ScriptStep> getBuffer() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetBuffer_1()
        throws Exception {
        CopyBuffer fixture = new CopyBuffer();

        List<ScriptStep> result = fixture.getBuffer();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.CopyBuffer.getBuffer(CopyBuffer.java:32)
        assertNotNull(result);
    }

    /**
     * Run the boolean isPasteEnabled() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsPasteEnabled_1()
        throws Exception {
        CopyBuffer fixture = new CopyBuffer();

        boolean result = fixture.isPasteEnabled();
        assertTrue(!result);
    }

    /**
     * Run the boolean isPasteEnabled() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsPasteEnabled_2()
        throws Exception {
        CopyBuffer fixture = new CopyBuffer();

        boolean result = fixture.isPasteEnabled();
        assertTrue(!result);
    }

    /**
     * Run the void observeLogin(User) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testObserveLogin_1()
        throws Exception {
        CopyBuffer fixture = new CopyBuffer();
        User user = new TankUser(new com.intuit.tank.project.User());

        fixture.observeLogin(user);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
    }
}