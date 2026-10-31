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
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.intuit.tank.harness.data.HDTestPlan;

public class RequestResponsePanel extends JPanel implements StepListener, ScriptChangedListener, OutputTextWriter {

    private static final long serialVersionUID = 1L;

    private RSyntaxTextArea requestTA;
    private RSyntaxTextArea responseTA;
    private AgentDebuggerFrame parent;

    public RequestResponsePanel(AgentDebuggerFrame parent) {
        super(new BorderLayout());
        this.parent = parent;
        parent.addScriptChangedListener(this);
        parent.addStepChangedListener(this);

    }

    public void init() {
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        requestTA = new RSyntaxTextArea();
        requestTA.setEditable(false);
        requestTA.setAutoscrolls(true);
        requestTA.setHighlightCurrentLine(false);
        requestTA.setHyperlinksEnabled(false);
        DebuggerTheme.apply(requestTA);
        JScrollPane sp1 = new JScrollPane(requestTA, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel reqPane = new JPanel(new BorderLayout());
        SectionHeader requestHeader = new SectionHeader("Request");
        requestHeader.addButton(parent.getDebuggerActions().getSaveReqResponseAction());
        reqPane.add(BorderLayout.NORTH, requestHeader);
        reqPane.add(BorderLayout.CENTER, sp1);
        pane.setTopComponent(reqPane);

        responseTA = new RSyntaxTextArea();
        responseTA.setEditable(false);
        responseTA.setAutoscrolls(true);
        responseTA.setHighlightCurrentLine(false);
        responseTA.setHyperlinksEnabled(false);
        DebuggerTheme.apply(responseTA);
        JScrollPane sp2 = new JScrollPane(responseTA, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel responsePane = new JPanel(new BorderLayout());
        responsePane.add(BorderLayout.NORTH, new SectionHeader("Response"));
        responsePane.add(BorderLayout.CENTER, sp2);
        pane.setBottomComponent(responsePane);
        WindowState.track("request", pane, -1);
        pane.setResizeWeight(0.5D);

        add(pane, BorderLayout.CENTER);
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(parent.getDebuggerActions().getSaveReqResponseAction());
        requestTA.setPopupMenu(popupMenu);
        responseTA.setPopupMenu(popupMenu);
    }

    @Override
    public void scriptChanged(HDTestPlan plan) {
        setTextAreas(null);
    }

    @Override
    public void stepChanged(DebugStep step) {
        setTextAreas(step);
        requestTA.setCaretPosition(0);
        responseTA.setCaretPosition(0);
    }

    @Override
    public void stepEntered(DebugStep step) {
        setTextAreas(step);
        requestTA.setCaretPosition(0);
        responseTA.setCaretPosition(0);
    }

    @Override
    public void stepExited(DebugStep step) {
        setTextAreas(step);
        responseTA.setCaretPosition(0);
    }

    private void setTextAreas(DebugStep step) {
        requestTA.setText("");
        if (step != null && step.getRequest() != null && StringUtils.isNotBlank(step.getRequest().getLogMsg())) {
            requestTA.setText(step.getRequest().getLogMsg());
        }
        responseTA.setText("");
        if (step != null && step.getResponse() != null && StringUtils.isNotBlank(step.getResponse().getLogMsg())) {
            responseTA.setText(step.getResponse().getLogMsg());
        }
    }

    @Override
    public void writeText(Writer w) {
        List<DebugStep> steps = parent.getSteps();
        if (steps != null && !steps.isEmpty()) {
            for (DebugStep step : steps) {
                if (step != null && step.getRequest() != null && StringUtils.isNotBlank(step.getRequest().getLogMsg())) {
                    try {
                        w.write("\n############## REQUEST " + step.getStepRun().getStepIndex() + " #############\n");
                        w.write(step.getStepRun().getInfo() + "\n");
                        w.write(step.getRequest().getLogMsg() + "\n");
                        if (step != null && step.getResponse() != null
                                && StringUtils.isNotBlank(step.getResponse().getLogMsg())) {
                            w.write("\n############## RESPONSE " + step.getStepRun().getStepIndex()
                                    + " #############\n");
                            w.write(step.getResponse().getLogMsg() + "\n");
                        }
                    } catch (IOException e) {
                        System.err.println("Error writing doc: " + e);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasData() {
        boolean ret = false;
        List<DebugStep> steps = parent.getSteps();
        if (steps != null && !steps.isEmpty()) {
            ret = steps.stream().anyMatch(step -> step != null && step.getRequest() != null && StringUtils.isNotBlank(step.getRequest().getLogMsg()));
        }
        return ret;
    }

}
