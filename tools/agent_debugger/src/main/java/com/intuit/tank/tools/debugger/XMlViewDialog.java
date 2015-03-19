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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchEngine;

/**
 * XMlViewDialog
 * 
 * @author dangleton
 * 
 */
public class XMlViewDialog extends JDialog implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RSyntaxTextArea xmlViewTA;

    private JTextField searchField;
    private JCheckBox regexCB;
    private JCheckBox matchCaseCB;

    /**
     * @param parent
     * @param xmlViewTA
     */
    public XMlViewDialog(Frame parent) {
        super(parent);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                setVisible(false);
            }
        });

        setModal(false);
        xmlViewTA = new RSyntaxTextArea();
        xmlViewTA.setEditable(false);
        xmlViewTA.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        RTextScrollPane sp = new RTextScrollPane(xmlViewTA);
        add(sp, BorderLayout.CENTER);
        JButton button = new JButton("Close");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);

            }
        });
        add(button, BorderLayout.SOUTH);

        // Create a toolbar with searching options.
        JToolBar toolBar = new JToolBar();
        toolBar.add(new JLabel("Search: "));
        searchField = new JTextField(30);
        toolBar.add(searchField);
        JButton b = new JButton("Find Next");
        b.setActionCommand("FindNext");
        b.addActionListener(this);
        toolBar.add(b);
        b = new JButton("Find Previous");
        b.setActionCommand("FindPrev");
        b.addActionListener(this);
        toolBar.add(b);
        regexCB = new JCheckBox("Regex");
        toolBar.add(regexCB);
        matchCaseCB = new JCheckBox("Match Case");
        toolBar.add(matchCaseCB);
        add(toolBar, BorderLayout.NORTH);
    }

    public void setText(String text, boolean resetCaret) {
        xmlViewTA.setText(text);
        if (resetCaret) {
            xmlViewTA.setCaretPosition(0);
        }
    }

    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        if ("FindNext".equals(command)) {
            String text = searchField.getText();
            if (text.length() == 0) {
                return;
            }
            boolean forward = true;
            boolean matchCase = matchCaseCB.isSelected();
            boolean wholeWord = false;
            boolean regex = regexCB.isSelected();
            boolean found = SearchEngine.find(xmlViewTA, text, forward,
                    matchCase, wholeWord, regex);
            if (!found) {
                JOptionPane.showMessageDialog(this, "Text not found");
            }
        }

        else if ("FindPrev".equals(command)) {
            String text = searchField.getText();
            if (text.length() == 0) {
                return;
            }
            boolean forward = false;
            boolean matchCase = matchCaseCB.isSelected();
            boolean wholeWord = false;
            boolean regex = regexCB.isSelected();
            boolean found = SearchEngine.find(xmlViewTA, text, forward,
                    matchCase, wholeWord, regex);
            if (!found) {
                JOptionPane.showMessageDialog(this, "Text not found");
            }
        }

    }

}
