/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;

/**
 * TransactionTableModel
 * 
 * @author dangleton
 * 
 */
public class RuleTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;
    private List<ConfigInclusionExclusionRule> dataList = new ArrayList<ConfigInclusionExclusionRule>();

    private static final String[] COLUMN_NAMES = new String[] {
            "#",
            "Check",
            "Header",
            "Match",
            "Value",
    };

    public RuleTableModel() {
        this.setColumnCount(COLUMN_NAMES.length);
        this.setColumnIdentifiers(COLUMN_NAMES);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        }
        return String.class;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    public void addRule(ConfigInclusionExclusionRule r) {
        Vector<Object> rowData = new Vector<Object>(COLUMN_NAMES.length);
        rowData.add(dataList.size() + 1);
        rowData.add(r.getTransactionPart().toString());
        rowData.add(r.getHeader());
        rowData.add(r.getMatch());
        rowData.add(r.getValue());
        dataList.add(r);
        this.addRow(rowData);
    }

    public ConfigInclusionExclusionRule getRuleForIndex(int index) {
        ConfigInclusionExclusionRule ret = null;
        if (index >= 0 && index < dataList.size()) {
            ret = dataList.get(index);
        }
        return ret;
    }

    public void removeRule(ConfigInclusionExclusionRule rule, int rowIndex) {
        if (dataList.remove(rule)) {
            for (int i = getRowCount(); --i >= 0;) {
                if ((Integer) getValueAt(i, 0) - 1 == rowIndex) {
                    this.removeRow(i);
                    break;
                } else if ((Integer) getValueAt(i, 0) - 1 > rowIndex) {
                    setValueAt(i, i, 0);
                }
            }
        }
    }

    public void replaceRule(ConfigInclusionExclusionRule rule, ConfigInclusionExclusionRule updatedRule, int rowIndex) {
        setValueAt(updatedRule.getTransactionPart().toString(), rowIndex, 1);
        setValueAt(updatedRule.getHeader().toString(), rowIndex, 2);
        setValueAt(updatedRule.getMatch().toString(), rowIndex, 3);
        setValueAt(updatedRule.getValue().toString(), rowIndex, 4);

        dataList.remove(rowIndex);
        dataList.add(rowIndex, updatedRule);
    }

    public List<ConfigInclusionExclusionRule> getDataList() {
        return dataList;
    }

}
