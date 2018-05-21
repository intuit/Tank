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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.tools.debugger.ActionProducer.IconSize;

public class VariablesPanel extends JPanel implements StepListener, ScriptChangedListener {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private AgentDebuggerFrame parent;
    private VarsTableModel initVarsModel;
    private VarsTableModel afterVarsModel;

    public VariablesPanel(AgentDebuggerFrame parent) {
        super(new BorderLayout());
        parent.addScriptChangedListener(this);
        parent.addStepChangedListener(this);
        // this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.parent = parent;
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

        pane.setTopComponent(createVarsPanel(true));

        pane.setBottomComponent(createVarsPanel(false));
        pane.setDividerLocation(0.5D);
        pane.setResizeWeight(0.5D);
        add(pane, BorderLayout.CENTER);
    }

    private JPanel createVarsPanel(final boolean isInitialValues) {
        JPanel ret = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout());
        top.add(BorderLayout.WEST, new JLabel(isInitialValues ? "Initial Varaible Values"
                : "Completed Variable Values"));
        JButton copyBtn = new JButton(ActionProducer.getIcon("copying_and_distribution.png", IconSize.SMALL));
        copyBtn.addActionListener( (ActionEvent arg0) -> {
            displayVars(isInitialValues);
        });
        top.add(BorderLayout.EAST, copyBtn);
        ret.add(BorderLayout.NORTH, top);
        VarsTableModel model = new VarsTableModel();
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(model);
        table.setGridColor(Color.GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        table.setShowGrid(true);
        table.getTableHeader().setReorderingAllowed(false);
        panel.add(BorderLayout.NORTH, table.getTableHeader());
        JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(sp, BorderLayout.CENTER);
        ret.add(panel, BorderLayout.CENTER);
        if (isInitialValues) {
            initVarsModel = model;
        } else {
            afterVarsModel = model;
        }
        return ret;
    }

    protected void displayVars(boolean isInitialValues) {
        VarsTableModel model = isInitialValues ? initVarsModel : afterVarsModel;
        String sb = model.keys.stream().map(key -> key + " = " + model.values.get(key) + '\n').collect(Collectors.joining());
        System.out.println(sb);
        JTextArea ta = new JTextArea(sb);
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        JOptionPane.showMessageDialog(parent, sp, isInitialValues ? "Initial Varaible Values"
                : "Completed Variable Values", JOptionPane.PLAIN_MESSAGE, null);
    }

    @Override
    public void stepChanged(DebugStep step) {
        stepEntered(step);
        stepExited(step);
    }

    @Override
    public void stepEntered(DebugStep step) {
        if (step != null) {
            initVarsModel.setVariables(step.getEntryVariables());
        } else {
            initVarsModel.setVariables(null);
        }

    }

    public void stepExited(DebugStep step) {
        if (step != null) {
            afterVarsModel.setVariables(step.getExitVariables());
        } else {
            afterVarsModel.setVariables(null);
        }
    }

    @Override
    public void scriptChanged(HDTestPlan plan) {
        initVarsModel.setVariables(null);
        afterVarsModel.setVariables(null);
    }

    private static class VarsTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private Map<String, String> values = new HashMap<String, String>();
        private List<String> keys = new ArrayList<String>();

        private VarsTableModel() {
            super();
            keys = new ArrayList<String>();
        }

        private void setVariables(Map<String, String> map) {
            values.clear();
            keys.clear();
            if (map != null && !map.isEmpty()) {
                values.putAll(map);
                keys.addAll(values.keySet());
                Collections.sort(keys);
            }
            fireTableDataChanged();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Key" : "Value";
        }

        @Override
        public int getRowCount() {
            return values.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object ret = null;
            if (rowIndex >= 0 && rowIndex < getRowCount()) {
                String key = keys.get(rowIndex);
                if (columnIndex == 0) {
                    ret = key;
                } else {
                    ret = values.get(key);
                }
            }
            return ret;
        }

    }

}
