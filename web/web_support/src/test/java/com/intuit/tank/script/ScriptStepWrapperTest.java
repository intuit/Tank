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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptStepWrapper;

/**
 * The class <code>ScriptStepWrapperTest</code> contains tests for the class <code>{@link ScriptStepWrapper}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ScriptStepWrapperTest {
    /**
     * Run the ScriptStepWrapper(ScriptStep) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testScriptStepWrapper_1()
            throws Exception {
        ScriptStep script = new ScriptStep();

        ScriptStepWrapper result = new ScriptStepWrapper(script);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
        assertNotNull(result);
    }

    /**
     * Run the String getComments() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetComments_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());
        fixture.getScriptStep().setComments("Test");
        String result = fixture.getComments();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
        assertNotNull(result);
    }


    /**
     * Run the String getMethod() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetMethod_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());
        fixture.getScriptStep().setMethod("Method");
        String result = fixture.getMethod();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
        assertNotNull(result);
    }

    /**
     * Run the String getMimetype() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetMimetype_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());
        fixture.getScriptStep().setMimetype("Tst");
        String result = fixture.getMimetype();
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());
        fixture.getScriptStep().setName("NAme");
        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
        assertNotNull(result);
    }

    /**
     * Run the String getScriptGroupName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetScriptGroupName_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());
        fixture.setScriptGroupName("Name");
        String result = fixture.getScriptGroupName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep getScriptStep() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetScriptStep_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());

        ScriptStep result = fixture.getScriptStep();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
        assertNotNull(result);
    }

    /**
     * Run the void setScriptGroupName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetScriptGroupName_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());
        String scriptGroupName = "";

        fixture.setScriptGroupName(scriptGroupName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
    }

    /**
     * Run the void setScriptStep(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetScriptStep_1()
            throws Exception {
        ScriptStepWrapper fixture = new ScriptStepWrapper(new ScriptStep());
        ScriptStep scriptStep = new ScriptStep();

        fixture.setScriptStep(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptStepWrapper.<init>(ScriptStepWrapper.java:17)
    }
}