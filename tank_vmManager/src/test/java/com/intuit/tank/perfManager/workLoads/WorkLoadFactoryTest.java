package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.project.Workload;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>WorkLoadFactoryTest</code> contains tests for the class <code>{@link WorkLoadFactory}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class WorkLoadFactoryTest {

    private static WorkLoadFactory fixture;

    @BeforeAll
    public static void constructor() {
        fixture = new WorkLoadFactory();
    }
    /**
     * Run the WorkLoadFactory() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testWorkLoadFactory()
            throws Exception {
        assertNotNull(fixture);
    }

    /**
     * Run the String buildScriptXml(String,Workload) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testBuildScriptXml()
            throws Exception {
        String jobId = "";
        Workload workload = new Workload();
        String result = fixture.buildScriptXml(jobId, workload);

        assertNotNull(result);
    }
}