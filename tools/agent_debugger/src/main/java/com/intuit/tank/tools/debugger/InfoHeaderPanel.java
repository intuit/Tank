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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.formdev.flatlaf.FlatClientProperties;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.runner.ErrorContainer;

public class InfoHeaderPanel extends JPanel implements StepListener, ScriptChangedListener, ActionListener {

    private static final char NEWLINE = '\n';

    private static final long serialVersionUID = 1L;

    private final AgentDebuggerFrame parent;
    private JLabel label;
    private StatusBadge methodBadge;
    private StatusBadge statusBadge;
    private JLabel description;
    private JButton errorButton;
    private DebugStep step;
    private ScriptErrorViewDialog scriptErrorViewDialog;

    public InfoHeaderPanel(AgentDebuggerFrame parent) {
        super(new BorderLayout(0, 2));
        this.parent = parent;
        parent.addScriptChangedListener(this);
        parent.addStepChangedListener(this);
        this.scriptErrorViewDialog = new ScriptErrorViewDialog(parent);

        label = new JLabel("No step selected");
        label.putClientProperty(FlatClientProperties.STYLE, "font: +1 bold");
        methodBadge = new StatusBadge();
        statusBadge = new StatusBadge();
        description = new JLabel(" ");
        description.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
        errorButton = new JButton(ActionProducer.getIcon("error"));
        errorButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        errorButton.setToolTipText("Show the validation failures in this step");
        errorButton.addActionListener(this);
        errorButton.setVisible(false);

        JPanel title = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 0));
        title.setOpaque(false);
        title.add(label);
        title.add(methodBadge);
        title.add(statusBadge);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(BorderLayout.WEST, title);
        top.add(BorderLayout.EAST, errorButton);

        super.add(BorderLayout.NORTH, top);
        super.add(BorderLayout.SOUTH, description);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBorder(DebuggerTheme.bottomLine(6, 4, 6, 8));
        if (label != null) {
            setText(step); // badge colors come from the theme
        }
    }

    @Override
    public void scriptChanged(HDTestPlan plan) {
        setText(null);
    }

    @Override
    public void stepChanged(DebugStep step) {
        setText(step);
    }

    @Override
    public void stepEntered(DebugStep step) {
        setText(step);

    }

    @Override
    public void stepExited(DebugStep step) {
        setText(step);
    }

    private void setText(DebugStep step) {
        if (step != null) {
            label.setText("Step " + (step.getStepRun().getStepIndex() + 1) + " of " + parent.getSteps().size());
            description.setText(step.getStepRun().getInfo());
            description.setToolTipText(step.getStepRun().getInfo());
            String method = step.getMethod();
            methodBadge.setStatus(StringUtils.isNotBlank(method) ? method : null,
                    DebuggerTheme.color("Actions.Blue", new Color(0x389FD6)));
            setStatusBadge(step);
            int errorCount = step.getErrors().size();
            errorButton.setText(errorCount + (errorCount == 1 ? " validation failure" : " validation failures"));
            errorButton.setVisible(errorCount > 0);
        } else {
            label.setText("No step selected");
            description.setText(" ");
            description.setToolTipText(null);
            methodBadge.setStatus(null, null);
            statusBadge.setStatus(null, null);
            errorButton.setVisible(false);
        }
        this.step = step;
    }

    private void setStatusBadge(DebugStep step) {
        BaseResponse response = step.getResponse();
        // the response exists before the request is sent, so wait until the step has finished
        if (response == null || step.getExitVariables() == null) {
            statusBadge.setStatus(null, null);
            return;
        }
        int code = response.getHttpCode();
        Color color;
        if (code == -1 || code >= 400) {
            color = DebuggerTheme.color("Actions.Red", new Color(0xDB5860));
        } else if (code >= 300) {
            color = DebuggerTheme.color("Actions.Yellow", new Color(0xEDA200));
        } else {
            color = DebuggerTheme.color("Actions.Green", new Color(0x59A869));
        }
        statusBadge.setStatus(code == -1 ? "No response" : Integer.toString(code), color);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        scriptErrorViewDialog.setTitle("Errors in step" + step.getStepRun().getInfo());
        scriptErrorViewDialog.setText(getText());
        scriptErrorViewDialog.setVisible(true);
    }

    private String getText() {
        StringBuilder sb = new StringBuilder();
        for (ErrorContainer ec : step.getErrors()) {
            if (sb.length() != 0) {
                sb.append(NEWLINE).append(NEWLINE);
            }
            sb.append(ec.getValidation().getPhase().getDisplay()).append(" failure in ").append(ec.getLocation())
                    .append(NEWLINE);
            sb.append("  Raw Validation: ").append(ec.getOriginalValidation().toString()).append(NEWLINE);
            sb.append("  Interpreted Validation: ").append(ec.getValidation().toString()).append(NEWLINE);
            sb.append("  Message: " + ec.getReason());
        }
        return sb.toString();
    }
}
