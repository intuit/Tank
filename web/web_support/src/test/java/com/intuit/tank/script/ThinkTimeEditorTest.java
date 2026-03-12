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

import com.intuit.tank.util.Messages;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.ThinkTimeEditor;

import java.util.HashSet;
import java.util.Set;

/**
 * The class <code>ThinkTimeEditorTest</code> contains tests for the class <code>{@link ThinkTimeEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ThinkTimeEditorTest {

    @InjectMocks
    private ThinkTimeEditor thinkTimeEditor;

    @Mock
    private ScriptEditor scriptEditor;

    @Mock
    private Messages messages;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testThinkTimeEditor_1()
        throws Exception {
        ThinkTimeEditor result = new ThinkTimeEditor();
        assertNotNull(result);
    }

    @Test
    public void testAddToScript_WhenValidDigits_InsertsStep() {
        thinkTimeEditor.insertThinkTime();
        thinkTimeEditor.setMinTime("100");
        thinkTimeEditor.setMaxTime("500");
        thinkTimeEditor.addToScript();
        verify(scriptEditor).insert(any(ScriptStep.class));
    }

    @Test
    public void testAddToScript_WhenMinGreaterThanMax_ShowsError() {
        thinkTimeEditor.insertThinkTime();
        thinkTimeEditor.setMinTime("500");
        thinkTimeEditor.setMaxTime("100");
        thinkTimeEditor.addToScript();
        verify(messages).error(anyString());
        verify(scriptEditor, never()).insert(any());
    }

    @Test
    public void testAddToScript_WhenInvalidMinTime_ShowsError() {
        thinkTimeEditor.insertThinkTime();
        thinkTimeEditor.setMinTime("notanumber");
        thinkTimeEditor.setMaxTime("500");
        thinkTimeEditor.addToScript();
        verify(messages).error(anyString());
        verify(scriptEditor, never()).insert(any());
    }

    @Test
    public void testAddToScript_WhenInvalidMaxTime_ShowsError() {
        thinkTimeEditor.insertThinkTime();
        thinkTimeEditor.setMinTime("100");
        thinkTimeEditor.setMaxTime("notanumber");
        thinkTimeEditor.addToScript();
        verify(messages).error(anyString());
        verify(scriptEditor, never()).insert(any());
    }

    @Test
    public void testAddToScript_WhenEditMode_CallsDone() {
        ScriptStep step = new ScriptStep();
        thinkTimeEditor.editThinkTime(step);
        thinkTimeEditor.setMinTime("100");
        thinkTimeEditor.setMaxTime("500");
        thinkTimeEditor.addToScript();
        // done() was called - step should have data
        assertNotNull(step.getData());
    }


    @Test
    public void testEditThinkTime_WithExistingMinMaxData_PopulatesFields() {
        ScriptStep step = new ScriptStep();
        Set<RequestData> data = new HashSet<>();
        data.add(new RequestData(ScriptConstants.MIN_TIME, "200", "string"));
        data.add(new RequestData(ScriptConstants.MAX_TIME, "800", "string"));
        step.setData(data);

        thinkTimeEditor.editThinkTime(step);

        assertEquals("200", thinkTimeEditor.getMinTime());
        assertEquals("800", thinkTimeEditor.getMaxTime());
    }

    @Test
    public void testInsert_CallsScriptEditorInsert() {
        thinkTimeEditor.setMinTime("100");
        thinkTimeEditor.setMaxTime("500");
        thinkTimeEditor.insert();
        verify(scriptEditor).insert(any(ScriptStep.class));
    }

    @Test
    public void testDone_WithStep_UpdatesStepData() {
        ScriptStep step = new ScriptStep();
        thinkTimeEditor.editThinkTime(step);
        thinkTimeEditor.setMinTime("150");
        thinkTimeEditor.setMaxTime("600");
        thinkTimeEditor.done();
        assertEquals("", thinkTimeEditor.getMinTime());
        assertEquals("", thinkTimeEditor.getMaxTime());
    }

    @Test
    public void testInsertThinkTime_SetsInsertMode() {
        thinkTimeEditor.insertThinkTime();
        assertEquals("", thinkTimeEditor.getMinTime());
        assertEquals("", thinkTimeEditor.getMaxTime());
        assertEquals("Add", thinkTimeEditor.getButtonLabel());
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