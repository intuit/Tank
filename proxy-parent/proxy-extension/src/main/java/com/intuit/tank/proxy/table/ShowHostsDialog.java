/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.table;

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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.intuit.tank.proxy.WindowUtil;
import com.intuit.tank.proxy.settings.ui.IncludeType;
import com.intuit.tank.proxy.settings.ui.ProxyConfigDialog;

/**
 * ShowHostsDialog
 * 
 * @author dangleton
 * 
 */
public class ShowHostsDialog extends JDialog {

    private DefaultListModel model;
    private ProxyConfigDialog configDialog;

    /**
     * @param arg0
     */
    public ShowHostsDialog(Frame arg0, ProxyConfigDialog configDialog) {
        super(arg0, true);
        this.configDialog = configDialog;
        setSize(new Dimension(300, 400));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setLayout(new BorderLayout());
        model = new DefaultListModel();
        JList hostList = new JList(model);
        JScrollPane spane = new JScrollPane(hostList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        hostList.getInsets().set(5, 5, 5, 5);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
        panel.add(new JLabel("Hosts"));
        add(panel, BorderLayout.NORTH);
        add(spane, BorderLayout.CENTER);
        WindowUtil.centerOnParent(this);
        getInsets().set(5, 5, 5, 5);

        createRightClickMenu(hostList);

        pack();
    }

    public void createRightClickMenu(final JList table) {
        final JPopupMenu pm = new JPopupMenu();
        for (final IncludeType type : IncludeType.values()) {
            JMenuItem item = new JMenuItem("Add Host to " + type.getDisplay());
            item.addActionListener( (ActionEvent arg0) -> {
                String value = (String) table.getSelectedValue();
                if (value != null) {
                    configDialog.addHostRule(value, type);
                }
            });
            pm.add(item);
        }
        pm.addSeparator();
        for (final IncludeType type : IncludeType.values()) {
            JMenuItem item = new JMenuItem("Add Domain to " + type.getDisplay());
            item.addActionListener( (ActionEvent arg0) -> {
                String value = (String) table.getSelectedValue();
                if (value != null) {
                    configDialog.addHostRule(value, type);
                }
            });
            pm.add(item);
        }
        table.add(pm);

        table.addMouseListener(new MouseAdapter() {
            boolean pressed = false;

            /**
             * @inheritDoc
             */
            @Override
            public void mousePressed(MouseEvent e) {
                int index = table.locationToIndex(e.getPoint());
                if (index != -1) {
                    table.setSelectedIndex(index);
                }
                if (e.isPopupTrigger() && table.getSelectedIndex() != -1) {
                    pressed = true;
                    pm.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            /**
             * @inheritDoc
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!pressed && e.isPopupTrigger() && table.getSelectedIndex() != -1) {
                    pm.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });

    }

    public void addHost(String host, IncludeType type) {
        configDialog.addHostRule(host, type);
    }

    public void setHosts(Collection<String> hosts) {
        model.clear();
        List<String> list = new ArrayList<String>(hosts);
        Collections.sort(list);
        for (String s : list) {
            model.addElement(s);
        }
    }

}
