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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.tools.debugger.ActionProducer.IconSize;

public class InfoHeaderPanel extends JPanel implements StepListener, ScriptChangedListener, ActionListener {

    private static final char NEWLINE = '\n';

    private static final long serialVersionUID = 1L;

    private JLabel label;
    private JLabel description;
    private JButton errorButton;
    private DebugStep step;
    private ScriptErrorViewDialog scriptErrorViewDialog;

    public InfoHeaderPanel(AgentDebuggerFrame parent) {
        super(new BorderLayout());
        parent.addScriptChangedListener(this);
        parent.addStepChangedListener(this);
        this.scriptErrorViewDialog = new ScriptErrorViewDialog(parent);

        label = new JLabel("Step #");
        description = new JLabel(" ");
        errorButton = new JButton(ActionProducer.getIcon("error.png", IconSize.SMALL));
        errorButton.setToolTipText("Validation failures occurred in step");
        errorButton.addActionListener(this);
        errorButton.setVisible(false);

        super.add(BorderLayout.WEST, label);
        super.add(BorderLayout.EAST, errorButton);
        super.add(BorderLayout.SOUTH, description);
    }

    @Override
    public void scriptChanged(HDTestPlan plan) {
        description.setText(" ");
        label.setText("Step #");
        errorButton.setVisible(false);
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
            label.setText("Step # " + (step.getStepRun().getStepIndex() + 1));
            description.setText(step.getStepRun().getInfo());
            errorButton.setVisible(!step.getErrors().isEmpty());
        } else {
            description.setText(" ");
            label.setText("Step #");
            errorButton.setVisible(false);
        }
        this.step = step;
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
