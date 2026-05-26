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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.util.Messages;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.intuit.tank.util.ButtonLabel.ADD_LABEL;
import static com.intuit.tank.util.ButtonLabel.EDIT_LABEL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AggregatorEditorTest {

    @InjectMocks
    private AggregatorEditor fixture;

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
    public void testConstructor() {
        assertNotNull(fixture);
    }

    @Test
    public void testGetSetAggregatorName() {
        fixture.setAggregatorName("MyAggregator");
        assertEquals("MyAggregator", fixture.getAggregatorName());
    }

    @Test
    public void testGetSetButtonLabel() {
        fixture.setButtonLabel("Save");
        assertEquals("Save", fixture.getButtonLabel());
    }

    @Test
    public void testIsCanBeAggregated_InitiallyFalse() {
        assertFalse(fixture.isCanBeAggregated());
    }

    @Test
    public void testInsertAggregator_ResetsState() {
        fixture.setAggregatorName("SomeName");
        fixture.setButtonLabel(EDIT_LABEL);

        fixture.insertAggregator();

        assertEquals("", fixture.getAggregatorName());
        assertEquals(ADD_LABEL, fixture.getButtonLabel());
    }

    @Test
    public void testEditAggregator_SetsEditMode() {
        ScriptStep step = new ScriptStep();
        Set<RequestData> data = new HashSet<>();
        RequestData rd = new RequestData();
        rd.setKey(ScriptConstants.LOGGING_KEY);
        rd.setValue("myLabel");
        data.add(rd);
        step.setData(data);

        fixture.editAggregator(step);

        assertEquals(EDIT_LABEL, fixture.getButtonLabel());
        assertEquals("myLabel", fixture.getAggregatorName());
    }

    @Test
    public void testEditAggregator_WithNoLoggingKey_KeepsCurrentName() {
        fixture.setAggregatorName("existing");
        ScriptStep step = new ScriptStep();
        // no data set = empty set, so no logging key found

        fixture.editAggregator(step);

        assertEquals(EDIT_LABEL, fixture.getButtonLabel());
        assertEquals("existing", fixture.getAggregatorName());
    }

    @Test
    public void testIsAggregator_WhenTimerWithLoggingKey_ReturnsTrue() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.TIMER);

        Set<RequestData> data = new HashSet<>();
        RequestData rd = new RequestData();
        rd.setKey(ScriptConstants.LOGGING_KEY);
        rd.setValue("someAgg");
        data.add(rd);
        step.setData(data);

        assertTrue(fixture.isAggregator(step));
    }

    @Test
    public void testIsAggregator_WhenNotTimerType_ReturnsFalse() {
        ScriptStep step = new ScriptStep();
        step.setType("REQUEST");

        Set<RequestData> data = new HashSet<>();
        RequestData rd = new RequestData();
        rd.setKey(ScriptConstants.LOGGING_KEY);
        rd.setValue("someAgg");
        data.add(rd);
        step.setData(data);

        assertFalse(fixture.isAggregator(step));
    }

    @Test
    public void testIsAggregator_WhenNoData_ReturnsFalse() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.TIMER);

        assertFalse(fixture.isAggregator(step));
    }

    @Test
    public void testIsStart_WhenStartTimer_ReturnsTrue() {
        ScriptStep step = new ScriptStep();

        Set<RequestData> data = new HashSet<>();
        RequestData rd = new RequestData();
        rd.setKey(ScriptConstants.IS_START);
        rd.setValue(TimerAction.START.name());
        data.add(rd);
        step.setData(data);

        assertTrue(fixture.isStart(step));
    }

    @Test
    public void testIsStart_WhenStopTimer_ReturnsFalse() {
        ScriptStep step = new ScriptStep();

        Set<RequestData> data = new HashSet<>();
        RequestData rd = new RequestData();
        rd.setKey(ScriptConstants.IS_START);
        rd.setValue(TimerAction.STOP.name());
        data.add(rd);
        step.setData(data);

        assertFalse(fixture.isStart(step));
    }

    @Test
    public void testIsStart_WhenNoData_ReturnsFalse() {
        ScriptStep step = new ScriptStep();
        assertFalse(fixture.isStart(step));
    }

    @Test
    public void testCheckFixOrder_WhenNotStart_DoesNothing() {
        ScriptStep step = new ScriptStep();
        // No IS_START data - should do nothing
        assertDoesNotThrow(() -> fixture.checkFixOrder(step));
    }

    @Test
    public void testAggregateCheck_WhenSingleStep_CannotAggregate() {
        List<ScriptStep> steps = new ArrayList<>();
        ScriptStep s = new ScriptStep();
        s.setStepIndex(0);
        steps.add(s);
        when(scriptEditor.getSelectedSteps()).thenReturn(steps);
        when(scriptEditor.getSteps()).thenReturn(steps);

        fixture.aggregateCheck();

        assertFalse(fixture.isCanBeAggregated());
    }

    @Test
    public void testAggregateCheck_WhenContiguousSteps_CanAggregate() {
        List<ScriptStep> selectedSteps = new ArrayList<>();
        ScriptStep s1 = new ScriptStep();
        s1.setStepIndex(1);
        ScriptStep s2 = new ScriptStep();
        s2.setStepIndex(2);
        selectedSteps.add(s1);
        selectedSteps.add(s2);

        when(scriptEditor.getSelectedSteps()).thenReturn(selectedSteps);
        when(scriptEditor.getSteps()).thenReturn(selectedSteps);

        fixture.aggregateCheck();

        assertTrue(fixture.isCanBeAggregated());
    }

    @Test
    public void testAggregateCheck_WhenNonContiguousSteps_CannotAggregate() {
        List<ScriptStep> selectedSteps = new ArrayList<>();
        ScriptStep s1 = new ScriptStep();
        s1.setStepIndex(1);
        ScriptStep s2 = new ScriptStep();
        s2.setStepIndex(3); // gap at 2
        selectedSteps.add(s1);
        selectedSteps.add(s2);

        when(scriptEditor.getSelectedSteps()).thenReturn(selectedSteps);
        when(scriptEditor.getSteps()).thenReturn(selectedSteps);

        fixture.aggregateCheck();

        assertFalse(fixture.isCanBeAggregated());
    }

    @Test
    public void testAddToScript_InInsertMode_CallsInsert() {
        // In insert mode, addToScript calls insert() which needs selectedSteps
        List<ScriptStep> selectedSteps = new ArrayList<>();
        ScriptStep s = new ScriptStep();
        s.setStepIndex(1);
        selectedSteps.add(s);
        when(scriptEditor.getSelectedSteps()).thenReturn(selectedSteps);

        fixture.insertAggregator(); // sets editMode = false
        fixture.addToScript();

        // verify insert was called
        verify(scriptEditor, atLeastOnce()).insert(any(ScriptStep.class), anyInt());
    }

    private ScriptStep buildAggregatorStep(String loggingKey, String pairUuid, String isStartValue) {
        ScriptStep step = new ScriptStep();
        Set<RequestData> data = new HashSet<>();
        if (loggingKey != null) {
            data.add(new RequestData(ScriptConstants.LOGGING_KEY, loggingKey, "string"));
        }
        if (pairUuid != null) {
            data.add(new RequestData(ScriptConstants.AGGREGATOR_PAIR, pairUuid, "string"));
        }
        if (isStartValue != null) {
            data.add(new RequestData(ScriptConstants.IS_START, isStartValue, "string"));
        }
        step.setData(data);
        return step;
    }

    @Test
    public void testAddToScript_InEditMode_CallsEdit() {
        // Create pair step (the partner of the step we're editing)
        ScriptStep pairStep = buildAggregatorStep("OldName", null, null);
        pairStep.setUuid(java.util.UUID.randomUUID().toString());

        // Create the step to edit, pointing to the pair
        ScriptStep step = buildAggregatorStep("OldName", pairStep.getUuid(), null);

        // scriptEditor.getSteps() returns the pair
        when(scriptEditor.getSteps()).thenReturn(List.of(pairStep));

        fixture.editAggregator(step); // sets editMode = true
        fixture.setAggregatorName("NewName");
        fixture.addToScript(); // should call edit()

        // After edit(), the logging key of both steps should be updated to "NewName"
        assertEquals("NewName", step.getData().stream()
                .filter(rd -> rd.getKey().equals(ScriptConstants.LOGGING_KEY))
                .findFirst().map(RequestData::getValue).orElse(""));
    }

    @Test
    public void testGetAggregatorPair_FindsPairByUuid() {
        ScriptStep pairStep = new ScriptStep();
        pairStep.setUuid(java.util.UUID.randomUUID().toString());

        ScriptStep step = buildAggregatorStep(null, pairStep.getUuid(), null);
        when(scriptEditor.getSteps()).thenReturn(List.of(pairStep));

        ScriptStep result = fixture.getAggregatorPair(step);
        assertEquals(pairStep, result);
    }

    @Test
    public void testCheckFixOrder_WhenStartAndIndexGreaterThanPair_SwapsIndices() {
        ScriptStep pairStep = buildAggregatorStep("label", null, null);
        pairStep.setUuid(java.util.UUID.randomUUID().toString());
        pairStep.setStepIndex(1); // pair has lower index

        ScriptStep step = buildAggregatorStep("label", pairStep.getUuid(), TimerAction.START.name());
        step.setStepIndex(5); // start has higher index - needs swap

        when(scriptEditor.getSteps()).thenReturn(List.of(pairStep));

        fixture.checkFixOrder(step);

        // After fix, start should have lower index
        assertEquals(1, step.getStepIndex());
        assertEquals(5, pairStep.getStepIndex());
    }

    @Test
    public void testCheckFixOrder_WhenStartAndIndexLessThanPair_NoSwap() {
        ScriptStep pairStep = buildAggregatorStep("label", null, null);
        pairStep.setUuid(java.util.UUID.randomUUID().toString());
        pairStep.setStepIndex(5);

        ScriptStep step = buildAggregatorStep("label", pairStep.getUuid(), TimerAction.START.name());
        step.setStepIndex(1); // start already has lower index

        when(scriptEditor.getSteps()).thenReturn(List.of(pairStep));

        fixture.checkFixOrder(step);

        // No swap needed
        assertEquals(1, step.getStepIndex());
        assertEquals(5, pairStep.getStepIndex());
    }
}
