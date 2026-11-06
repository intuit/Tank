package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.awt.Component;
import java.awt.GraphicsEnvironment;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.MockedStatic;

import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.ThinkTimeStep;

import static com.intuit.tank.tools.debugger.DialogTestSupport.find;
import static com.intuit.tank.tools.debugger.DialogTestSupport.onEdt;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class StepDialogTest {

    private static AgentDebuggerFrame frame;

    private StepDialog dialog;

    @BeforeAll
    public static void setup() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        frame = DialogTestSupport.unshownDebuggerFrame();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (frame != null) {
            onEdt(frame::dispose);
        }
    }

    @BeforeEach
    public void loadPlan() throws Exception {
        HDScriptUseCase useCase = new HDScriptUseCase();
        useCase.getScriptSteps().add(thinkTime(0, "1", "2"));
        useCase.getScriptSteps().add(thinkTime(1, "3", "4"));
        HDScript script = new HDScript();
        script.getUseCase().add(useCase);
        HDScriptGroup group = new HDScriptGroup();
        group.getGroupSteps().add(script);
        HDTestPlan plan = new HDTestPlan();
        plan.getGroup().add(group);
        onEdt(() -> frame.setCurrentTestPlan(plan));
    }

    @AfterEach
    public void disposeDialog() throws Exception {
        if (dialog != null) {
            onEdt(dialog::dispose);
        }
    }

    private static ThinkTimeStep thinkTime(int index, String min, String max) {
        ThinkTimeStep step = new ThinkTimeStep();
        step.setStepIndex(index);
        step.setMinTime(min);
        step.setMaxTime(max);
        return step;
    }

    private JButton button(String text) {
        return DialogTestSupport.button(dialog.getContentPane(), JButton.class, text);
    }

    @Test
    public void testShowsTheStepText() throws Exception {
        onEdt(() -> {
            dialog = new StepDialog(frame, "<thinkTimeStep/>", SyntaxConstants.SYNTAX_STYLE_XML);
            RSyntaxTextArea ta = find(dialog.getContentPane(), RSyntaxTextArea.class, t -> true);
            assertTrue(dialog.isModal());
            assertEquals("<thinkTimeStep/>", dialog.getText());
            assertEquals(SyntaxConstants.SYNTAX_STYLE_XML, ta.getSyntaxEditingStyle());
            assertEquals(0, ta.getCaretPosition());
            assertFalse(ta.getHyperlinksEnabled());
        });
    }

    @Test
    public void testSaveReplacesTheStepInThePlan() throws Exception {
        String edited = JaxbUtil.marshall(thinkTime(0, "5", "6"));
        onEdt(() -> {
            assertEquals("ThinkTime(1, 2)\nThinkTime(3, 4)", frame.getScriptEditorTA().getText());
            dialog = new StepDialog(frame, edited, SyntaxConstants.SYNTAX_STYLE_XML);
            button("Save").doClick();
            assertFalse(dialog.isVisible());
            ThinkTimeStep saved = (ThinkTimeStep) frame.getSteps().get(0).getStepRun();
            assertEquals("5", saved.getMinTime());
            assertEquals("6", saved.getMaxTime());
            assertEquals("ThinkTime(5, 6)\nThinkTime(3, 4)", frame.getScriptEditorTA().getText());
        });
    }

    @Test
    public void testSaveWithInvalidXmlReportsTheErrorAndKeepsTheStep() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                dialog = new StepDialog(frame, "not xml", SyntaxConstants.SYNTAX_STYLE_XML);
                button("Save").doClick();
                optionPane.verify(() -> JOptionPane.showMessageDialog(eq(frame), anyString(), eq("Error"),
                        eq(JOptionPane.ERROR_MESSAGE)));
                assertEquals("ThinkTime(1, 2)\nThinkTime(3, 4)", frame.getScriptEditorTA().getText());
                assertEquals("not xml", dialog.getText());
            }
        });
    }

    @Test
    public void testCloseDiscardsTheEdit() throws Exception {
        String edited = JaxbUtil.marshall(thinkTime(0, "7", "8"));
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                dialog = new StepDialog(frame, edited, SyntaxConstants.SYNTAX_STYLE_XML);
                button("Close").doClick();
                assertFalse(dialog.isVisible());
                assertEquals("ThinkTime(1, 2)\nThinkTime(3, 4)", frame.getScriptEditorTA().getText());
                optionPane.verify(() -> JOptionPane.showMessageDialog(any(Component.class), any(), anyString(),
                        anyInt()), never());
            }
        });
    }
}
