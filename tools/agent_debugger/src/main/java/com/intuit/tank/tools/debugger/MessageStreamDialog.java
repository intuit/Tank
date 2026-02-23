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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

public class MessageStreamDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final String PHASE_ENTRY = "Entry";
    private static final String PHASE_EXIT = "Exit";

    private final DebugStep debugStep;
    private final JComboBox<String> phaseSelector;
    private final JComboBox<String> connectionSelector;
    private final JLabel summaryLabel;
    private final JTextArea streamTextArea;

    private final Map<String, DebugStep.MessageStreamSnapshot> empty = Collections.emptyMap();

    public MessageStreamDialog(Frame parent, DebugStep debugStep) {
        super(parent, true);
        this.debugStep = debugStep;

        setTitle("WebSocket Message Streams");
        setLayout(new BorderLayout());

        phaseSelector = new JComboBox<String>(new String[] { PHASE_ENTRY, PHASE_EXIT });
        connectionSelector = new JComboBox<String>();
        summaryLabel = new JLabel(" ");
        streamTextArea = new JTextArea();
        streamTextArea.setEditable(false);

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTextPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setSize(new Dimension(900, 560));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());

        registerListeners();
        reloadSelectors();
        WindowUtil.centerOnParent(this);
    }

    private JPanel buildTopPanel() {
        JPanel container = new JPanel(new BorderLayout());
        JPanel selectors = new JPanel(new GridLayout(2, 2, 8, 4));
        selectors.add(new JLabel("Snapshot:"));
        selectors.add(phaseSelector);
        selectors.add(new JLabel("Connection:"));
        selectors.add(connectionSelector);

        container.add(selectors, BorderLayout.NORTH);
        container.add(summaryLabel, BorderLayout.SOUTH);
        return container;
    }

    private JScrollPane buildTextPanel() {
        JScrollPane scrollPane = new JScrollPane(streamTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JButton close = new JButton("Close");
        close.addActionListener((ActionEvent e) -> setVisible(false));
        panel.add(close);
        return panel;
    }

    private void registerListeners() {
        phaseSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                reloadSelectors();
            }
        });

        connectionSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshContents();
            }
        });
    }

    private void reloadSelectors() {
        Map<String, DebugStep.MessageStreamSnapshot> snapshots = getCurrentPhaseSnapshots();
        connectionSelector.removeAllItems();
        for (String connectionId : snapshots.keySet()) {
            connectionSelector.addItem(connectionId);
        }
        if (connectionSelector.getItemCount() > 0) {
            connectionSelector.setSelectedIndex(0);
        }
        refreshContents();
    }

    private void refreshContents() {
        if (debugStep == null) {
            summaryLabel.setText("No step selected.");
            streamTextArea.setText("Select a script step to inspect its MessageStream snapshot.");
            return;
        }

        Map<String, DebugStep.MessageStreamSnapshot> snapshots = getCurrentPhaseSnapshots();
        String selectedConnection = (String) connectionSelector.getSelectedItem();

        if (StringUtils.isBlank(selectedConnection) || !snapshots.containsKey(selectedConnection)) {
            summaryLabel.setText("No MessageStream data for this phase.");
            streamTextArea.setText("No MessageStream snapshot was captured for this step/phase.");
            return;
        }

        DebugStep.MessageStreamSnapshot snapshot = snapshots.get(selectedConnection);
        summaryLabel.setText(buildSummary(snapshot));
        streamTextArea.setText(buildMessageText(snapshot));
        streamTextArea.setCaretPosition(0);
    }

    private Map<String, DebugStep.MessageStreamSnapshot> getCurrentPhaseSnapshots() {
        if (debugStep == null) {
            return empty;
        }
        if (PHASE_ENTRY.equals(phaseSelector.getSelectedItem())) {
            return new LinkedHashMap<String, DebugStep.MessageStreamSnapshot>(debugStep.getEntryMessageStreams());
        }
        return new LinkedHashMap<String, DebugStep.MessageStreamSnapshot>(debugStep.getExitMessageStreams());
    }

    private String buildSummary(DebugStep.MessageStreamSnapshot snapshot) {
        return "Connection=" + safe(snapshot.getConnectionId(), "(unknown)")
                + " | Connected=" + snapshot.isConnected()
                + " | Failed=" + snapshot.isFailed()
                + " | Messages=" + snapshot.getMessageCount()
                + " | ElapsedMs=" + snapshot.getElapsedTimeMs();
    }

    private String buildMessageText(DebugStep.MessageStreamSnapshot snapshot) {
        StringBuilder sb = new StringBuilder();

        if (snapshot.isFailed()) {
            sb.append("Failure Pattern: ").append(safe(snapshot.getFailurePattern(), "(none)")).append('\n');
            sb.append("Failure Message: ").append(safe(snapshot.getFailureMessage(), "(none)")).append('\n').append('\n');
        }

        List<DebugStep.MessageSnapshot> messages = snapshot.getMessages();
        if (messages == null || messages.isEmpty()) {
            sb.append("No messages captured.");
            return sb.toString();
        }

        for (DebugStep.MessageSnapshot message : messages) {
            sb.append('[').append(message.getIndex()).append("] ")
              .append('(').append(message.getRelativeTimeMs()).append("ms)")
              .append(" ")
              .append(safe(message.getContent(), ""))
              .append('\n');
        }
        return sb.toString();
    }

    private String safe(String value, String fallback) {
        return Objects.toString(value, fallback);
    }
}
