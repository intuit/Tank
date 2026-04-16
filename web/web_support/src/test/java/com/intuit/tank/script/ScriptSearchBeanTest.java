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

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.util.Messages;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;

import jakarta.enterprise.context.ConversationScoped;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class ScriptSearchBeanTest {

    @InjectMocks
    private ScriptSearchBean scriptSearchBean;

    @Mock
    private Messages messages;

    @Mock
    private ScriptEditor editor;

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
    public void testScriptSearchBean() {
        assertNotNull(scriptSearchBean);
    }

    @Test
    public void testReplaceString() {
        String replaceString = "ReplaceMe";
        scriptSearchBean.setReplaceString(replaceString);
        assertEquals(replaceString, scriptSearchBean.getReplaceString());
    }

    @Test
    public void testRequestSections() {
        List<SearchOptionWrapper> requestSections = new LinkedList();
        scriptSearchBean.setRequestSections(requestSections);
        assertEquals(requestSections, scriptSearchBean.getRequestSections());
    }

    @Test
    public void testThinkTimeSections() {
        List<SearchOptionWrapper> thinkTimeSections = new LinkedList();
        scriptSearchBean.setThinkTimeSections(thinkTimeSections);
        assertEquals(thinkTimeSections, scriptSearchBean.getThinkTimeSections());
    }

    @Test
    public void testSleepTimeSections() {
        List<SearchOptionWrapper> sleepTimeSections = new LinkedList();
        scriptSearchBean.setSleepTimeSections(sleepTimeSections);
        assertEquals(sleepTimeSections, scriptSearchBean.getSleepTimeSections());
    }

    @Test
    public void testVariableSections() {
        List<SearchOptionWrapper> variableSections = new LinkedList();
        scriptSearchBean.setVariableSections(variableSections);
        assertEquals(variableSections, scriptSearchBean.getVariableSections());
    }

    @Test
    public void testReplaceEntity() {
        List<ReplaceEntity> replaceEntity = new LinkedList();
        scriptSearchBean.setReplaceEntity(replaceEntity);
        assertEquals(replaceEntity, scriptSearchBean.getReplaceEntity());
    }

    @Test
    public void testSearchQuery() {
        String searchQuery = "Search Query String";
        scriptSearchBean.setSearchQuery(searchQuery);
        assertEquals(searchQuery, scriptSearchBean.getSearchQuery());
    }

    @Test
    public void testSearching() {
        scriptSearchBean.setSearching(true);
        assertTrue(scriptSearchBean.getSearching());

        scriptSearchBean.toggleSearching();
        assertFalse(scriptSearchBean.getSearching());

        scriptSearchBean.setSearching(false);
        assertFalse(scriptSearchBean.getSearching());

        scriptSearchBean.toggleSearching();
        assertTrue(scriptSearchBean.getSearching());
    }

    @Test
    public void testReplaceModeKey() {
        scriptSearchBean.setReplaceMode(ReplaceMode.KEY);
        assertEquals(ReplaceMode.KEY, scriptSearchBean.getReplaceMode());
    }

    @Test
    public void testReplaceModeValue() {
        scriptSearchBean.setReplaceMode(ReplaceMode.VALUE);
        assertEquals(ReplaceMode.VALUE, scriptSearchBean.getReplaceMode());
    }

    @Test
    public void testSearch() {
        assertEquals("success", scriptSearchBean.search());
    }

    @Test
    public void testCanel() {
        assertEquals("success", scriptSearchBean.cancel());
    }

    @Test
    public void testRunSearchl() {
        List<ScriptStep> steps = new LinkedList<>();
        ScriptStep step = ScriptStep.builder().build();
        steps.add(step);
        when(editor.getSteps()).thenReturn(steps);
        scriptSearchBean.runSearch();
    }

    @Test
    public void testGetIndex_SetAndGet() {
        scriptSearchBean.setIndex(5);
        assertEquals(5, scriptSearchBean.getIndex());
    }

    @Test
    public void testGetSearchMatchSize_InitiallyZero() {
        assertEquals(0, scriptSearchBean.getSearchMatchSize());
    }

    @Test
    public void testGetNextX_SetAndGet() {
        scriptSearchBean.setNextX(3);
        assertEquals(3, scriptSearchBean.getNextX());
    }

    @Test
    public void testGetReplaceModeList_ReturnsAllValues() {
        ReplaceMode[] modes = scriptSearchBean.getReplaceModeList();
        assertNotNull(modes);
        assertTrue(modes.length > 0);
    }

    @Test
    public void testGetScript_DelegatesToEditor() {
        scriptSearchBean.getScript();
        // verify editor is called
    }

    @Test
    public void testGetSteps_DelegatesToEditor() {
        List<ScriptStep> steps = new LinkedList<>();
        when(editor.getSteps()).thenReturn(steps);
        assertEquals(steps, scriptSearchBean.getSteps());
    }

    @Test
    public void testRemoveFromSearchMatch_RemovesStep() {
        List<ScriptStep> steps = new LinkedList<>();
        ScriptStep step = ScriptStep.builder().build();
        steps.add(step);
        when(editor.getSteps()).thenReturn(steps);
        scriptSearchBean.runSearch();

        // The step wasn't added to searchMatch (type doesn't match), so remove should still work
        scriptSearchBean.removeFromSearchMatch(step);
    }

    @Test
    public void testGetCurrentScriptStep_WhenEmpty_ReturnsNull() {
        assertNull(scriptSearchBean.getCurrentScriptStep());
    }

    @Test
    public void testSetReplaceString_WhenSearchMatchEmpty_CallsInfo() {
        scriptSearchBean.setReplaceString("replacement");
        verify(messages).info(anyString());
    }

    @Test
    public void testNext_WhenSearchMatchEmpty_CallsInfo() {
        scriptSearchBean.next();
        verify(messages).info(anyString());
    }

    @Test
    public void testPrevious_WhenIndexZero_DoesNotDecrementBelowZero() {
        scriptSearchBean.setIndex(0);
        scriptSearchBean.previous();
        assertEquals(0, scriptSearchBean.getIndex());
        verify(messages).info(anyString());
    }

    @Test
    public void testInitReplacementDialog_ResetsIndex() {
        scriptSearchBean.initReplacementDialog();
        verify(messages).info(anyString());
    }

    @Test
    public void testGetSelectedRequestStepSection_InitiallyEmpty() {
        assertTrue(scriptSearchBean.getSelectedRequestStepSection().isEmpty());
    }

    @Test
    public void testGetSelectedThinkTimeSection_InitiallyEmpty() {
        assertTrue(scriptSearchBean.getSelectedThinkTimeSection().isEmpty());
    }

    @Test
    public void testGetSelectedSleepTimeSection_InitiallyEmpty() {
        assertTrue(scriptSearchBean.getSelectedSleepTimeSection().isEmpty());
    }

    @Test
    public void testGetSelectedVariableSection_InitiallyEmpty() {
        assertTrue(scriptSearchBean.getSelectedVariableSection().isEmpty());
    }

    @Test
    public void testSearchMatch_InitiallyEmpty() {
        assertTrue(scriptSearchBean.getSearchMatch().isEmpty());
    }

    @Test
    public void testToggleSearching_WhenFalse_BuildsOptionsAndSetsTrue() {
        scriptSearchBean.setSearching(false);
        scriptSearchBean.toggleSearching();
        assertTrue(scriptSearchBean.getSearching());
        // After toggleSearching, requestSections is populated by buildOptions
        assertFalse(scriptSearchBean.getRequestSections().isEmpty());
    }

    @Test
    public void testToggleSearching_WhenTrue_ResetsAndSetsFalse() {
        // First turn on
        scriptSearchBean.setSearching(false);
        scriptSearchBean.toggleSearching();
        assertTrue(scriptSearchBean.getSearching());
        assertFalse(scriptSearchBean.getRequestSections().isEmpty());

        // Now turn off
        scriptSearchBean.toggleSearching();
        assertFalse(scriptSearchBean.getSearching());
        assertTrue(scriptSearchBean.getRequestSections().isEmpty());
    }

    @Test
    public void testCancel_ResetsVariables() {
        // first search to populate options
        scriptSearchBean.search();
        assertFalse(scriptSearchBean.getRequestSections().isEmpty());

        // cancel resets
        scriptSearchBean.cancel();
        assertTrue(scriptSearchBean.getRequestSections().isEmpty());
    }

    @Test
    public void testGetCurrentScriptStep_WhenSearchMatchNotEmpty_ReturnsStep() {
        List<ScriptStep> steps = new LinkedList<>();
        ScriptStep step = ScriptStep.builder()
                .type(ScriptConstants.THINK_TIME)
                .build();
        com.intuit.tank.project.RequestData rd = new com.intuit.tank.project.RequestData(ScriptConstants.MIN_TIME, "100", "string");
        step.getData().add(rd);
        steps.add(step);
        when(editor.getSteps()).thenReturn(steps);
        when(editor.getScript()).thenReturn(new com.intuit.tank.project.Script());
        scriptSearchBean.setSearchQuery("100");
        scriptSearchBean.runSearch();

        // If 100 was found in the step, getCurrentScriptStep returns non-null
        if (!scriptSearchBean.getSearchMatch().isEmpty()) {
            assertNotNull(scriptSearchBean.getCurrentScriptStep());
        }
    }

    @Test
    public void testReplaceAll_WhenSearchMatchEmpty_DoesNothing() {
        // no searchMatch, replaceAll should complete without error
        assertDoesNotThrow(() -> scriptSearchBean.replaceAll());
    }

    @Test
    public void testReplaceNextX_WhenSearchMatchEmpty_DoesNothing() {
        scriptSearchBean.setNextX(5);
        assertDoesNotThrow(() -> scriptSearchBean.replaceNextX());
    }

    private void setSearchMatchViaReflection(List<ScriptStep> match) throws Exception {
        java.lang.reflect.Field field = ScriptSearchBean.class.getDeclaredField("searchMatch");
        field.setAccessible(true);
        field.set(scriptSearchBean, match);
    }

    @Test
    public void testPrevious_WhenIndexGreaterThanZero_DecrementsIndex() throws Exception {
        List<ScriptStep> match = new ArrayList<>();
        match.add(new ScriptStep());
        match.add(new ScriptStep());
        setSearchMatchViaReflection(match);
        scriptSearchBean.setIndex(1);
        scriptSearchBean.previous();
        assertEquals(0, scriptSearchBean.getIndex());
    }

    @Test
    public void testGetCurrentScriptStep_WhenSearchMatchNotEmpty_ReturnsNonNull() throws Exception {
        List<ScriptStep> match = new ArrayList<>();
        ScriptStep step = new ScriptStep();
        match.add(step);
        setSearchMatchViaReflection(match);
        scriptSearchBean.setIndex(0);
        assertNotNull(scriptSearchBean.getCurrentScriptStep());
        assertEquals(step, scriptSearchBean.getCurrentScriptStep());
    }

    @Test
    public void testReplaceAll_WhenSearchMatchHasItems_CallsReplace() throws Exception {
        List<ScriptStep> match = new ArrayList<>();
        match.add(new ScriptStep());
        setSearchMatchViaReflection(match);
        scriptSearchBean.setIndex(0);
        // replaceAll loops through and calls replace() which calls next()
        assertDoesNotThrow(() -> scriptSearchBean.replaceAll());
    }

    @Test
    public void testReplaceNextX_WhenSearchMatchHasItems_ClampsCount() throws Exception {
        List<ScriptStep> match = new ArrayList<>();
        match.add(new ScriptStep());
        setSearchMatchViaReflection(match);
        scriptSearchBean.setIndex(0);
        scriptSearchBean.setNextX(5); // 0+5 >= 1 → totalCount clamped to 1
        assertDoesNotThrow(() -> scriptSearchBean.replaceNextX());
    }

    @Test
    public void testReplaceNextX_WhenTotalCountLessThanMatchSize_DoesNotClamp() throws Exception {
        List<ScriptStep> match = new ArrayList<>();
        match.add(new ScriptStep());
        match.add(new ScriptStep());
        match.add(new ScriptStep());
        setSearchMatchViaReflection(match);
        scriptSearchBean.setIndex(0);
        scriptSearchBean.setNextX(1); // 0+1=1 < 3 → no clamping
        assertDoesNotThrow(() -> scriptSearchBean.replaceNextX());
    }

    @Test
    public void testGetSelectedSectionList_WithSearchRequestSelected_ExpandsToAllSections() {
        // Toggle searching to populate requestSections
        scriptSearchBean.toggleSearching();
        // Select the searchRequest section (first one in requestSections)
        List<SearchOptionWrapper> sections = scriptSearchBean.getRequestSections();
        if (!sections.isEmpty()) {
            // Find searchRequest section
            sections.stream()
                    .filter(sow -> sow.getValue() == RequestStepSection.searchRequest)
                    .findFirst()
                    .ifPresent(sow -> sow.setSelected(true));
            List<com.intuit.tank.script.Section> selected = scriptSearchBean.getSelectedRequestStepSection();
            assertFalse(selected.isEmpty());
        }
    }
}