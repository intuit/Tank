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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

/**
 * The file chooser and JOptionPane are mocked, so these tests never open a dialog and run headless.
 */
public class SaveTextActionTest {

    @TempDir
    Path tempDir;

    @Test
    public void testConfiguresChooserFromConstructor() {
        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class)) {
            SaveTextAction action = new SaveTextAction(null, "Save Log As...", "debuggerLog.txt", writer("x"));

            assertEquals("Save Log As...", action.getValue(Action.NAME));
            JFileChooser chooser = choosers.constructed().get(0);
            verify(chooser).setDialogTitle("Save Log As...");
            verify(chooser).setSelectedFile(new File("debuggerLog.txt"));
        }
    }

    @Test
    public void testNoDataShowsMessage() {
        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class);
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            new SaveTextAction(null, "Save", "out.txt", writer("")).actionPerformed(null);

            pane.verify(() -> JOptionPane.showMessageDialog(null, "There is no data to write."));
            verify(choosers.constructed().get(0), never()).showSaveDialog(any());
        }
    }

    @Test
    public void testWritesToNewFile() throws IOException {
        File target = tempDir.resolve("out.txt").toFile();
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.APPROVE_OPTION, target)) {
            new SaveTextAction(null, "Save", "out.txt", writer("hello")).actionPerformed(null);
        }

        assertEquals("hello", Files.readString(target.toPath()));
    }

    @Test
    public void testCancelWritesNothing() {
        File target = tempDir.resolve("out.txt").toFile();
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.CANCEL_OPTION, target)) {
            new SaveTextAction(null, "Save", "out.txt", writer("hello")).actionPerformed(null);
        }

        assertFalse(target.exists());
    }

    @Test
    public void testExistingFileKeptWhenOverwriteDeclined() throws IOException {
        Path target = Files.writeString(tempDir.resolve("out.txt"), "original");
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.APPROVE_OPTION, target.toFile());
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            pane.when(() -> JOptionPane.showConfirmDialog(any(), eq("Overwrite file out.txt?")))
                    .thenReturn(JOptionPane.NO_OPTION);

            new SaveTextAction(null, "Save", "out.txt", writer("replacement")).actionPerformed(null);
        }

        assertEquals("original", Files.readString(target));
    }

    @Test
    public void testExistingFileOverwrittenWhenConfirmed() throws IOException {
        Path target = Files.writeString(tempDir.resolve("out.txt"), "original");
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.APPROVE_OPTION, target.toFile());
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            pane.when(() -> JOptionPane.showConfirmDialog(any(), anyString())).thenReturn(JOptionPane.YES_OPTION);

            new SaveTextAction(null, "Save", "out.txt", writer("replacement")).actionPerformed(null);
        }

        assertEquals("replacement", Files.readString(target));
    }

    @Test
    public void testWriteFailureShowsError() {
        File target = tempDir.resolve("out.txt").toFile();
        OutputTextWriter failing = new OutputTextWriter() {
            @Override
            public void writeText(Writer w) {
                throw new IllegalStateException("disk full");
            }

            @Override
            public boolean hasData() {
                return true;
            }
        };
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.APPROVE_OPTION, target);
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            new SaveTextAction(null, "Save", "out.txt", failing).actionPerformed(null);

            pane.verify(() -> JOptionPane.showMessageDialog(isNull(),
                    eq("Error writing file: java.lang.IllegalStateException: disk full"), eq("Error"),
                    eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    private static MockedConstruction<JFileChooser> mockChooser(int response, File selected) {
        return mockConstruction(JFileChooser.class, (mock, context) -> {
            when(mock.showSaveDialog(any())).thenReturn(response);
            when(mock.getSelectedFile()).thenReturn(selected);
        });
    }

    private static OutputTextWriter writer(String text) {
        return new OutputTextWriter() {
            @Override
            public void writeText(Writer w) {
                try {
                    w.write(text);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public boolean hasData() {
                return !text.isEmpty();
            }
        };
    }
}
