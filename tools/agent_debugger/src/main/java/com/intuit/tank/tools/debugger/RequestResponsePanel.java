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
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rtextarea.RTextArea;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.WebSocketRequest;
import com.intuit.tank.harness.data.WebSocketStep;

public class RequestResponsePanel extends JPanel implements StepListener, ScriptChangedListener, OutputTextWriter {

    private static final long serialVersionUID = 1L;

    private RTextArea requestTA;
    private RTextArea responseTA;
    private final AgentDebuggerFrame parent;
    private JLabel requestLabel;
    private JLabel responseLabel;
    private JPanel webSocketControls;
    private JToggleButton sentViewTB;
    private JToggleButton receivedViewTB;
    private JToggleButton beforeStepTB;
    private JToggleButton afterStepTB;
    private DebugStep currentStep;
    private boolean showingSent = false;
    private boolean showingAfterStep = true;

    public RequestResponsePanel(AgentDebuggerFrame parent) {
        super(new BorderLayout());
        this.parent = parent;
        parent.addScriptChangedListener(this);
        parent.addStepChangedListener(this);

    }

    public void init() {
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        requestTA = new RTextArea();
        requestTA.setEditable(false);
        requestTA.setAutoscrolls(true);
        requestTA.setHighlightCurrentLine(false);
        JScrollPane sp1 = new JScrollPane(requestTA, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel reqPane = new JPanel(new BorderLayout());
        requestLabel = new JLabel("Request:");
        reqPane.add(BorderLayout.NORTH, requestLabel);
        reqPane.add(BorderLayout.CENTER, sp1);
        pane.setTopComponent(reqPane);

        responseTA = new RTextArea();
        responseTA.setEditable(false);
        responseTA.setAutoscrolls(true);
        responseTA.setHighlightCurrentLine(false);
        JScrollPane sp2 = new JScrollPane(responseTA, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel responsePane = new JPanel(new BorderLayout());
        responseLabel = new JLabel("Response:");
        responsePane.add(BorderLayout.NORTH, responseLabel);
        responsePane.add(BorderLayout.CENTER, sp2);
        pane.setBottomComponent(responsePane);
        pane.setDividerLocation(0.5D);
        pane.setResizeWeight(0.5D);

        webSocketControls = buildWebSocketControls();
        webSocketControls.setVisible(false);
        add(webSocketControls, BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(parent.getDebuggerActions().getSaveReqResponseAction());
        requestTA.setPopupMenu(popupMenu);
        responseTA.setPopupMenu(popupMenu);
    }

    @Override
    public void scriptChanged(HDTestPlan plan) {
        currentStep = null;
        setTextAreas(null);
    }

    @Override
    public void stepChanged(DebugStep step) {
        currentStep = step;
        setTextAreas(step);
        requestTA.setCaretPosition(0);
        responseTA.setCaretPosition(0);
    }

    @Override
    public void stepEntered(DebugStep step) {
        currentStep = step;
        setTextAreas(step);
        requestTA.setCaretPosition(0);
        responseTA.setCaretPosition(0);
    }

    @Override
    public void stepExited(DebugStep step) {
        currentStep = step;
        setTextAreas(step);
        responseTA.setCaretPosition(0);
    }

    private void setTextAreas(DebugStep step) {
        if (isWebSocketStep(step)) {
            setWebSocketText(step);
            return;
        }

        webSocketControls.setVisible(false);
        requestLabel.setText("Request:");
        responseLabel.setText("Response:");
        requestTA.setText("");
        if (step != null && step.getRequest() != null && StringUtils.isNotBlank(step.getRequest().getLogMsg())) {
            requestTA.setText(step.getRequest().getLogMsg());
        }
        responseTA.setText("");
        if (step != null && step.getResponse() != null && StringUtils.isNotBlank(step.getResponse().getLogMsg())) {
            responseTA.setText(step.getResponse().getLogMsg());
        }
    }

    private JPanel buildWebSocketControls() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 4));
        panel.add(new JLabel("WebSocket:"));

        sentViewTB = new JToggleButton("Sent");
        receivedViewTB = new JToggleButton("Received", true);
        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(sentViewTB);
        viewGroup.add(receivedViewTB);
        panel.add(sentViewTB);
        panel.add(receivedViewTB);

        panel.add(new JLabel("Moment:"));
        beforeStepTB = new JToggleButton("Before Step");
        afterStepTB = new JToggleButton("After Step", true);
        ButtonGroup momentGroup = new ButtonGroup();
        momentGroup.add(beforeStepTB);
        momentGroup.add(afterStepTB);
        panel.add(beforeStepTB);
        panel.add(afterStepTB);

        sentViewTB.addActionListener(e -> {
            showingSent = true;
            refreshWebSocketPanels();
        });
        receivedViewTB.addActionListener(e -> {
            showingSent = false;
            refreshWebSocketPanels();
        });
        beforeStepTB.addActionListener(e -> {
            showingAfterStep = false;
            refreshWebSocketPanels();
        });
        afterStepTB.addActionListener(e -> {
            showingAfterStep = true;
            refreshWebSocketPanels();
        });

        return panel;
    }

