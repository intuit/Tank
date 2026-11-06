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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static com.intuit.tank.tools.debugger.DialogTestSupport.find;
import static com.intuit.tank.tools.debugger.DialogTestSupport.findAll;
import static com.intuit.tank.tools.debugger.DialogTestSupport.onEdt;
import static com.intuit.tank.tools.debugger.DialogTestSupport.waitFor;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class SelectDialogTest {

    private static final List<String> SCRIPTS = List.of("Login", "Logout", "Search", "Checkout");

    private static JFrame owner;

    private SelectDialog<String> dialog;
    private JList<?> list;

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

    @AfterEach
    public void disposeDialog() throws Exception {
        if (dialog != null) {
            onEdt(dialog::dispose);
        }
    }

    private void open(boolean singleSelection) throws Exception {
        onEdt(() -> {
            dialog = singleSelection
                    ? new SelectDialog<>(owner, SCRIPTS, "script")
                    : new SelectDialog<>(owner, SCRIPTS, "script", false);
            list = find(dialog.getContentPane(), JList.class, l -> true);
        });
    }

    private JButton button(String text) {
        return DialogTestSupport.button(dialog.getContentPane(), JButton.class, text);
    }

    private List<String> labels() {
        return findAll(dialog.getContentPane(), JLabel.class).stream().map(JLabel::getText)
                .collect(Collectors.toList());
    }

    @Test
    public void testSingleSelectionDialogListsEveryItem() throws Exception {
        open(true);
        onEdt(() -> {
            assertTrue(dialog.isModal());
            assertEquals(ListSelectionModel.SINGLE_SELECTION, list.getSelectionMode());
            assertEquals(List.of("Select a script."), labels());
            assertEquals(SCRIPTS, items(list.getModel()));
            assertNull(dialog.getSelectedObject());
            assertTrue(dialog.getSelectedObjects().isEmpty());
        });
    }

    @Test
    public void testMultipleSelectionDialogExplainsHowToSelectSeveral() throws Exception {
        open(false);
        onEdt(() -> {
            assertEquals(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, list.getSelectionMode());
            List<String> labels = labels();
            assertEquals(2, labels.size());
            assertTrue(labels.get(1).startsWith("Hold down the "));
            assertTrue(labels.get(1).endsWith(" key to select multiple script."));
        });
    }

    @Test
    public void testOkReturnsTheSelection() throws Exception {
        open(false);
        onEdt(() -> {
            list.setSelectedIndices(new int[] { 1, 3 });
            assertTrue(button("Ok").isEnabled());
            button("Ok").doClick();
            assertEquals("Logout", dialog.getSelectedObject());
            assertEquals(List.of("Logout", "Checkout"), dialog.getSelectedObjects());
            assertFalse(dialog.isVisible());
        });
    }

    @Test
    public void testOkWithoutASelectionReturnsNothing() throws Exception {
        open(true);
        onEdt(() -> {
            list.setSelectedIndex(0);
            list.clearSelection();
            assertFalse(button("Ok").isEnabled());
            // a stale enabled button selects nothing
            button("Ok").setEnabled(true);
            button("Ok").doClick();
            assertNull(dialog.getSelectedObject());
            assertTrue(dialog.getSelectedObjects().isEmpty());
        });
    }

    @Test
    public void testDoubleClickSelects() throws Exception {
        open(true);
        onEdt(() -> {
            list.setSelectedIndex(2);
            list.dispatchEvent(click(1));
            assertNull(dialog.getSelectedObject());
            list.dispatchEvent(click(2));
            assertEquals("Search", dialog.getSelectedObject());
            assertEquals(List.of("Search"), dialog.getSelectedObjects());
        });
    }

    @Test
    public void testCancelAndEscapeLeaveNothingSelected() throws Exception {
        open(true);
        onEdt(() -> {
            button("Cancel").doClick();
            assertFalse(dialog.isVisible());
            assertNull(dialog.getSelectedObject());

            JTextField filterField = find(dialog.getContentPane(), JTextField.class, f -> true);
            KeyListener handler = keyHandler(filterField);
            handler.keyPressed(key(filterField, KeyEvent.KEY_PRESSED, KeyEvent.VK_ESCAPE));
            assertFalse(dialog.isVisible());
            // other keys are ignored
            handler.keyPressed(key(filterField, KeyEvent.KEY_PRESSED, KeyEvent.VK_A));
            assertNull(dialog.getSelectedObject());
        });
    }

    @Test
    public void testTypingFiltersTheListIgnoringCase() throws Exception {
        open(true);
        JTextField filterField = onEdt(() -> find(dialog.getContentPane(), JTextField.class, f -> true));
        onEdt(() -> {
            filterField.setText(" LOG ");
            keyHandler(filterField).keyTyped(key(filterField, KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED));
        });
        waitFor(() -> modelSize() == 2, 5000, "the list to be filtered");
        assertEquals(List.of("Login", "Logout"), onEdt(() -> items(list.getModel())));

        // clearing the filter shows everything again
        onEdt(() -> {
            filterField.setText("");
            keyHandler(filterField).keyTyped(key(filterField, KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED));
        });
        waitFor(() -> modelSize() == SCRIPTS.size(), 5000, "the filter to be cleared");
    }

    @Test
    public void testOnlyTheLatestFilterRequestIsApplied() throws Exception {
        open(true);
        JTextField filterField = onEdt(() -> find(dialog.getContentPane(), JTextField.class, f -> true));
        onEdt(() -> {
            filterField.setText("Search");
            // a request for an earlier keystroke is skipped
            dialog.filter(-1);
            keyHandler(filterField).keyTyped(key(filterField, KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED));
        });
        waitFor(() -> modelSize() == 1, 5000, "the list to be filtered");
        assertEquals(List.of("Search"), onEdt(() -> items(list.getModel())));
    }

    private int modelSize() {
        try {
            return onEdt(() -> list.getModel().getSize());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Object> items(ListModel<?> model) {
        return java.util.stream.IntStream.range(0, model.getSize()).mapToObj(model::getElementAt)
                .collect(Collectors.toList());
    }

    private MouseEvent click(int count) {
        return new MouseEvent(list, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 5, 5, count, false,
                MouseEvent.BUTTON1);
    }

    private static KeyEvent key(JTextField source, int id, int keyCode) {
        return new KeyEvent(source, id, System.currentTimeMillis(), 0, keyCode,
                id == KeyEvent.KEY_TYPED ? 'x' : KeyEvent.CHAR_UNDEFINED);
    }

    private static KeyListener keyHandler(JTextField filterField) {
        for (KeyListener l : filterField.getKeyListeners()) {
            if (l instanceof SelectDialog.KeyHandler) {
                return l;
            }
        }
        throw new AssertionError("No key handler on the filter field");
    }
}
