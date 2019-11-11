package com.intuit.tank.transform.scriptGenerator;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.test.TestGroups;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class ApiScriptGeneratorTest {

    @Test
    @Tag(TestGroups.EXPERIMENTAL)
    @Disabled
    public void testGenerate() throws Exception {
//         Script script = ScriptDataGenerator.createScript();
//         List<ScriptStep> steps = ScriptDataGenerator.getScriptSteps(300);
//         script.getScriptSteps().addAll(steps);
//
//         ApiScriptGeneratorImpl api = new ApiScriptGeneratorImpl();
//         api.addData("testPlanName", script.getName());
//         api.addData("testPlanDescription", "Description's for " + script.getName());
//         api.addData("testSuiteName", "Suite for " + script.getName());
//         api.addData("testSuiteDescription", "Suite \"Description\" for " + script.getName());
//         long start = System.currentTimeMillis();
//         String xml = api.generate(steps);
//         long end = System.currentTimeMillis();
//         System.out.println("Jaxb took " + (end - start) + "ms.");
//         File dir = new File("test-output");
//         dir.mkdirs();
//         File f = new File(dir, "testScriptApiGenerate.xml");
//         System.out.println("Writing xml output to: " + f.getAbsolutePath());
//         FileUtils.writeStringToFile(f, xml, "UTF-8");
    }

}
