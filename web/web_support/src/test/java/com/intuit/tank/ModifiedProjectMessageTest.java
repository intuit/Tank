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

import com.intuit.tank.ModifiedProjectMessage;
import com.intuit.tank.project.Project;

/**
 * The class <code>ModifiedProjectMessageTest</code> contains tests for the class <code>{@link ModifiedProjectMessage}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ModifiedProjectMessageTest {
    /**
     * Run the ModifiedProjectMessage(Project,Object) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testModifiedProjectMessage_1()
        throws Exception {
        Project modified = new Project();
        Object modifier = new Object();

        ModifiedProjectMessage result = new ModifiedProjectMessage(modified, modifier);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }
}