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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;

public class GeneralInclusionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Handler handler = null;
    private RuleTableModel ruleTableModel;
    private RuleTable ruleTable;

    private ProxyConfigDialog dialog;

    public GeneralInclusionPanel(Handler handler, ProxyConfigDialog dialog) {
        this.handler = handler;
        this.dialog = dialog;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        ruleTableModel = new RuleTableModel();

        for (ConfigInclusionExclusionRule rule : handler.getData()) {
            ruleTableModel.addRule(rule);
        }

        ruleTable = new RuleTable(ruleTableModel);
        ruleTable.setShowGrid(true);
        ruleTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(ruleTable);
        ruleTable.setFillsViewportHeight(true);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel opsPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener((ActionEvent e) -> {
            handler.addRule(ruleTableModel, dialog);
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener((ActionEvent e) -> {
            int selectedRow = ruleTable.getSelectedRow();
            if (selectedRow != -1) {
                handler.removeRule(ruleTableModel, ruleTableModel.getRuleForIndex(selectedRow),
                        ruleTable.getSelectedRow());
            }
        });
        JButton editButton = new JButton("Edit");
        editButton.addActionListener( (ActionEvent e) -> {
            int selectedRow = ruleTable.getSelectedRow();
            if (selectedRow != -1) {
                handler.editRule(ruleTableModel, ruleTableModel.getRuleForIndex(selectedRow),
                        ruleTable.getSelectedRow(), dialog);
            }
        });

        opsPanel.add(addButton);
        opsPanel.add(deleteButton);
        opsPanel.add(editButton);

        this.add(opsPanel, BorderLayout.SOUTH);
    }

    public Set<ConfigInclusionExclusionRule> getData() {
        List<ConfigInclusionExclusionRule> dataList = ruleTableModel.getDataList();
        return new HashSet<ConfigInclusionExclusionRule>(dataList);
    }

    public void addRule(ConfigInclusionExclusionRule rule) {
        ruleTableModel.addRule(rule);
    }

    public void update(Set<ConfigInclusionExclusionRule> rules) {
        ruleTableModel.getDataVector().clear();
        ruleTableModel.getDataList().clear();
        for (ConfigInclusionExclusionRule configInclusionExclusionRule : rules) {
            ruleTableModel.addRule(configInclusionExclusionRule);
        }
        validate();
    }
}
