/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * StepDialog
 * 
 * @author dangleton
 * 
 */
public class StepDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private RSyntaxTextArea ta;
    private AgentDebuggerFrame f;

    public StepDialog(AgentDebuggerFrame f, String text, String type) {
        super(f, true);
        this.f = f;
        setLayout(new BorderLayout());
        ta = new RSyntaxTextArea();
        ta.setSyntaxEditingStyle(type);
        ta.setHyperlinksEnabled(false);
        ta.setText(text);
        ta.setCaretPosition(0);
        RTextScrollPane sp = new RTextScrollPane(ta);
        sp.setIconRowHeaderEnabled(true);
        add(BorderLayout.CENTER, sp);
        add(createButtonPanel(), BorderLayout.SOUTH);
        setSize(new Dimension(800, 600));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        WindowUtil.centerOnParent(this);
    }

    public String getText() {
        return this.ta.getText();
    }

    /**
     * @return
     */
    private Component createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 5));
        JButton saveBT = new JButton("Save");
        saveBT.addActionListener( (ActionEvent arg0) -> {
            if (f.setStepfromString(ta.getText())) {
                setVisible(false);
            }
        });
        panel.add(saveBT);

        JButton cancelBT = new JButton("Close");
        cancelBT.addActionListener( (ActionEvent arg0) -> {
            setVisible(false);
        });
        panel.add(cancelBT);
        return panel;
    }

}
