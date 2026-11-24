package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2026 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.formdev.flatlaf.FlatClientProperties;

/**
 * A small rounded label, such as an HTTP method or status code, tinted with its color.
 */
public class StatusBadge extends JLabel {

    private static final long serialVersionUID = 1L;

    public StatusBadge() {
        setBorder(BorderFactory.createEmptyBorder(1, 7, 1, 7));
        putClientProperty(FlatClientProperties.STYLE, "font: -1 bold");
        setVisible(false);
    }

    /** Shows text in color, or hides the badge when text is null */
    public void setStatus(String text, Color color) {
        setText(text);
        // a plain Color, since updateUI() replaces a UIResource color with the default label foreground
        setForeground(color != null ? new Color(color.getRGB(), true) : null);
        setVisible(text != null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = getForeground();
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
