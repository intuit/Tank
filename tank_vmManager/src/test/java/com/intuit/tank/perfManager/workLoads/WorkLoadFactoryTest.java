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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.perfManager.workLoads.WorkLoadFactory;
import com.intuit.tank.project.Workload;

/**
 * The class <code>WorkLoadFactoryTest</code> contains tests for the class <code>{@link WorkLoadFactory}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class WorkLoadFactoryTest {
    /**
     * Run the WorkLoadFactory() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testWorkLoadFactory_1()
            throws Exception {
        WorkLoadFactory result = new WorkLoadFactory();
        assertNotNull(result);
    }

    /**
     * Run the String buildScriptXml(String,Workload) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testBuildScriptXml_1()
            throws Exception {
        WorkLoadFactory fixture = new WorkLoadFactory();
        String jobId = "";
        Workload workload = new Workload();

        String result = fixture.buildScriptXml(jobId, workload);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.TankConfig.<init>(TankConfig.java:78)
        // at
        // com.intuit.tank.api.service.v1.project.ProjectServiceUrlBuilder.getScriptXmlUrl(ProjectServiceUrlBuilder.java:29)
        // at com.intuit.tank.perfManager.workLoads.WorkLoadFactory.buildScriptXml(WorkLoadFactory.java:175)
        assertNotNull(result);
    }

}