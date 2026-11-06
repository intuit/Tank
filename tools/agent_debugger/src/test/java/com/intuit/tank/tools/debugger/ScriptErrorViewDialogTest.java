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

import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static com.intuit.tank.tools.debugger.DialogTestSupport.button;
import static com.intuit.tank.tools.debugger.DialogTestSupport.find;
import static com.intuit.tank.tools.debugger.DialogTestSupport.onEdt;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class ScriptErrorViewDialogTest {

    private static JFrame owner;

    private ScriptErrorViewDialog dialog;
    private JTextArea textArea;

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
            dialog = new ScriptErrorViewDialog(owner);
            textArea = find(dialog.getContentPane(), JTextArea.class, t -> true);
        });
    }

    @AfterEach
    public void disposeDialog() throws Exception {
        if (dialog != null) {
            onEdt(dialog::dispose);
        }
    }

    @Test
    public void testIsAModalReadOnlyView() throws Exception {
        onEdt(() -> {
            assertTrue(dialog.isModal());
            assertFalse(textArea.isEditable());
            assertEquals(400, dialog.getWidth());
            assertEquals(500, dialog.getHeight());
        });
    }

    @Test
    public void testSetTextShowsTheErrorsFromTheTop() throws Exception {
        onEdt(() -> {
            textArea.setText("old");
            textArea.setCaretPosition(3);
            dialog.setText("line one\nline two");
            assertEquals("line one\nline two", textArea.getText());
            assertEquals(0, textArea.getCaretPosition());
        });
    }

    @Test
    public void testCloseButtonAndClosingTheWindowHideIt() throws Exception {
        onEdt(() -> {
            dialog.setText("error");
            button(dialog.getContentPane(), JButton.class, "Close").doClick();
            assertFalse(dialog.isVisible());
            for (WindowListener l : dialog.getWindowListeners()) {
                l.windowClosed(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSED));
            }
            assertFalse(dialog.isVisible());
            assertEquals("error", textArea.getText());
        });
    }
}