    private void setWebSocketText(DebugStep step) {
        webSocketControls.setVisible(true);
        refreshWebSocketPanels();
    }

    private void refreshWebSocketPanels() {
        if (!isWebSocketStep(currentStep)) {
            return;
        }
        requestLabel.setText("WebSocket Step:");
        requestTA.setText(buildWebSocketSummary(currentStep));
        requestTA.setCaretPosition(0);

        boolean hasConnection = StringUtils.isNotBlank(resolveConnectionId(currentStep));
        beforeStepTB.setEnabled(!showingSent && hasConnection);
        afterStepTB.setEnabled(!showingSent && hasConnection);

        if (showingSent) {
            responseLabel.setText("Sent (This Step):");
            responseTA.setText(buildSentText(currentStep));
        } else {
            responseLabel.setText("Received (" + (showingAfterStep ? "After Step" : "Before Step") + "):");
            responseTA.setText(buildReceivedText(currentStep, showingAfterStep));
        }
        responseTA.setCaretPosition(0);
    }

    private String buildWebSocketSummary(DebugStep step) {
        WebSocketStep wsStep = (WebSocketStep) step.getStepRun();
        StringBuilder sb = new StringBuilder();
        sb.append("Use controls above to switch between Sent and Received views.\n");
        sb.append("Connection is taken from this step's connectionId.\n");
        sb.append("Action: ").append(wsStep.getAction()).append('\n');
        sb.append("Connection: ").append(StringUtils.defaultIfBlank(wsStep.getConnectionId(), "(unset)")).append('\n');
        if (wsStep.getRequest() != null && StringUtils.isNotBlank(wsStep.getRequest().getUrl())) {
            sb.append("URL: ").append(wsStep.getRequest().getUrl()).append('\n');
        }
        if (wsStep.getRequest() != null && wsStep.getRequest().getTimeoutMs() != null) {
            sb.append("TimeoutMs: ").append(wsStep.getRequest().getTimeoutMs()).append('\n');
        }
        if (!step.getErrors().isEmpty()) {
            sb.append("Validation Errors: ").append(step.getErrors().size()).append('\n');
            sb.append("Latest: ").append(step.getErrors().get(step.getErrors().size() - 1).getReason()).append('\n');
        }
        return sb.toString();
    }

    private String buildSentText(DebugStep step) {
        WebSocketStep wsStep = (WebSocketStep) step.getStepRun();
        WebSocketRequest request = wsStep.getRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("Action: ").append(wsStep.getAction()).append('\n');
        sb.append("Connection: ").append(StringUtils.defaultIfBlank(wsStep.getConnectionId(), "(unset)")).append("\n\n");

        if (request == null) {
            sb.append("No outbound request object for this step.");
            return sb.toString();
        }

        if (StringUtils.isNotBlank(request.getPayload())) {
            sb.append("Payload:\n").append(request.getPayload()).append('\n');
        } else {
            sb.append("No payload sent in this step.\n");
        }
        if (StringUtils.isNotBlank(request.getUrl())) {
            sb.append("\nURL: ").append(request.getUrl());
        }
        return sb.toString();
    }

