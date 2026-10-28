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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;

/**
 * A slim title bar for a pane, with optional icon-only buttons on the right.
 */
public class SectionHeader extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 2, 0));

    public SectionHeader(String title) {
        super(new BorderLayout());
        JLabel label = new JLabel(title);
        label.putClientProperty(FlatClientProperties.STYLE, "font: bold");
        buttons.setOpaque(false);
        add(label, BorderLayout.WEST);
        add(buttons, BorderLayout.EAST);
    }

    public JButton addButton(Action action) {
        JButton button = new JButton(action);
        button.setHideActionText(true);
        return addButton(button);
    }

    public JButton addButton(JButton button) {
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        button.setFocusable(false);
        buttons.add(button);
        return button;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBorder(DebuggerTheme.bottomLine(3, 8, 3, 4));
    }
}
