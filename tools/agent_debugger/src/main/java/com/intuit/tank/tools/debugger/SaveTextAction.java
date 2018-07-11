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
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import com.intuit.tank.tools.debugger.ActionProducer.IconSize;

public class SaveTextAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private JFileChooser saveAsChooser;
    private Component parent;

    private OutputTextWriter writer;

    public SaveTextAction(Component parent, String text, String fileName, OutputTextWriter writer) {
        super(text, ActionProducer.getIcon("save_as.png", IconSize.SMALL));
        this.parent = parent;
        this.writer = writer;
        saveAsChooser = new JFileChooser();
        saveAsChooser.setDialogTitle(text);
        // saveAsChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveAsChooser.setSelectedFile(new File(fileName));
    }

    /**
     * 
     */
    protected void showSaveDialog() {
        int response = saveAsChooser.showSaveDialog(parent);
        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = saveAsChooser.getSelectedFile();
            if (selectedFile.exists()) {
                int confirm = JOptionPane.showConfirmDialog(parent, "Overwrite file " + selectedFile.getName() + "?");
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            try (FileWriter fw = new FileWriter(selectedFile)) {
                writer.writeText(fw);
            } catch (Exception e) {
                System.err.println("Error writing file: " + e.toString());
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Error writing file: " + e.toString(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (writer.hasData()) {
            showSaveDialog();
        } else {
            JOptionPane.showMessageDialog(parent, "There is no data to write.");
        }
    }

}
