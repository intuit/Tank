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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * XMlViewDialog
 * 
 * @author dangleton
 * 
 */
public class ScriptErrorViewDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private boolean centered;

    /**
     * @param parent
     * @param xmlViewTA
     */
    public ScriptErrorViewDialog(Frame parent) {
        super(parent);
        super.setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                setVisible(false);
            }
        });
        setModal(true);
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane sp = new JScrollPane(textArea);
        sp.setAutoscrolls(true);
        add(sp, BorderLayout.CENTER);
        JPanel jPanel = new JPanel(new FlowLayout());
        JButton button = new JButton("Close");
        button.addActionListener((ActionEvent e) -> {
            setVisible(false);
        });
        jPanel.add(button);
        add(jPanel, BorderLayout.SOUTH);
        setSize(new Dimension(400, 500));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
    }

    @Override
    public void setVisible(boolean b) {
        if (!centered && b) {
            centered = true;
            WindowUtil.centerOnParent(this);
        }
        super.setVisible(b);
    }

    public void setText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
    }

}
