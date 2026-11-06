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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static com.intuit.tank.tools.debugger.DialogTestSupport.find;
import static com.intuit.tank.tools.debugger.DialogTestSupport.onEdt;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class VariableDialogTest {

    private static AgentDebuggerFrame frame;

    private VariableDialog dialog;
    private JTable table;

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

    @AfterEach
    public void disposeDialog() throws Exception {
        if (dialog != null) {
            onEdt(dialog::dispose);
        }
    }

    private void open(Map<String, String> variables) throws Exception {
        onEdt(() -> {
            dialog = new VariableDialog(frame, variables);
            table = find(dialog.getContentPane(), JTable.class, t -> true);
        });
    }

    private JButton button(String text) {
        return DialogTestSupport.button(dialog.getContentPane(), JButton.class, text);
    }

    @Test
    public void testShowsVariablesSortedByName() throws Exception {
        open(Map.of("zeta", "26", "alpha", "1", "mu", "12"));
        onEdt(() -> {
            assertEquals("View Edit Project Variables", dialog.getTitle());
            assertTrue(dialog.isModal());
            assertEquals(3, table.getRowCount());
            assertEquals("alpha", table.getValueAt(0, 0));
            assertEquals("1", table.getValueAt(0, 1));
            assertEquals("mu", table.getValueAt(1, 0));
            assertEquals("zeta", table.getValueAt(2, 0));
            assertEquals("26", table.getValueAt(2, 1));
        });
    }

    @Test
    public void testSaveSendsNonBlankVariablesToTheFrame() throws Exception {
        frame.setProjectVariables(Map.of("old", "gone"));
        open(Map.of("host", "example.com", "port", "80"));
        onEdt(() -> {
            button("Add Variable").doClick();
            assertEquals(3, table.getRowCount());
            assertEquals("Key", table.getValueAt(2, 0));
            assertEquals("Value", table.getValueAt(2, 1));
            // blank names or values are dropped on save
            button("Add Variable").doClick();
            table.setValueAt(" ", 3, 1);
            table.setValueAt("8080", 1, 1);

            button("Save").doClick();
            Map<String, String> expected = new HashMap<>();
            expected.put("host", "example.com");
            expected.put("port", "8080");
            expected.put("Key", "Value");
            assertEquals(expected, frame.getProjectVariables());
            assertFalse(dialog.isVisible());
        });
    }

    @Test
    public void testSaveCommitsAnEditInProgress() throws Exception {
        frame.setProjectVariables(null);
        open(Map.of("host", "example.com"));
        onEdt(() -> {
            assertTrue(table.editCellAt(0, 1));
            ((JTextField) table.getEditorComponent()).setText("tank.example.com");
            button("Save").doClick();
            assertFalse(table.isEditing());
            assertEquals(Map.of("host", "tank.example.com"), frame.getProjectVariables());
        });
    }

    @Test
    public void testAddCommitsAnEditInProgress() throws Exception {
        open(Map.of("host", "example.com"));
        onEdt(() -> {
            assertTrue(table.editCellAt(0, 0));
            ((JTextField) table.getEditorComponent()).setText("server");
            button("Add Variable").doClick();
            assertFalse(table.isEditing());
            assertEquals("server", table.getValueAt(0, 0));
            assertEquals(2, table.getRowCount());
        });
    }

    @Test
    public void testCloseLeavesTheFrameVariablesAlone() throws Exception {
        frame.setProjectVariables(Map.of("keep", "me"));
        open(Map.of("host", "example.com"));
        onEdt(() -> {
            button("Add Variable").doClick();
            button("Close").doClick();
            assertFalse(dialog.isVisible());
            assertEquals(Map.of("keep", "me"), frame.getProjectVariables());
        });
    }

    @Test
    public void testDeleteIsOnlyEnabledWithASelection() throws Exception {
        open(Map.of("a", "1", "b", "2"));
        onEdt(() -> {
            JButton delete = button("Delete Variable");
            assertFalse(delete.isEnabled());
            table.setRowSelectionInterval(1, 1);
            assertTrue(delete.isEnabled());
            table.clearSelection();
            assertFalse(delete.isEnabled());
        });
    }

    @Test
    public void testDeleteMovesTheSelectionToANeighbour() throws Exception {
        open(Map.of("a", "1", "b", "2", "c", "3"));
        onEdt(() -> {
            JButton delete = button("Delete Variable");
            // deleting a later row selects the one above it
            table.setRowSelectionInterval(2, 2);
            delete.doClick();
            assertEquals(2, table.getRowCount());
            assertEquals(1, table.getSelectedRow());
            assertEquals("b", table.getValueAt(1, 0));

            // deleting the first row selects the new first row
            table.setRowSelectionInterval(0, 0);
            delete.doClick();
            assertEquals(1, table.getRowCount());
            assertEquals("b", table.getValueAt(0, 0));
            assertEquals(0, table.getSelectedRow());

            // deleting the last remaining row clears the selection
            delete.doClick();
            assertEquals(0, table.getRowCount());
            assertEquals(-1, table.getSelectedRow());
            assertFalse(delete.isEnabled());
        });
    }

    @Test
    public void testDeleteCommitsAnEditInProgressAndSurvivesNoSelection() throws Exception {
        open(Map.of("a", "1", "b", "2"));
        onEdt(() -> {
            JButton delete = button("Delete Variable");
            table.setRowSelectionInterval(1, 1);
            assertTrue(table.editCellAt(1, 1));
            delete.doClick();
            assertFalse(table.isEditing());
            assertEquals(1, table.getRowCount());
            assertEquals("a", table.getValueAt(0, 0));

            // a stale enabled button with nothing selected fails to remove anything, quietly
            table.clearSelection();
            delete.setEnabled(true);
            delete.doClick();
            assertEquals(1, table.getRowCount());
        });
    }
}
