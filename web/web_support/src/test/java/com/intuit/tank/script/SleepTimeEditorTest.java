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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.SleepTimeEditor;

/**
 * The class <code>SleepTimeEditorTest</code> contains tests for the class <code>{@link SleepTimeEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class SleepTimeEditorTest {
    /**
     * Run the SleepTimeEditor() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSleepTimeEditor_1()
        throws Exception {
        SleepTimeEditor result = new SleepTimeEditor();
        assertNotNull(result);
    }


    /**
     * Run the void done() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testDone_1()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");

        fixture.done();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
    }

    /**
     * Run the void editSleepTime(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testEditSleepTime_1()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");
        ScriptStep step = new ScriptStep();

        fixture.editSleepTime(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
    }

    /**
     * Run the void editSleepTime(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testEditSleepTime_2()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");
        ScriptStep step = new ScriptStep();

        fixture.editSleepTime(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
    }

    /**
     * Run the String getButtonLabel() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetButtonLabel_1()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");

        String result = fixture.getButtonLabel();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
        assertNotNull(result);
    }

    /**
     * Run the String getSleepTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSleepTime_1()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");

        String result = fixture.getSleepTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
        assertNotNull(result);
    }


    /**
     * Run the void insertSleepTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInsertSleepTime_1()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");

        fixture.insertSleepTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
    }

    /**
     * Run the void setButtonLabel(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetButtonLabel_1()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");
        String buttonLabel = "";

        fixture.setButtonLabel(buttonLabel);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
    }

    /**
     * Run the void setSleepTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSleepTime_1()
        throws Exception {
        SleepTimeEditor fixture = new SleepTimeEditor();
        fixture.setSleepTime("");
        fixture.editSleepTime(new ScriptStep());
        fixture.setButtonLabel("");
        String sleepTime = "";

        fixture.setSleepTime(sleepTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SleepTimeEditor.setSleepTime(SleepTimeEditor.java:131)
    }
}