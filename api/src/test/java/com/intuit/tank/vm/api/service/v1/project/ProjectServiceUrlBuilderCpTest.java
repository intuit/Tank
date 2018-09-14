package com.intuit.tank.vm.api.service.v1.project;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.api.service.v1.project.ProjectServiceUrlBuilder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ProjectServiceUrlBuilderCpTest</code> contains tests for the class
 * <code>{@link ProjectServiceUrlBuilder}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class ProjectServiceUrlBuilderCpTest {
    /**
     * Run the String getScriptXmlUrl(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetScriptXmlUrl_1()
            throws Exception {
        String jobId = "";

        String result = ProjectServiceUrlBuilder.getScriptXmlUrl(jobId);

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
        // at com.intuit.tank.settings.TAnkConfig.<init>(TankConfig.java:78)
        // at
        // com.intuit.tank.api.service.v1.project.ProjectServiceUrlBuilder.getScriptXmlUrl(ProjectServiceUrlBuilder.java:29)
        assertNotNull(result);
    }
}