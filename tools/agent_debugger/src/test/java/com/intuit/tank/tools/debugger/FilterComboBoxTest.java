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

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

/**
 * The combo box is never displayed; showPopup and hidePopup are overridden to record the calls instead.
 */
public class FilterComboBoxTest {

    @Test
    public void testStartsWithAllItemsAndEditable() {
        RecordingComboBox combo = new RecordingComboBox(FilterComboBox.populateArray());

        assertTrue(combo.isEditable());
        assertEquals(8, combo.getItemCount());
    }

    @Test
    public void testFilterKeepsMatchingItemsIgnoringCase() {
        RecordingComboBox combo = new RecordingComboBox(FilterComboBox.populateArray());

        combo.comboFilter("MOUNT");

        assertEquals(2, combo.getItemCount());
        assertEquals("Mountain Flight", combo.getItemAt(0));
        assertEquals("Mount Climbing", combo.getItemAt(1));
        assertEquals("MOUNT", combo.getSelectedItem());
        assertTrue(combo.popupShown);
        assertFalse(combo.popupHidden);
    }

    @Test
    public void testFilterWithNoMatchHidesPopupAndKeepsItems() {
        RecordingComboBox combo = new RecordingComboBox(FilterComboBox.populateArray());

        combo.comboFilter("zzz");

        assertEquals(8, combo.getItemCount());
        assertTrue(combo.popupHidden);
        assertFalse(combo.popupShown);
    }

    @Test
    public void testKeyReleaseFiltersOnTypedText() throws Exception {
        RecordingComboBox combo = new RecordingComboBox(List.of("Rafting", "Trekking", "Para Gliding"));
        JTextField editor = (JTextField) combo.getEditor().getEditorComponent();
        editor.setText("trek");
        KeyEvent released = new KeyEvent(editor, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,
                KeyEvent.VK_K, 'k');

        for (KeyListener listener : editor.getKeyListeners()) {
            listener.keyReleased(released);
        }
        // the filter is queued on the event dispatch thread; wait for it to run
        SwingUtilities.invokeAndWait(() -> { });

        assertEquals(1, combo.getItemCount());
        assertEquals("Trekking", combo.getItemAt(0));
        assertTrue(combo.popupShown);
    }

    private static class RecordingComboBox extends FilterComboBox {
        private static final long serialVersionUID = 1L;
        boolean popupShown;
        boolean popupHidden;

        RecordingComboBox(List<String> items) {
            super(items);
        }

        @Override
        public void showPopup() {
            popupShown = true;
        }

        @Override
        public void hidePopup() {
            popupHidden = true;
        }
    }
}
