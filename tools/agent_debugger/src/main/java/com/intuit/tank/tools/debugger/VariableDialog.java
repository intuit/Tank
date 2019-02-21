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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;

/**
 * StepDialog
 * 
 * @author dangleton
 * 
 */
public class VariableDialog extends JDialog implements ListSelectionListener {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private AgentDebuggerFrame f;
    private JButton deleteBT;

    public VariableDialog(AgentDebuggerFrame f, Map<String, String> variables) {
        super(f, true);
        this.f = f;
        setLayout(new BorderLayout());
        setTitle("View Edit Project Variables");
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Variable Name");
        model.addColumn("Variable Value");
        List<String> keys = new ArrayList<String>(variables.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Object[] data = new Object[2];
            data[0] = key;
            data[1] = variables.get(key);
            model.addRow(data);
        }
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        table.setGridColor(Color.GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        table.setShowGrid(true);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(table);
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        setSize(new Dimension(800, 600));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        WindowUtil.centerOnParent(this);
    }

    /**
     * @return
     */
    private Component createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 5));
        JButton saveBT = new JButton("Save");
        saveBT.addActionListener( (ActionEvent arg0) -> {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            Map<String, String> ret = new HashMap<String, String>();
            for (int row = 0; row < table.getModel().getRowCount(); row++) {
                String key = (String) table.getModel().getValueAt(row, 0);
                String value = (String) table.getModel().getValueAt(row, 1);
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    ret.put(key, value);
                }
            }
            f.setProjectVariables(ret);
            setVisible(false);
        });
        panel.add(saveBT);

        JButton cancelBT = new JButton("Close");
        cancelBT.addActionListener( (ActionEvent arg0) -> {
            setVisible(false);
        });
        panel.add(cancelBT);

        JButton addBt = new JButton("Add Variable");
        addBt.addActionListener( (ActionEvent arg0) -> {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            try {
                ((DefaultTableModel) table.getModel()).addRow(new Object[] { "Key", "Value" });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        panel.add(addBt);

        deleteBT = new JButton("Delete Variable");
        deleteBT.addActionListener( (ActionEvent arg0) -> {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            try {
                int selectedRow = table.getSelectedRow();
                ((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow());
                if (selectedRow > 0) {
                    table.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
                } else if (table.getRowCount() > 0) {
                    table.getSelectionModel().setSelectionInterval(0, 0);
                } else {
                    table.getSelectionModel().clearSelection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        deleteBT.setEnabled(false);
        panel.add(deleteBT);
        return panel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        deleteBT.setEnabled(table.getSelectedRow() != -1);

    }

}
