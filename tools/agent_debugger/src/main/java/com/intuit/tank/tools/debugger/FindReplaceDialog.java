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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class FindReplaceDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;

    public enum DialogType {
        SEARCH, // Defines a search only dialog
        REPLACE // Defines a search and replace dialog
    }

    // Private declarations
    private AgentDebuggerFrame parent;
    private JComboBox cbSearch;
    private JComboBox cbReplace;
    private JTextField tfSearchEditor;
    private JTextField tfReplaceEditor;
    private JButton btnFind;
    private JButton btnReplace;
    private JButton btnReplaceAll;
    private JButton btnCancel;
    private JCheckBox checkboxMatchCase;
    private JCheckBox checkboxRegexp;
    private JCheckBox checkboxWrap;
    private int currentLine;
    private int initialLine;
    private int initialRunningStep;

    /**
     * Constructs a new find dialog according to the specified type of dialog requested. The dialog can be either a FIND
     * dialog, either a REPLACE dialog. In both cases, components displayed remain the sames, but the ones specific to
     * replace feature are grayed out.
     * 
     * @param parent
     *            The window holder
     * @param type
     *            The type of the dialog: FindReplace.FIND or FindReplace.REPLACE
     * @param modal
     *            Displays dialog as a modal window if true
     */

    public FindReplaceDialog(AgentDebuggerFrame parent, DialogType type) {
        super(parent, type == DialogType.REPLACE ? "Replace" : "Find", true);
        this.parent = parent;

        cbSearch = new JComboBox();
        cbSearch.setEditable(true);
        cbReplace = new JComboBox();
        cbReplace.setEditable(true);
        KeyHandler handler = new KeyHandler();
        tfSearchEditor = (JTextField) cbSearch.getEditor().getEditorComponent();
        tfSearchEditor.addKeyListener(handler);
        tfReplaceEditor = (JTextField) cbReplace.getEditor().getEditorComponent();
        tfReplaceEditor.addKeyListener(handler);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        getContentPane().setLayout(gridbag);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        int gridX = 0;
        int gridY = 0;
        JLabel findLabel = new JLabel("Find");
        buildConstraints(constraints, gridX, gridY, 1, 1, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(findLabel, constraints);
        getContentPane().add(findLabel);
        gridX++;

        buildConstraints(constraints, gridX, gridY, 1, 1, 100, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(cbSearch, constraints);
        getContentPane().add(cbSearch);
        gridX++;

        btnFind = new JButton("Find");
        btnFind.setToolTipText("Find text in scripts");
        btnFind.setMnemonic('F');
        btnFind.addActionListener(this);
        buildConstraints(constraints, gridX, gridY, 1, 1, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(btnFind, constraints);
        getContentPane().add(btnFind);
        getRootPane().setDefaultButton(btnFind);
        gridX++;

        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic('C');
        btnCancel.addActionListener(this);
        buildConstraints(constraints, gridX, gridY, 1, 1, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(btnCancel, constraints);
        getContentPane().add(btnCancel);
        gridY++;
        gridX = 0;
        if (type == DialogType.REPLACE) {
            JLabel replaceLabel = new JLabel("Replace");
            buildConstraints(constraints, gridX, gridY, 1, 1, 0, 0);
            constraints.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(replaceLabel, constraints);
            getContentPane().add(replaceLabel);
            gridX++;

            buildConstraints(constraints, gridX, gridY, 1, 1, 100, 0);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.CENTER;
            gridbag.setConstraints(cbReplace, constraints);
            getContentPane().add(cbReplace);
            gridX++;

            btnReplace = new JButton("Replace");
            btnReplace.setToolTipText("REplace in script");
            btnReplace.setMnemonic('R');
            btnReplace.addActionListener(this);
            buildConstraints(constraints, gridX, gridY, 1, 1, 0, 0);
            constraints.anchor = GridBagConstraints.CENTER;
            gridbag.setConstraints(btnReplace, constraints);
            getContentPane().add(btnReplace);
            gridX++;

            btnReplaceAll = new JButton("Replace All");
            btnReplaceAll.addActionListener(this);
            buildConstraints(constraints, gridX, gridY, 1, 1, 0, 0);
            constraints.anchor = GridBagConstraints.CENTER;
            gridbag.setConstraints(btnReplaceAll, constraints);
            getContentPane().add(btnReplaceAll);
            btnReplace.addKeyListener(handler);
            btnReplaceAll.addKeyListener(handler);
            gridY++;
            gridX = 0;
        }

        TitledBorder border = new TitledBorder("Options");
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.setBorder(border);
        checkboxWrap = new JCheckBox("Wrap Search");
        panel.add(checkboxWrap);

        checkboxMatchCase = new JCheckBox("Case sensitive");
        panel.add(checkboxMatchCase);

        checkboxRegexp = new JCheckBox("Regular expressions");
        panel.add(checkboxRegexp);

        buildConstraints(constraints, gridX, gridY, 4, 1, 100, 100);
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(panel, constraints);
        getContentPane().add(panel);

        FontMetrics fm = getFontMetrics(getFont());
        cbSearch.setPreferredSize(new Dimension(18 * fm.charWidth('m'),
                (int) cbSearch.getPreferredSize().height));
        cbReplace.setPreferredSize(new Dimension(18 * fm.charWidth('m'),
                (int) cbReplace.getPreferredSize().height));

        pack();
        // setResizable(false);
        WindowUtil.centerOnParent(this);

        // patch by MJB 8/1/2002
        btnFind.addKeyListener(handler);

        btnCancel.addKeyListener(handler);
        checkboxMatchCase.addKeyListener(handler);
        checkboxRegexp.addKeyListener(handler);

    }

    @Override
    public void setVisible(boolean b) {
        tfSearchEditor.setText("");
        if (tfReplaceEditor != null) {
            tfReplaceEditor.setText("");
        }
        RSyntaxTextArea ta = parent.getScriptEditorTA();
        if (b) {
            currentLine = ta.getCaretLineNumber();
            initialRunningStep = parent.getCurrentRunningStep();
            initialLine = ta.getCaretLineNumber();
            tfSearchEditor.requestFocusInWindow();
        } else {
            // if (initialRunningStep >= 0 && initialRunningStep < ta.getLineCount() -1) {
            // ta.setActiveLineRange(initialRunningStep + 1, initialRunningStep + 1);
            // } else {
            // ta.setActiveLineRange(-1, -1);
            // }
            // parent.fireStepChanged(initialRunningStep);
            // ta.setCurrentLine(initialLine);
            // parent.repaint();
        }
        super.setVisible(b);
    }

    // Catch the action performed and then look for its source
    // According to the source object we call appropriate methods

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnCancel) {
            setVisible(false);
        } else if (source == btnFind) {
            doFind();
        } else if (source == btnReplace) {
            doReplace();
        } else if (source == btnReplaceAll) {
            doReplaceAll();
        }
    }

    // replace all the occurences of search pattern by
    // the replace one. If 'All Files' is checked, this is
    // done in all the opened file in the component 'parent'

    private void doReplaceAll() {
        addReplaceHistory();
        addSearchHistory();
        // try {
        // JextTextArea textArea = parent.getTextArea();
        // setSettings();
        // if (Search.replaceAll(textArea, 0, textArea.getLength()) == 0) {
        // Utilities.beep();
        // }
        //
        // } catch (Exception e) {
        // // nothing
        // } finally {
        // Utilities.setCursorOnWait(this, false);
        // }
    }

    // replaces specified search pattern by the replace one.
    // this is done only if a match is found.

    private void doReplace() {
        addReplaceHistory();
        addSearchHistory();
        //
        // try {
        //
        // JextTextArea textArea = parent.getTextArea();
        // setSettings();
        //
        // if (!Search.replace(textArea))
        // {
        // Utilities.beep();
        // } else
        // find(textArea);
        //
        // } catch (Exception e) {
        // // nothing
        // } finally {
        // Utilities.setCursorOnWait(this, false);
        // }
    }

    // finds the next occurence of current search pattern
    // the search is done in current text area

    private void doFind() {
        addSearchHistory();
        find(parent.getScriptEditorTA());
    }

    // finds the next occurence of the search pattern in a
    // a given text area. if match is not found, and if user
    // don't ask to start over from beginning, then the method
    // calls itself by specifying next opened text area.

    private void find(RSyntaxTextArea textArea) {

        try {
            int offset = currentLine < textArea.getLineCount() ? textArea.getLineStartOffset(currentLine) : 0;
            String searchTerm = tfSearchEditor.getText();
            String text = textArea.getText();
            int foundIndex = -1;
            int flags = (checkboxRegexp.isSelected() ? 0 : Pattern.LITERAL)
                    | (checkboxMatchCase.isSelected() ? 0 : Pattern.CASE_INSENSITIVE);
            Pattern p = Pattern.compile(searchTerm, flags);
            Matcher matcher = p.matcher(text);
            matcher.region(offset, text.length());
            if (matcher.find()) {
                foundIndex = matcher.start();
            } else if (checkboxWrap.isSelected() && offset > 0) {
                matcher.region(0, offset);
                if (matcher.find()) {
                    foundIndex = matcher.start();
                }
            }
            if (foundIndex != -1) {
                int lineOfOffset = textArea.getLineOfOffset(foundIndex);
                // textArea.setActiveLineRange(lineOfOffset, lineOfOffset);
                textArea.setCurrentLine(lineOfOffset);
                // textArea.setCaretPosition(foundIndex + searchTerm.length());
                parent.repaint();
                parent.fireStepChanged(lineOfOffset);
                currentLine = lineOfOffset + 1;
            } else {
                JOptionPane.showMessageDialog(parent, "Search String not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adds current search pattern in the search history list
     */
    private void addSearchHistory() {
        addSearchHistory(tfSearchEditor.getText());
    }

    /**
     * adds a pattern in the search history list
     * 
     * @param c
     *            the pattern to be added
     */
    private void addSearchHistory(String c) {
        if (StringUtils.isBlank(c)) {
            return;
        }
        for (int i = 0; i < cbSearch.getItemCount(); i++) {
            if (((String) cbSearch.getItemAt(i)).equals(c)) {
                return;
            }
        }
        cbSearch.insertItemAt(c, 0);
        if (cbSearch.getItemCount() > 25) {
            for (int i = 25; i < cbSearch.getItemCount();)
                cbSearch.removeItemAt(i);
        }
        tfSearchEditor.setText((String) cbSearch.getItemAt(0));
    }

    /**
     * adds current replace pattern in the replace history list
     */
    private void addReplaceHistory() {
        addReplaceHistory(tfReplaceEditor.getText());
    }

    /**
     * adds a pattern in the replace history list
     * 
     * @param c
     *            the pattern to be added
     */
    private void addReplaceHistory(String c) {
        if (StringUtils.isBlank(c)) {
            return;
        }

        for (int i = 0; i < cbReplace.getItemCount(); i++) {
            if (((String) cbReplace.getItemAt(i)).equals(c)) {
                return;
            }
        }

        cbReplace.insertItemAt(c, 0);
        if (cbReplace.getItemCount() > 25) {
            for (int i = 25; i < cbReplace.getItemCount();)
                cbReplace.removeItemAt(i);
        }

        tfReplaceEditor.setText((String) cbReplace.getItemAt(0));
    }

    private void buildConstraints(GridBagConstraints agbc, int agx, int agy, int agw, int agh,
            int awx, int awy) {
        agbc.gridx = agx;
        agbc.gridy = agy;
        agbc.gridwidth = agw;
        agbc.gridheight = agh;
        agbc.weightx = awx;
        agbc.weighty = awy;
        agbc.insets = new Insets(2, 2, 2, 2);
    }

    class KeyHandler extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (evt.getSource() == tfSearchEditor) {
                    doFind();
                } else if (evt.getSource() == tfReplaceEditor) {
                    doReplace();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                setVisible(false);
                break;
            }
        }
    }

}
