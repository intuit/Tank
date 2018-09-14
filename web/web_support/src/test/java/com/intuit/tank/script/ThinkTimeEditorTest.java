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
import com.intuit.tank.script.ThinkTimeEditor;

/**
 * The class <code>ThinkTimeEditorTest</code> contains tests for the class <code>{@link ThinkTimeEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ThinkTimeEditorTest {
    /**
     * Run the ThinkTimeEditor() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testThinkTimeEditor_1()
        throws Exception {
        ThinkTimeEditor result = new ThinkTimeEditor();
        assertNotNull(result);
    }


    /**
     * Run the void done() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDone_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");

        fixture.done();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the void editThinkTime(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEditThinkTime_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");
        ScriptStep step = new ScriptStep();

        fixture.editThinkTime(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the void editThinkTime(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEditThinkTime_2()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");
        ScriptStep step = new ScriptStep();

        fixture.editThinkTime(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the void editThinkTime(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEditThinkTime_3()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");
        ScriptStep step = new ScriptStep();

        fixture.editThinkTime(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the void editThinkTime(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEditThinkTime_4()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");
        ScriptStep step = new ScriptStep();

        fixture.editThinkTime(step);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the String getButtonLabel() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetButtonLabel_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");

        String result = fixture.getButtonLabel();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
        assertNotNull(result);
    }

    /**
     * Run the String getMaxTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetMaxTime_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");

        String result = fixture.getMaxTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
        assertNotNull(result);
    }

    /**
     * Run the String getMinTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetMinTime_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");

        String result = fixture.getMinTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
        assertNotNull(result);
    }


    /**
     * Run the void insertThinkTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testInsertThinkTime_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");

        fixture.insertThinkTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the void setButtonLabel(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetButtonLabel_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");
        String buttonLabel = "";

        fixture.setButtonLabel(buttonLabel);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the void setMaxTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetMaxTime_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");
        String maxTime = "";

        fixture.setMaxTime(maxTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }

    /**
     * Run the void setMinTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetMinTime_1()
        throws Exception {
        ThinkTimeEditor fixture = new ThinkTimeEditor();
        fixture.setMinTime("");
        fixture.setMaxTime("");
        fixture.editThinkTime(new ScriptStep());
        fixture.setButtonLabel("");
        String minTime = "";

        fixture.setMinTime(minTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ThinkTimeEditor.setMinTime(ThinkTimeEditor.java:147)
    }
}