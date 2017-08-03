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

import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.converter.ScriptStepHolder;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptEditor;

/**
 * The class <code>ScriptEditorTest</code> contains tests for the class <code>{@link ScriptEditor}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ScriptEditorTest {
    /**
     * Run the ScriptEditor() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testScriptEditor_1()
            throws Exception {
        ScriptEditor result = new ScriptEditor();
        assertNotNull(result);
    }

    /**
     * Run the String cancel() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    @org.junit.Ignore
    public void testCancel_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.cancel();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the void clearOrderList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testClearOrderList_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.clearOrderList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the List<String> complete(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testComplete_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String query = "";

        List<String> result = fixture.complete(query);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<String> complete(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testComplete_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String query = "";

        List<String> result = fixture.complete(query);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<String> complete(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testComplete_3()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String query = "";

        List<String> result = fixture.complete(query);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<String> complete(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testComplete_4()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String query = "";

        List<String> result = fixture.complete(query);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the void createScriptDetails() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateScriptDetails_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.createScriptDetails();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void deselectAll() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testDeselectAll_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.deselectAll();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void enterFilterMode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testEnterFilterMode_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.enterFilterMode();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the int getCurrentPage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCurrentPage_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        int result = fixture.getCurrentPage();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertEquals(1, result);
    }

    /**
     * Run the ScriptStep getCurrentStep() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCurrentStep_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        ScriptStep result = fixture.getCurrentStep();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<String> getGotoGroupList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetGotoGroupList_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<String> result = fixture.getGotoGroupList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getMaxThinkTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetMaxThinkTime_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getMaxThinkTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getMinThinkTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetMinThinkTime_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getMinThinkTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the int getNumRowsVisible() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetNumRowsVisible_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        int result = fixture.getNumRowsVisible();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertEquals(1, result);
    }

    /**
     * Run the List<ScriptStepHolder> getOrderList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetOrderList_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<ScriptStepHolder> result = fixture.getOrderList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptStepHolder> getOrderList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetOrderList_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<ScriptStepHolder> result = fixture.getOrderList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptStepHolder> getOrderList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetOrderList_3()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<ScriptStepHolder> result = fixture.getOrderList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getPrettyString(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPrettyString_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String s = "";
        String mimetype = "";

        String result = fixture.getPrettyString(s, mimetype);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getPrettyString(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPrettyString_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String s = "";
        String mimetype = "";

        String result = fixture.getPrettyString(s, mimetype);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getPrettyString(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPrettyString_3()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String s = "";
        String mimetype = "";

        String result = fixture.getPrettyString(s, mimetype);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getPrettyString(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPrettyString_4()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String s = "";
        String mimetype = "";

        String result = fixture.getPrettyString(s, mimetype);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getPrettyString(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPrettyString_5()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String s = "";
        String mimetype = "";

        String result = fixture.getPrettyString(s, mimetype);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep getPreviousRequest(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPreviousRequest_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep step = new ScriptStep();

        ScriptStep result = fixture.getPreviousRequest(step);
    }

    /**
     * Run the ScriptStep getPreviousRequest(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPreviousRequest_3()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep step = null;

        ScriptStep result = fixture.getPreviousRequest(step);
    }

    /**
     * Run the String getSaveAsName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSaveAsName_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getSaveAsName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the Script getScript() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetScript_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        Script result = fixture.getScript();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getScriptDetails() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetScriptDetails_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getScriptDetails();
    }

    /**
     * Run the String getScriptName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetScriptName_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getScriptName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptStep> getSelectedSteps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectedSteps_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<ScriptStep> result = fixture.getSelectedSteps();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep[] getSelectedStepsArr() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectedStepsArr_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        ScriptStep[] result = fixture.getSelectedStepsArr();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
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
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getSleepTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptStep> getSteps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSteps_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<ScriptStep> result = fixture.getSteps();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the TablePreferences getTablePrefs() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetTablePrefs_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        TablePreferences result = fixture.getTablePrefs();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the TableViewState getTableState() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetTableState_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        TableViewState result = fixture.getTableState();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getVariableKey() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetVariableKey_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getVariableKey();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the String getVariableValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetVariableValue_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        String result = fixture.getVariableValue();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertNotNull(result);
    }

    /**
     * Run the void insert(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInsert_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep step = new ScriptStep();

        fixture.insert(step);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void insertThinkTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInsertThinkTime_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.insertThinkTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void insertVariable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInsertVariable_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.insertVariable();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the boolean isAggregator(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsAggregator_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isAggregator(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isAggregator(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsAggregator_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isAggregator(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isClearVariable(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsClearVariable_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isClearVariable(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isClearVariable(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsClearVariable_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isClearVariable(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isCookieStep(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsCookieStep_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isCookieStep(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isCookieStep(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsCookieStep_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isCookieStep(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isCopyEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsCopyEnabled_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.isCopyEnabled();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isDeleteEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsDeleteEnabled_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.isDeleteEnabled();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isDeleteEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsDeleteEnabled_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.isDeleteEnabled();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isLogicStep(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsLogicStep_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isLogicStep(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isLogicStep(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsLogicStep_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isLogicStep(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRequest(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsRequest_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isRequest(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(result);
    }

    /**
     * Run the boolean isRequest(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsRequest_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isRequest(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(result);
    }

    /**
     * Run the boolean isSleepTime(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsSleepTime_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isSleepTime(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isSleepTime(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsSleepTime_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isSleepTime(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isThinkTime(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsThinkTime_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isThinkTime(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isVariable(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsVariable_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isVariable(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the boolean isVariable(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsVariable_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep scriptStep = new ScriptStep();

        boolean result = fixture.isVariable(scriptStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
        assertTrue(!result);
    }

    /**
     * Run the void populateGroupList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testPopulateGroupList_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.populateGroupList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void populateGroupList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testPopulateGroupList_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.populateGroupList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void populateGroupList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testPopulateGroupList_3()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.populateGroupList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void populateGroupList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testPopulateGroupList_4()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.populateGroupList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void reIndexAndCheck() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testReIndexAndCheck_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.reIndexAndCheck();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void reIndexAndCheck() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testReIndexAndCheck_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.reIndexAndCheck();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void reIndexAndCheck() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testReIndexAndCheck_3()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.reIndexAndCheck();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void reindexScriptSteps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testReindexScriptSteps_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.reindexScriptSteps();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void reindexScriptSteps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testReindexScriptSteps_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.reindexScriptSteps();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void selectAll() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSelectAll_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.selectAll();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setCurrentPage(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetCurrentPage_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        int currentPage = 1;

        fixture.setCurrentPage(currentPage);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setCurrentStep(ScriptStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetCurrentStep_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep currentStep = new ScriptStep();

        fixture.setCurrentStep(currentStep);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setMaxThinkTime(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetMaxThinkTime_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String maxThinkTime = "";

        fixture.setMaxThinkTime(maxThinkTime);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setMinThinkTime(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetMinThinkTime_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String minThinkTime = "";

        fixture.setMinThinkTime(minThinkTime);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setNumRowsVisible(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetNumRowsVisible_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        int numRowsVisible = 1;

        fixture.setNumRowsVisible(numRowsVisible);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setOrderList(List<ScriptStepHolder>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetOrderList_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<ScriptStepHolder> list = new LinkedList();

        fixture.setOrderList(list);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setOrderList(List<ScriptStepHolder>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetOrderList_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<ScriptStepHolder> list = new LinkedList();

        fixture.setOrderList(list);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setOrderList(List<ScriptStepHolder>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetOrderList_3()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<ScriptStepHolder> list = new LinkedList();

        fixture.setOrderList(list);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setOrderList(List<ScriptStepHolder>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetOrderList_4()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<ScriptStepHolder> list = new LinkedList();

        fixture.setOrderList(list);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setSaveAsName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSaveAsName_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String saveAsName = "";

        fixture.setSaveAsName(saveAsName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setScript(Script) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetScript_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        Script script1 = new Script();
        script1.setName("");

        fixture.setScript(script1);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Script.setName(Script.java:133)
    }

    /**
     * Run the void setScriptName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetScriptName_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String name = "";

        fixture.setScriptName(name);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setSelectedSteps(List<ScriptStep>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectedSteps_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<ScriptStep> selectedSteps = new LinkedList();

        fixture.setSelectedSteps(selectedSteps);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setSelectedStepsArr(ScriptStep[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectedStepsArr_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep[] selectedStepsArr = new ScriptStep[] {};

        fixture.setSelectedStepsArr(selectedStepsArr);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setSelectedStepsArr(ScriptStep[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectedStepsArr_2()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ScriptStep[] selectedStepsArr = new ScriptStep[] {};

        fixture.setSelectedStepsArr(selectedStepsArr);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
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
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String sleepTime = "";

        fixture.setSleepTime(sleepTime);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setSteps(List<ScriptStep>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSteps_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<ScriptStep> steps = new LinkedList();

        fixture.setSteps(steps);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setVariableKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetVariableKey_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String variableKey = "";

        fixture.setVariableKey(variableKey);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    /**
     * Run the void setVariableValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetVariableValue_1()
            throws Exception {
        ScriptEditor fixture = new ScriptEditor();
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList());
        fixture.setSelectedSteps(new LinkedList());
        fixture.setSaveAsName("");
        Script script = new Script();
        script.setName("");
        fixture.setScript(script);
        fixture.setSelectedStepsArr(new ScriptStep[] {});
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String variableValue = "";

        fixture.setVariableValue(variableValue);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }
}