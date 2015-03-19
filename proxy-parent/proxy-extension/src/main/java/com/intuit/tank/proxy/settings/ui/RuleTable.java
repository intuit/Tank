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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * 
 * @author dangleton
 * 
 */
public class RuleTable extends JTable {

    /**
     * 
     */
    public static final Color LIGHT_BLUE = new Color(219, 234, 255, 255);
    private static final long serialVersionUID = 1L;

    /**
     * @param arg0
     */
    public RuleTable(TableModel model) {
        super(model);
        setShowGrid(true);
        setGridColor(Color.LIGHT_GRAY);
        setRowSelectionAllowed(true);
        // setIntercellSpacing(new Dimension(10, 5));
        getColumnModel().setColumnMargin(1);
        getColumnModel().getColumn(0).setMaxWidth(100);
        getColumnModel().getColumn(0).setMinWidth(50);
        getColumnModel().getColumn(0).setPreferredWidth(50);
        getColumnModel().getColumn(0).setWidth(50);

        getColumnModel().getColumn(1).setPreferredWidth(100);
        getColumnModel().getColumn(0).setWidth(100);
        for (int i = 1; i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setMinWidth(100);
        }

    }

    public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(RuleTable.LIGHT_BLUE);
        } else if (!isCellSelected(rowIndex, vColIndex)) {
            // If not shaded, match the table's background
            c.setBackground(getBackground());
        }
        return c;
    }

    public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
    }

    class MultiLineToolTip extends JToolTip {
        public MultiLineToolTip() {
            setUI(new MultiLineToolTipUI());
        }
    }

    class MultiLineToolTipUI extends MetalToolTipUI {
        private String[] strs;

        private int maxWidth = 500;

        public void paint(Graphics g, JComponent c) {
            FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
            Dimension size = c.getSize();
            g.setColor(c.getBackground());
            g.fillRect(0, 0, size.width, size.height);
            g.setColor(c.getForeground());
            if (strs != null) {
                for (int i = 0; i < strs.length; i++) {
                    g.drawString(strs[i], 3, (metrics.getHeight()) * (i + 1));
                }
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Dimension getPreferredSize(JComponent c) {
            FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());
            String tipText = ((JToolTip) c).getTipText();
            if (tipText == null) {
                tipText = "";
            }
            BufferedReader br = new BufferedReader(new StringReader(tipText));
            String line;
            int maxWidth = 0;
            Vector v = new Vector();
            try {
                while ((line = br.readLine()) != null) {
                    int width = SwingUtilities.computeStringWidth(metrics, line);
                    maxWidth = (maxWidth < width) ? width : maxWidth;
                    v.addElement(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            int lines = v.size();
            if (lines < 1) {
                strs = null;
                lines = 1;
            } else {
                strs = new String[lines];
                int i = 0;
                for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
                    strs[i] = (String) e.nextElement();
                }
            }
            int height = metrics.getHeight() * lines;
            this.maxWidth = maxWidth;
            return new Dimension(maxWidth + 6, height + 4);
        }
    }
}
