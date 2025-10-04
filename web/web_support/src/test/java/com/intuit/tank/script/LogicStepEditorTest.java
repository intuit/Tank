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

import com.intuit.tank.project.Script;
import com.intuit.tank.util.Messages;
import jakarta.enterprise.context.ConversationScoped;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.intuit.tank.project.ScriptStep;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class LogicStepEditorTest {

    @InjectMocks
    private LogicStepEditor logicStepEditor;

    @Mock
    private ScriptEditor scriptEditor;

    @Mock
    private Messages messages;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() { closeable = MockitoAnnotations.openMocks(this); }

    @AfterEach
    void closeService() throws Exception { closeable.close(); }

    @Test
    public void testTestScript() {
        assertFalse(logicStepEditor.editMode);
        when(scriptEditor.getScript()).thenReturn(Script.builder().name("TestScript").build());
        logicStepEditor.editLogicStep(ScriptStep.builder().name("ScriptStep").build());
        assertEquals("ScriptStep", logicStepEditor.getName());
        assertTrue(logicStepEditor.editMode);
        String thread = (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 25) ? "3" : "1";

        logicStepEditor.testScript();
        String expected = "------------- Variables -------------\n" +
                "mode = test\n" +
                "THREAD_ID = " + thread + "\n" +
                "------------- script -------------\n" +
                "Starting scriptEngine...\n" +
                "Finished scriptEngine...\n" +
                "------------- Variables -------------\n" +
                "mode = test\n" +
                "THREAD_ID = " + thread + "\n";
        assertEquals(expected, logicStepEditor.getOutput());
    }

    @Test
    public void testAddToScript() {
        logicStepEditor.addToScript(); // validate()
        when(scriptEditor.getScript()).thenReturn(Script.builder().name("TestScript").build());
        logicStepEditor.editLogicStep(ScriptStep.builder().name("ScriptStep").build());
        logicStepEditor.setValuesFromPrevious();
        logicStepEditor.addToScript(); // insert()
        logicStepEditor.editLogicStep(ScriptStep.builder().name("ScriptStep").build());
        logicStepEditor.editMode = false;
        logicStepEditor.addToScript();  // done()
    }

    @Test
    public void testInsertLogicStep() {
        when(scriptEditor.getScript()).thenReturn(Script.builder().name("TestScript").build());
        logicStepEditor.insertLogicStep();
        assertFalse(logicStepEditor.editMode);
        assertNotNull(logicStepEditor.getLogicTestData());
    }
}