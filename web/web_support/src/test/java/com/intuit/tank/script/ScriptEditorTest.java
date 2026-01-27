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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.auth.Security;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.filter.FilterBean;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.script.util.ScriptFilterUtil;
import com.intuit.tank.util.Messages;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.validation.constraints.AssertTrue;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.intuit.tank.converter.ScriptStepHolder;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import org.mockito.*;

import jakarta.enterprise.context.Conversation;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.security.enterprise.CallerPrincipal;

@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class ScriptEditorTest {

    @InjectMocks
    private ScriptEditor fixture;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private Messages messages;

    @Mock
    private ScriptSearchBean searchBean;

    @Mock
    private FilterBean filterBean;

    @Mock
    private AggregatorEditor aggregatorEditor;

    @Mock
    private Security security;

    @Mock
    private Conversation conversation;

    @Mock
    private Event<ModifiedScriptMessage> scriptEvent;

    @Mock
    private CopyBuffer copyBuffer;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void testScriptEditor() {
        assertNotNull(fixture);
    }

    @Test
    public void testDeleteRequest() {
        ScriptStep step = ScriptStep.builder().name("ScriptStep").build();
        when(aggregatorEditor.isAggregator(step)).thenReturn(Boolean.TRUE);
        when(aggregatorEditor.getAggregatorPair(step)).thenReturn(new ScriptStep());
        List<ScriptStep> steps = new LinkedList<>();
        steps.add(step);
        steps.add(ScriptStep.builder().name("ScriptStep").build());
        fixture.setSteps(steps);
        doNothing().when(searchBean).removeFromSearchMatch(any());

        fixture.deleteRequest(step);

        assertEquals(1, fixture.getSteps().size());
    }

    @Test
    public void testPaste() {
        List<ScriptStep> steps = new LinkedList<>();
        steps.add(ScriptStep.builder().name("ScriptStep").build());
        steps.add(ScriptStep.builder().name("ScriptStep").build());
        fixture.setSteps(steps);
        when(copyBuffer.getBuffer()).thenReturn(List.of(ScriptStep.builder().name("ScriptStep").build()));

        fixture.paste();

        assertEquals(3, fixture.getSteps().size());
    }

    @Test
    public void testEditScript() {
        Script script = Script.builder().name("Script").creator("TESTER").build();
        script = new ScriptDao().saveOrUpdate(script);

        when(conversation.isTransient()).thenReturn(Boolean.TRUE);
        when(security.hasRight(any())).thenReturn(Boolean.TRUE);

        assertEquals("success", fixture.editScript(script));

        when(conversation.isTransient()).thenReturn(Boolean.TRUE);
        when(security.hasRight(any())).thenReturn(Boolean.FALSE);

        assertEquals("success", fixture.editScript(script));
        verify(messages).warn("You do not have permission to edit this script.");
    }

    @Test
    public void testDeleteSelected() {
        when(aggregatorEditor.isAggregator(any())).thenReturn(Boolean.FALSE);
        ScriptStep step = ScriptStep.builder().name("ScriptStep2").build();
        List<ScriptStep> steps = new LinkedList<>();
        steps.add(ScriptStep.builder().name("ScriptStep1").build());
        steps.add(step);
        steps.add(ScriptStep.builder().name("ScriptStep3").build());
        fixture.setSteps(steps);
        List<ScriptStep> selectedSteps = new LinkedList<>();
        selectedSteps.add(step);
        fixture.setSelectedSteps(selectedSteps);

        fixture.deleteSelected();

        assertTrue(fixture.getSelectedSteps().isEmpty());
        verify(messages).info("Deleted 1 selected Script Steps.");
    }

    /**
     * Run the String cancel() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    @Disabled
    public void testCancel_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testClearOrderList_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testComplete_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        String query = "TEST";

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
    public void testComplete_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testComplete_3() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testComplete_4() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testCreateScriptDetails_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testDeselectAll_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testEnterFilterMode_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetCurrentPage_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetCurrentStep_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetGotoGroupList_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetMaxThinkTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetMinThinkTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetNumRowsVisible_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetOrderList_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(List.of(ScriptStep.builder().build()));
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

        List<ScriptStepHolder> result = fixture.getOrderList();

        assertEquals(1, result.size());

        fixture.getOrderList();
    }

    /**
     * Run the List<ScriptStepHolder> getOrderList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetOrderList_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

        List<ScriptStepHolder> result = fixture.getOrderList();

        assertTrue(result.isEmpty());
    }

    /**
     * Run the List<ScriptStepHolder> getOrderList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetOrderList_3() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

        List<ScriptStepHolder> result = fixture.getOrderList();

        assertTrue(result.isEmpty());
    }

    /**
     * Run the String getPrettyString(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPrettyString_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testGetPrettyString_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testGetPrettyString_3() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testGetPrettyString_4() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testGetPrettyString_5() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testGetPreviousRequest_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testGetPreviousRequest_3() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testGetSaveAsName_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetScript_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetScriptDetails_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetScriptName_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetSelectedSteps_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

        List<ScriptStep> result = fixture.getSelectedSteps();

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
    public void testGetSleepTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetSteps_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetTablePrefs_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetTableState_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetVariableKey_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testGetVariableValue_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testInsert_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList<>());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        ScriptStep step = new ScriptStep();

        fixture.insert(step);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    @Test
    public void testInsertSleepTime() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        List<ScriptStep> steps = new LinkedList<>();
        steps.add(ScriptStep.builder().name("ScriptStep1").build());
        steps.add(ScriptStep.builder().name("ScriptStep2").build());
        fixture.setSteps(steps);
        fixture.setSelectedSteps(steps);
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

        fixture.insertSleepTime();
    }

    @Test
    public void testInsertThinkTime() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList<>());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

        fixture.insertThinkTime();
    }

    /**
     * Run the void insertVariable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInsertVariable_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(new LinkedList<>());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testIsAggregator_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsAggregator_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsClearVariable_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsClearVariable_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsCookieStep_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsCookieStep_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsCopyEnabled_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testIsDeleteEnabled_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testIsDeleteEnabled_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testIsLogicStep_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsLogicStep_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsRequest_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsRequest_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsSleepTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsSleepTime_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsThinkTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsVariable_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testIsVariable_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testPopulateGroupList_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("Script")
                .steps(List.of(ScriptStep.builder().name("ScriptStep").scriptGroupName("GroupName").build()))
                .build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testPopulateGroupList_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testPopulateGroupList_3() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testPopulateGroupList_4() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testReIndexAndCheck_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testReIndexAndCheck_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testReIndexAndCheck_3() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testReindexScriptSteps_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testReindexScriptSteps_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testSelectAll_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());

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
    public void testSetCurrentPage_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetCurrentStep_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetMaxThinkTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetMinThinkTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetNumRowsVisible_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetOrderList_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        List<ScriptStep> steps = new LinkedList<>();
        steps.add(ScriptStep.builder().name("ScriptStep1").build());
        steps.add(ScriptStep.builder().name("ScriptStep2").build());
        fixture.setSteps(steps);
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        List<ScriptStepHolder> list = List.of(new ScriptStepHolder(0,"uuid" , "TEST"));

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
    public void testSetOrderList_2() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        List<ScriptStepHolder> list = Collections.emptyList();

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
    public void testSetOrderList_3() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        List<ScriptStepHolder> list = Collections.emptyList();

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
    public void testSetOrderList_4() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        List<ScriptStepHolder> list = Collections.emptyList();

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
    public void testSetSaveAsName_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetScript_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetScriptName_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetSelectedSteps_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        List<ScriptStep> selectedSteps = Collections.emptyList();

        fixture.setSelectedSteps(selectedSteps);

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
    public void testSetSleepTime_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetSteps_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        List<ScriptStep> steps = Collections.emptyList();

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
    public void testSetVariableKey_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
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
    public void testSetVariableValue_1() {
        fixture.setNumRowsVisible(1);
        fixture.setVariableValue("");
        fixture.setCurrentPage(1);
        fixture.setSleepTime("");
        fixture.setMinThinkTime("");
        fixture.setSteps(Collections.emptyList());
        fixture.setSelectedSteps(Collections.emptyList());
        fixture.setSaveAsName("");
        fixture.setScript(Script.builder().name("").build());
        fixture.setMaxThinkTime("");
        fixture.setVariableKey("");
        fixture.setCurrentStep(new ScriptStep());
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(Collections.emptyList());
        String variableValue = "";

        fixture.setVariableValue(variableValue);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.ScriptEditor.setNumRowsVisible(ScriptEditor.java:310)
    }

    @Test
    public void testSaveAsNoName() {
        fixture.saveAs();
        verify(messages).error("You must give the script a name.");
    }

    @Test
    public void testSaveAsToSaveException() {
        Script script = Script.builder().name("Name").build();
        fixture.setScript(script);
        fixture.setSaveAsName("Name");

        fixture.saveAs();

        verify(messages).error(anyString());
    }

    @Test
    public void testSaveAsToSave() {
        doNothing().when(scriptEvent).fire(any());
        Script script = new ScriptDao().saveOrUpdate(
                Script.builder().name("Name").creator("TESTER").build());
        fixture.setScript(script);
        fixture.setSaveAsName("Name");

        fixture.saveAs();

        verify(messages).info("Script Name has been saved.");
    }

    @Test
    public void testSaveAs() {
        CallerPrincipal callerPrincipal = new CallerPrincipal("LoginName");
        when(securityContext.getCallerPrincipal()).thenReturn(callerPrincipal);
        Script script = Script.builder().name("Name").build();
        fixture.setScript(script);
        fixture.setSaveAsName("SaveAsName");
        fixture.saveAs();
    }

    @Test
    public void testPrettyStringJSON() {
        String input = "[{\"test\": \"fun\"}]";
        String output = "[\n  {\n    \"test\": \"fun\"\n  }\n]";
        assertEquals(output, fixture.getPrettyString( input, "json"));
    }

    @Test
    public void testPrettyStringXML() {
        String input = "<test><fun><yes/></fun></test>";
        String output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<test>\n  <fun>\n    <yes/>\n  </fun>\n</test>";

        assertEquals(output, fixture.getPrettyString( input, "xml"));
    }

    @Test
    public void testReapplyFilters() {
        FacesContext facesContext = mock(FacesContext.class);

        try (MockedStatic<FacesContext> mockedFacesContext = Mockito.mockStatic(FacesContext.class)) {
            ExternalContext externalContext = mock(ExternalContext.class);
            Flash flash = mock(Flash.class);
            when(facesContext.getExternalContext()).thenReturn(externalContext);
            when(externalContext.getFlash()).thenReturn(flash);
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            fixture.setScript(Script.builder().name("Name").build());
            AWSXRay.beginSegment("test");
            assertEquals("success", fixture.reapplyFilters());
            AWSXRay.endSegment();
        }
    }

    @Test
    public void testReapplyFilters_Exception() {
        FacesContext facesContext = mock(FacesContext.class);

        try (MockedStatic<ScriptFilterUtil> mockedscriptFilterUtil = Mockito.mockStatic(ScriptFilterUtil.class);
             MockedStatic<FacesContext> mockedFacesContext = Mockito.mockStatic(FacesContext.class)) {
            ExternalContext externalContext = mock(ExternalContext.class);
            Flash flash = mock(Flash.class);
            when(facesContext.getExternalContext()).thenReturn(externalContext);
            when(externalContext.getFlash()).thenReturn(flash);
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            mockedscriptFilterUtil.when(() -> ScriptFilterUtil.applyFilters(anyList(), any(Script.class))).thenThrow(new RuntimeException("failure"));

            fixture.setScript(Script.builder().name("Name").build());
            assertEquals("failure", fixture.reapplyFilters());
        }
    }

    @Test
    public void testCanel() {
        assertEquals("success", fixture.cancel());
    }
}