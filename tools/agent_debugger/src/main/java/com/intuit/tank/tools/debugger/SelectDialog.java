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
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import org.apache.commons.lang3.StringUtils;

/**
 * ScriptSelectDialog
 * 
 * @author dangleton
 * 
 */
public class SelectDialog<SELECTION_TYPE extends Object> extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField filterField;
    private JList list;
    private JButton okBT;
    private SELECTION_TYPE selectedObject;
    private SELECTION_TYPE[] selectedObjects;
    private List<SELECTION_TYPE> items;
    private long timeClicked;

    /**
     * @param arg0
     */
    public SelectDialog(Frame f, List<SELECTION_TYPE> items, String itemType) {
        this(f, items, itemType, true);
    }

    /**
     * @param arg0
     */
    public SelectDialog(Frame f, List<SELECTION_TYPE> items, String itemType, boolean singleSelection) {
        super(f, true);
        setLayout(new BorderLayout());
        this.items = items;
        filterField = new JTextField();
        filterField.addKeyListener(new KeyHandler());
        list = new JList(items.toArray());
        list.setSelectionMode(singleSelection ? ListSelectionModel.SINGLE_SELECTION
                : ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.addListSelectionListener( (ListSelectionEvent e) -> {
            okBT.setEnabled(list.getSelectedIndex() != -1);
        });
        list.addMouseListener(new MouseAdapter() {

            /**
             * @inheritDoc
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    select();
                }
            }

        });
        JPanel labelPanel = new JPanel(new GridLayout(singleSelection ? 1 : 2, 1, 0, 5));
        labelPanel.add(new JLabel("Select a " + itemType + "."));
        if (!singleSelection) {
            String key = System.getProperty("os.name").toLowerCase().indexOf("mac") != -1 ? "⌘" : "control";
            System.out.println(key);
            labelPanel.add(new JLabel("Hold down the " + key
                    + " key to select multiple " + itemType + "."));
        }
        add(labelPanel, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(list);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterField, BorderLayout.NORTH);
        centerPanel.add(sp, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        setSize(new Dimension(400, 500));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        WindowUtil.centerOnParent(this);
    }

    /**
     * @return the selectedScript
     */
    public SELECTION_TYPE getSelectedObject() {
        return selectedObject;
    }

    /**
     * @return the selectedScript
     */
    public List<SELECTION_TYPE> getSelectedObjects() {
        if (selectedObjects != null) {
            return (List<SELECTION_TYPE>) Arrays.asList(selectedObjects);
        }
        return Collections.emptyList();
    }

    public void filter(final long timeValue) {
        new Thread( () -> {
            try {
                Thread.sleep(200);
                if (timeValue == timeClicked) {
                    SwingUtilities.invokeLater( () -> {
                        list.setListData(items.stream().filter(obj -> StringUtils.isBlank(filterField.getText())
                                || StringUtils.containsIgnoreCase(obj.toString(), filterField.getText()
                                .trim())).toArray());
                        list.repaint();
                    });
                } else {
                    System.out.println("skipping...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * @inheritDoc
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            selectedObject = null;
            selectedObjects = null;
        }
        super.setVisible(b);
    }

    /**
     * @return
     */
    private Component createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 5));
        JButton cancelBT = new JButton("Cancel");
        cancelBT.addActionListener( (ActionEvent arg0) -> {
            setVisible(false);
        });
        okBT = new JButton("Ok");
        okBT.addActionListener( (ActionEvent arg0) -> {
            select();
        });

        panel.add(okBT);
        panel.add(cancelBT);
        return panel;
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private void select() {

        selectedObject = (SELECTION_TYPE) list.getSelectedValue();
        selectedObjects = (SELECTION_TYPE[]) list.getSelectedValues();
        if (selectedObject != null) {
            setVisible(false);
        }
    }

    class KeyHandler extends KeyAdapter {

        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setVisible(false);
            }
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
            timeClicked = System.currentTimeMillis();
            filter(timeClicked);
        }
    }

}
