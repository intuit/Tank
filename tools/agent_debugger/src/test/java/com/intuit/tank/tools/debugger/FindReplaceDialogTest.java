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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.MockedStatic;

import com.intuit.tank.tools.debugger.FindReplaceDialog.DialogType;

import static com.intuit.tank.tools.debugger.DialogTestSupport.button;
import static com.intuit.tank.tools.debugger.DialogTestSupport.findAll;
import static com.intuit.tank.tools.debugger.DialogTestSupport.onEdt;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class FindReplaceDialogTest {

    private static final String SCRIPT = "alpha\nbeta\ngamma\nBETA\n";

    private static AgentDebuggerFrame frame;
    private static final List<DebugStep> stepsChanged = new ArrayList<>();

    private FindReplaceDialog dialog;

    @BeforeAll
    public static void setup() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        frame = DialogTestSupport.unshownDebuggerFrame();
        frame.addStepChangedListener(new StepListener() {
            @Override
            public void stepChanged(DebugStep step) {
                stepsChanged.add(step);
            }

            @Override
            public void stepEntered(DebugStep step) {
            }

            @Override
            public void stepExited(DebugStep step) {
            }
        });
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (frame != null) {
            onEdt(frame::dispose);
        }
    }

    @BeforeEach
    public void resetEditor() throws Exception {
        onEdt(() -> {
            RSyntaxTextArea ta = frame.getScriptEditorTA();
            ta.setText(SCRIPT);
            ta.setCaretPosition(0);
            stepsChanged.clear();
        });
    }

    @AfterEach
    public void disposeDialog() throws Exception {
        if (dialog != null) {
            onEdt(dialog::dispose);
        }
    }

    @Test
    public void testTitleAndReplaceControlsFollowTheDialogType() throws Exception {
        onEdt(() -> {
            dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
            assertEquals("Find", dialog.getTitle());
            assertTrue(dialog.isModal());
            assertEquals(1, combos().size());
            assertTrue(findAll(dialog.getContentPane(), JButton.class).stream()
                    .noneMatch(b -> "Replace".equals(b.getText())));
            dialog.dispose();

            dialog = new FindReplaceDialog(frame, DialogType.REPLACE);
            assertEquals("Replace", dialog.getTitle());
            assertEquals(2, combos().size());
            assertNotNull(button(dialog.getContentPane(), JButton.class, "Replace"));
            assertNotNull(button(dialog.getContentPane(), JButton.class, "Replace All"));
        });
    }

    @Test
    public void testFindMovesCaretToEachMatchingLineInTurn() throws Exception {
        onEdt(() -> {
            dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
            searchEditor().setText("beta");
            JButton find = button(dialog.getContentPane(), JButton.class, "Find");

            find.doClick();
            assertEquals(1, frame.getScriptEditorTA().getCaretLineNumber());
            // the search is case insensitive by default, so the next match is BETA
            find.doClick();
            assertEquals(3, frame.getScriptEditorTA().getCaretLineNumber());
            assertEquals(2, stepsChanged.size());
            // the term was remembered once, however many times it was searched
            assertEquals(1, combos().get(0).getItemCount());
            assertEquals("beta", combos().get(0).getItemAt(0));
        });
    }

    @Test
    public void testFindReportsNoMatchPastTheLastOneWithoutWrap() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
                searchEditor().setText("gamma");
                JButton find = button(dialog.getContentPane(), JButton.class, "Find");
                find.doClick();
                assertEquals(2, frame.getScriptEditorTA().getCaretLineNumber());
                optionPane.verify(() -> JOptionPane.showMessageDialog(any(Component.class), anyString()), never());

                find.doClick();
                optionPane.verify(() -> JOptionPane.showMessageDialog(frame, "Search String not found."));
                assertEquals(2, frame.getScriptEditorTA().getCaretLineNumber());
                assertEquals(1, stepsChanged.size());
            }
        });
    }

    @Test
    public void testWrapSearchStartsOverFromTheTop() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
                button(dialog.getContentPane(), JCheckBox.class, "Wrap Search").setSelected(true);
                searchEditor().setText("gamma");
                JButton find = button(dialog.getContentPane(), JButton.class, "Find");
                find.doClick();
                assertEquals(2, frame.getScriptEditorTA().getCaretLineNumber());
                frame.getScriptEditorTA().setCaretPosition(0);
                // the second search starts after the first match and has to wrap to find it again
                find.doClick();
                assertEquals(2, frame.getScriptEditorTA().getCaretLineNumber());
                assertEquals(2, stepsChanged.size());
                optionPane.verify(() -> JOptionPane.showMessageDialog(any(Component.class), anyString()), never());
            }
        });
    }

    @Test
    public void testCaseSensitiveSearchSkipsOtherCases() throws Exception {
        onEdt(() -> {
            dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
            button(dialog.getContentPane(), JCheckBox.class, "Case sensitive").setSelected(true);
            searchEditor().setText("BETA");
            button(dialog.getContentPane(), JButton.class, "Find").doClick();
            assertEquals(3, frame.getScriptEditorTA().getCaretLineNumber());
        });
    }

    @Test
    public void testRegularExpressionsAreOnlyHonouredWhenSelected() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
                JButton find = button(dialog.getContentPane(), JButton.class, "Find");
                searchEditor().setText("g.mma");
                find.doClick();
                // taken literally, the pattern matches nothing
                optionPane.verify(() -> JOptionPane.showMessageDialog(frame, "Search String not found."));
                assertEquals(0, frame.getScriptEditorTA().getCaretLineNumber());

                button(dialog.getContentPane(), JCheckBox.class, "Regular expressions").setSelected(true);
                searchEditor().setText("g.mma");
                find.doClick();
                assertEquals(2, frame.getScriptEditorTA().getCaretLineNumber());
            }
        });
    }

    @Test
    public void testInvalidRegularExpressionIsIgnored() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
                button(dialog.getContentPane(), JCheckBox.class, "Regular expressions").setSelected(true);
                searchEditor().setText("(unclosed");
                button(dialog.getContentPane(), JButton.class, "Find").doClick();
                assertEquals(0, frame.getScriptEditorTA().getCaretLineNumber());
                assertTrue(stepsChanged.isEmpty());
                optionPane.verify(() -> JOptionPane.showMessageDialog(any(Component.class), anyString()), never());
            }
        });
    }

    @Test
    public void testSearchHistoryKeepsTheLatest25DistinctTerms() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                dialog = new FindReplaceDialog(frame, DialogType.SEARCH);
                JButton find = button(dialog.getContentPane(), JButton.class, "Find");
                // a blank term is never remembered
                searchEditor().setText("  ");
                find.doClick();
                assertEquals(0, combos().get(0).getItemCount());

                for (int i = 0; i < 27; i++) {
                    searchEditor().setText("term" + i);
                    find.doClick();
                }
                JComboBox<?> search = combos().get(0);
                assertEquals(25, search.getItemCount());
                assertEquals("term26", search.getItemAt(0));
                assertEquals("term2", search.getItemAt(24));
                assertEquals("term26", searchEditor().getText());
            }
        });
    }

    @Test
    public void testReplaceButtonsRememberBothTerms() throws Exception {
        onEdt(() -> {
            dialog = new FindReplaceDialog(frame, DialogType.REPLACE);
            searchEditor().setText("beta");
            replaceEditor().setText("delta");
            button(dialog.getContentPane(), JButton.class, "Replace").doClick();
            assertEquals("beta", combos().get(0).getItemAt(0));
            assertEquals("delta", combos().get(1).getItemAt(0));

            searchEditor().setText("gamma");
            replaceEditor().setText("epsilon");
            button(dialog.getContentPane(), JButton.class, "Replace All").doClick();
            assertEquals(2, combos().get(0).getItemCount());
            assertEquals("gamma", combos().get(0).getItemAt(0));
            assertEquals("epsilon", combos().get(1).getItemAt(0));

            // repeating a replacement term does not duplicate it
            replaceEditor().setText("delta");
            button(dialog.getContentPane(), JButton.class, "Replace").doClick();
            assertEquals(2, combos().get(1).getItemCount());
            // replace is not implemented yet, so the script is untouched
            assertEquals(SCRIPT, frame.getScriptEditorTA().getText());
        });
    }

    @Test
    public void testReplaceHistoryKeepsTheLatest25DistinctTerms() throws Exception {
        onEdt(() -> {
            dialog = new FindReplaceDialog(frame, DialogType.REPLACE);
            JButton replaceAll = button(dialog.getContentPane(), JButton.class, "Replace All");
            for (int i = 0; i < 26; i++) {
                replaceEditor().setText("with" + i);
                replaceAll.doClick();
            }
            JComboBox<?> replace = combos().get(1);
            assertEquals(25, replace.getItemCount());
            assertEquals("with25", replace.getItemAt(0));
            assertEquals("with1", replace.getItemAt(24));
            // blank search terms were never added
            assertEquals(0, combos().get(0).getItemCount());
        });
    }

    @Test
    public void testEnterKeyFindsFromTheSearchFieldAndReplacesFromTheReplaceField() throws Exception {
        onEdt(() -> {
            dialog = new FindReplaceDialog(frame, DialogType.REPLACE);
            searchEditor().setText("gamma");
            pressKey(searchEditor(), KeyEvent.VK_ENTER);
            assertEquals(2, frame.getScriptEditorTA().getCaretLineNumber());

            replaceEditor().setText("zeta");
            pressKey(replaceEditor(), KeyEvent.VK_ENTER);
            assertEquals("zeta", combos().get(1).getItemAt(0));

            // enter on a button is left to the button itself
            JButton cancel = button(dialog.getContentPane(), JButton.class, "Cancel");
            pressKey(cancel, KeyEvent.VK_ENTER);
            assertEquals(1, combos().get(0).getItemCount());
            assertEquals(1, combos().get(1).getItemCount());
        });
    }

    @Test
    public void testEscapeAndCancelHideTheDialogAndClearTheFields() throws Exception {
        onEdt(() -> {
            dialog = new FindReplaceDialog(frame, DialogType.REPLACE);
            searchEditor().setText("beta");
            replaceEditor().setText("delta");
            pressKey(searchEditor(), KeyEvent.VK_ESCAPE);
            assertFalse(dialog.isVisible());
            assertEquals("", searchEditor().getText());
            assertEquals("", replaceEditor().getText());

            searchEditor().setText("beta");
            button(dialog.getContentPane(), JButton.class, "Cancel").doClick();
            assertFalse(dialog.isVisible());
            assertEquals("", searchEditor().getText());
            // other keys are ignored
            searchEditor().setText("beta");
            pressKey(searchEditor(), KeyEvent.VK_A);
            assertEquals("beta", searchEditor().getText());
        });
    }

    @SuppressWarnings("rawtypes")
    private List<JComboBox> combos() {
        return findAll(dialog.getContentPane(), JComboBox.class);
    }

    private JTextField searchEditor() {
        return (JTextField) combos().get(0).getEditor().getEditorComponent();
    }

    private JTextField replaceEditor() {
        return (JTextField) combos().get(1).getEditor().getEditorComponent();
    }

    /** Sends a key press to the dialog's own key handler on the component. */
    private static void pressKey(Component source, int keyCode) {
        KeyEvent event = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode,
                KeyEvent.CHAR_UNDEFINED);
        for (KeyListener l : source.getKeyListeners()) {
            if (l instanceof FindReplaceDialog.KeyHandler) {
                l.keyPressed(event);
            }
        }
    }
}
