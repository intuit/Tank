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
import com.intuit.tank.util.Messages;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import javax.enterprise.context.ConversationScoped;
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
}