    private String buildReceivedText(DebugStep step, boolean useAfterStep) {
        String connectionId = resolveConnectionId(step);
        if (StringUtils.isBlank(connectionId)) {
            return "No connectionId found on this step or captured snapshots.";
        }
        DebugStep.MessageStreamSnapshot snapshot = useAfterStep
                ? step.getExitMessageStreams().get(connectionId)
                : step.getEntryMessageStreams().get(connectionId);
        if (snapshot == null) {
            return "No MessageStream data captured for " + connectionId + " at "
                    + (useAfterStep ? "After Step" : "Before Step") + ".";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Connection: ").append(StringUtils.defaultIfBlank(snapshot.getConnectionId(), connectionId)).append('\n');
        sb.append("Connected: ").append(snapshot.isConnected()).append('\n');
        sb.append("Fail-on Status: ").append(snapshot.isFailed() ? "Fail" : "Pass").append('\n');
        sb.append("Message Count: ").append(snapshot.getMessageCount()).append('\n');
        sb.append("Elapsed Ms: ").append(snapshot.getElapsedTimeMs()).append('\n');
        if (StringUtils.isNotBlank(snapshot.getFailurePattern())) {
            sb.append("Fail Pattern: ").append(snapshot.getFailurePattern()).append('\n');
        }
        if (StringUtils.isNotBlank(snapshot.getFailureMessage())) {
            sb.append("Fail Message: ").append(snapshot.getFailureMessage()).append('\n');
        }
        sb.append('\n');

        List<DebugStep.MessageSnapshot> messages = snapshot.getMessages();
        if (messages == null || messages.isEmpty()) {
            sb.append("No received messages captured.");
            return sb.toString();
        }

        for (DebugStep.MessageSnapshot message : messages) {
            sb.append('[').append(message.getIndex()).append("] ")
                    .append('(').append(message.getRelativeTimeMs()).append("ms) ")
                    .append(StringUtils.defaultString(message.getContent())).append('\n');
        }
        return sb.toString();
    }

    private String resolveConnectionId(DebugStep step) {
        if (step == null || !isWebSocketStep(step)) {
            return null;
        }
        WebSocketStep wsStep = (WebSocketStep) step.getStepRun();
        if (StringUtils.isNotBlank(wsStep.getConnectionId())) {
            return wsStep.getConnectionId();
        }
        if (!step.getExitMessageStreams().isEmpty()) {
            return step.getExitMessageStreams().keySet().iterator().next();
        }
        if (!step.getEntryMessageStreams().isEmpty()) {
            return step.getEntryMessageStreams().keySet().iterator().next();
        }
        return null;
    }

    private boolean isWebSocketStep(DebugStep step) {
        return step != null && step.getStepRun() instanceof WebSocketStep;
    }

    @Override
    public void writeText(Writer w) {
        List<DebugStep> steps = parent.getSteps();
        if (steps != null && !steps.isEmpty()) {
            for (DebugStep step : steps) {
                if (isWebSocketStep(step)) {
                    try {
                        WebSocketStep wsStep = (WebSocketStep) step.getStepRun();
                        w.write("\n############## WEBSOCKET STEP " + wsStep.getStepIndex() + " #############\n");
                        w.write(wsStep.getInfo() + "\n");
                        w.write(buildSentText(step) + "\n");
                        String connectionId = resolveConnectionId(step);
                        if (StringUtils.isNotBlank(connectionId)) {
                            w.write("\n------ RECEIVED (After Step) " + connectionId + " ------\n");
                            w.write(buildReceivedText(step, true) + "\n");
                        }
                    } catch (IOException e) {
                        System.err.println("Error writing doc: " + e);
                    }
                }
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
            ret = steps.stream().anyMatch(step ->
                    (step != null && step.getRequest() != null && StringUtils.isNotBlank(step.getRequest().getLogMsg()))
                            || (step != null && isWebSocketStep(step) && !step.getExitMessageStreams().isEmpty()));
        }
        return ret;
    }

}
