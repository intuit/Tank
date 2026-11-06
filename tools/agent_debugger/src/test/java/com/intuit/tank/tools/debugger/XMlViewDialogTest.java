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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
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

import static com.intuit.tank.tools.debugger.DialogTestSupport.find;
import static com.intuit.tank.tools.debugger.DialogTestSupport.onEdt;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class XMlViewDialogTest {

    private static final String XML = "<steps>\n  <step name=\"Login\"/>\n  <step name=\"login\"/>\n</steps>";

    private static JFrame owner;

    private XMlViewDialog dialog;
    private RSyntaxTextArea textArea;
    private JTextField searchField;

    @BeforeAll
    public static void setup() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        owner = DialogTestSupport.unshownFrame();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (owner != null) {
            onEdt(owner::dispose);
        }
    }

    @BeforeEach
    public void open() throws Exception {
        onEdt(() -> {
            dialog = new XMlViewDialog(owner);
            textArea = find(dialog.getContentPane(), RSyntaxTextArea.class, t -> true);
            searchField = find(dialog.getContentPane(), JTextField.class, f -> true);
            dialog.setText(XML, true);
        });
    }

    @AfterEach
    public void disposeDialog() throws Exception {
        if (dialog != null) {
            onEdt(dialog::dispose);
        }
    }

    private JButton button(String text) {
        return DialogTestSupport.button(dialog.getContentPane(), JButton.class, text);
    }

    private JCheckBox checkBox(String text) {
        return DialogTestSupport.button(dialog.getContentPane(), JCheckBox.class, text);
    }

    @Test
    public void testIsAReadOnlyModelessXmlView() throws Exception {
        onEdt(() -> {
            assertFalse(dialog.isModal());
            assertFalse(textArea.isEditable());
            assertEquals(XML, textArea.getText());
            assertEquals(0, textArea.getCaretPosition());
        });
    }

    @Test
    public void testSetTextCanKeepTheCaretAtTheEnd() throws Exception {
        onEdt(() -> {
            dialog.setText("<a/>", false);
            assertEquals("<a/>", textArea.getText());
            dialog.setText("<b/>", true);
            assertEquals(0, textArea.getCaretPosition());
        });
    }

    @Test
    public void testFindNextAndPreviousSelectEachMatch() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                searchField.setText("login");
                button("Find Next").doClick();
                assertEquals(XML.indexOf("Login"), textArea.getSelectionStart());
                button("Find Next").doClick();
                assertEquals(XML.indexOf("login"), textArea.getSelectionStart());
                button("Find Previous").doClick();
                assertEquals(XML.indexOf("Login"), textArea.getSelectionStart());
                optionPane.verify(() -> JOptionPane.showMessageDialog(any(Component.class), anyString()), never());
            }
        });
    }

    @Test
    public void testMatchCaseOnlyFindsTheExactCase() throws Exception {
        onEdt(() -> {
            checkBox("Match Case").setSelected(true);
            searchField.setText("login");
            button("Find Next").doClick();
            assertEquals("login", textArea.getSelectedText());
            assertEquals(XML.indexOf("login"), textArea.getSelectionStart());
        });
    }

    @Test
    public void testRegexSearch() throws Exception {
        onEdt(() -> {
            checkBox("Regex").setSelected(true);
            searchField.setText("name=\"[a-z]+\"");
            button("Find Next").doClick();
            assertEquals("name=\"Login\"", textArea.getSelectedText());
        });
    }

    @Test
    public void testMissingTextIsReported() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                searchField.setText("logout");
                button("Find Next").doClick();
                optionPane.verify(() -> JOptionPane.showMessageDialog(dialog, "Text not found"));
                button("Find Previous").doClick();
                optionPane.verify(() -> JOptionPane.showMessageDialog(dialog, "Text not found"),
                        org.mockito.Mockito.times(2));
                assertNull(textArea.getSelectedText());
            }
        });
    }

    @Test
    public void testEmptySearchDoesNothing() throws Exception {
        onEdt(() -> {
            try (MockedStatic<JOptionPane> optionPane = mockStatic(JOptionPane.class)) {
                searchField.setText("");
                button("Find Next").doClick();
                button("Find Previous").doClick();
                assertNull(textArea.getSelectedText());
                assertEquals(0, textArea.getCaretPosition());
                optionPane.verify(() -> JOptionPane.showMessageDialog(any(Component.class), anyString()), never());
            }
        });
    }

    @Test
    public void testUnknownCommandsAreIgnored() throws Exception {
        onEdt(() -> {
            searchField.setText("login");
            dialog.actionPerformed(new java.awt.event.ActionEvent(dialog, 0, "Other"));
            assertNull(textArea.getSelectedText());
        });
    }

    @Test
    public void testCloseButtonAndClosingTheWindowHideIt() throws Exception {
        onEdt(() -> {
            button("Close").doClick();
            assertFalse(dialog.isVisible());
            for (WindowListener l : dialog.getWindowListeners()) {
                l.windowClosed(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSED));
            }
            assertFalse(dialog.isVisible());
            // closing the window leaves the content in place for the next time it is shown
            assertEquals(XML, textArea.getText());
        });
    }
}
