package com.intuit.tank.proxy.settings.ui;

/*
 * #%L
 * proxy-extension
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intuit.tank.proxy.config.CommonHeaders;
import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;
import com.intuit.tank.proxy.config.MatchType;
import com.intuit.tank.proxy.config.TransactionPart;

public class RuleEditorDialog extends JDialog {

    private ConfigInclusionExclusionRule rule;

    private Component ruleLabel;
    private JLabel matchTypeLabel;
    private JLabel headerLabel;
    private JLabel transactionLabel;
    private JTextField ruleField;
    private JComboBox matchTypeField;
    private JComboBox headerField;
    private JComboBox transactionField;
    private JPanel opsPanel;
    private JPanel fillPanel;

    private boolean cancelFlag = false;

    /**
     * @return the cancelFlag
     */
    public boolean isCancelFlag() {
        return cancelFlag;
    }

    public RuleEditorDialog(JDialog parent) {
        this(parent, new ConfigInclusionExclusionRule(TransactionPart.both, "", MatchType.equals, "rule"));
    }

    public RuleEditorDialog(JDialog parent, ConfigInclusionExclusionRule rule) {
        super(parent, "Rule Editor", true);
        this.rule = rule;
        initialize();
    }

    private void initialize() {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(400, 300));
        this.getContentPane().setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(400, 300));
        this.setMinimumSize(new Dimension(400, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        this.getContentPane().add(getTransactionLabel(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        this.getContentPane().add(getTransactionField(), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getHeaderLabel(), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getHeaderField(), gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getMatchTypeLabel(), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getMatchTypeField(), gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getRuleLabel(), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getRuleField(), gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getOpsPanel(), gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        this.getContentPane().add(getFillPanel(), gbc);

    }

    private JPanel getFillPanel() {
        if (fillPanel == null) {
            fillPanel = new JPanel();
        }
        return fillPanel;
    }

    private void setRule(ConfigInclusionExclusionRule rule) {
        this.rule = rule;
    }

    public ConfigInclusionExclusionRule getRule() {
        return rule;
    }

    private void processDoneAndClose() {
        TransactionPart part = (TransactionPart) getTransactionField().getSelectedItem();
        String header = getHeaderField().getSelectedItem().toString();
        MatchType match = (MatchType) getMatchTypeField().getSelectedItem();
        String value = getRuleField().getText();
        ConfigInclusionExclusionRule rule = new ConfigInclusionExclusionRule(part, header, match, value);
        setRule(rule);
        setVisible(false);
    }

    private JPanel getOpsPanel() {
        // TODO Auto-generated method stub
        if (opsPanel == null) {
            opsPanel = new JPanel();

            JButton doneButton = new JButton("Done");
            doneButton.addActionListener( (ActionEvent e) -> {
                System.out.println("RuleEditorDialog.getOpsPanel().new ActionListener() {...}.actionPerformed()");
                cancelFlag = false;
                processDoneAndClose();
            });
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener((ActionEvent e) -> {
                // TODO Auto-generated method stub
                System.out.println("RuleEditorDialog.getOpsPanel().new ActionListener() {...}.actionPerformed()");
                cancelFlag = true;
                setVisible(false);
            });
            opsPanel.add(doneButton);
            opsPanel.add(cancelButton);
        }
        return opsPanel;
    }

    private JTextField getRuleField() {
        if (ruleField == null) {
            ruleField = new JTextField();
            ruleField.setPreferredSize(new Dimension(200, 30));
            ruleField.setText(rule.getValue());
        }
        return ruleField;
    }

    private Component getRuleLabel() {
        if (ruleLabel == null) {
            ruleLabel = new JLabel("Rule :");
        }
        return ruleLabel;
    }

    private JComboBox getMatchTypeField() {
        if (matchTypeField == null) {
            matchTypeField = new JComboBox(MatchType.values());
            matchTypeField.setPreferredSize(new Dimension(200, 30));
            matchTypeField.setSelectedItem(rule.getMatch());
        }
        return matchTypeField;
    }

    private JLabel getMatchTypeLabel() {
        if (matchTypeLabel == null) {
            matchTypeLabel = new JLabel("Match :");
        }
        return matchTypeLabel;
    }

    private JComboBox getHeaderField() {
        if (headerField == null) {
            headerField = new JComboBox(CommonHeaders.values());
            headerField.setEditable(true);
            headerField.setPreferredSize(new Dimension(200, 30));
            headerField.setSelectedItem(rule.getHeader());
        }
        return headerField;
    }

    private JLabel getHeaderLabel() {
        if (headerLabel == null) {
            headerLabel = new JLabel("Header :");
        }
        return headerLabel;
    }

    private JComboBox getTransactionField() {
        if (transactionField == null) {
            transactionField = new JComboBox(TransactionPart.values());
            transactionField.setPreferredSize(new Dimension(200, 30));
            transactionField.setSelectedItem(rule.getTransactionPart());
        }
        return transactionField;
    }

    private JLabel getTransactionLabel() {
        if (transactionLabel == null) {
            transactionLabel = new JLabel("Transaction :");
        }
        return transactionLabel;
    }

    public static void main(String[] args) {
        RuleEditorDialog pcd = new RuleEditorDialog(null);
        pcd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pcd.setVisible(true);
    }

}
