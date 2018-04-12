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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GeneralConfigPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel portLabel;
    private JLabel outputFileLabel;
    private JTextField portField;
    private JTextField outputFileField;
    private JCheckBox followRedirectsCB;

    private int port = 8888;
    private String outputFile = "";

    private JButton browseButton;

    private boolean collapseRedirects = true;

    public GeneralConfigPanel(int port, boolean collapseRedirects, String outputFile) {
        this.port = port;
        this.outputFile = outputFile;
        this.collapseRedirects = collapseRedirects;
        initialize();
    }

    public GeneralConfigPanel() {
        this(8888, true, "");
        initialize();
    }

    private void initialize() {

        GridBagLayout gbl = new GridBagLayout();
        this.setLayout(gbl);
        this.setPreferredSize(new Dimension(500, 200));
        this.setMinimumSize(new Dimension(500, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        this.add(getPortLabel(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        this.add(getPortField(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        this.add(getOutputFileLabel(), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        this.add(getOutputFileField(), gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        this.add(browseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        this.add(new JLabel("Collapse Redirects :"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        this.add(getFollowRedirectsCheckbox(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        JPanel jPanel = new JPanel();
        this.add(jPanel, gbc);
    }

    /**
     * @return
     */
    private JCheckBox getFollowRedirectsCheckbox() {
        if (followRedirectsCB == null) {
            followRedirectsCB = new JCheckBox();
            followRedirectsCB.setSelected(collapseRedirects);
        }
        return followRedirectsCB;
    }

    /**
     * @return the portLabel
     */
    public JLabel getPortLabel() {
        if (portLabel == null) {
            portLabel = new JLabel("Port :");
        }

        return portLabel;
    }

    /**
     * @param portLabel
     *            the portLabel to set
     */
    public void setPortLabel(JLabel portLabel) {
        this.portLabel = portLabel;
    }

    /**
     * @return the outputFileLabel
     */
    public JLabel getOutputFileLabel() {
        if (outputFileLabel == null) {
            outputFileLabel = new JLabel("Output File :");
        }
        return outputFileLabel;
    }

    /**
     * @param outputFileLabel
     *            the outputFileLabel to set
     */
    public void setOutputFileLabel(JLabel outputFileLabel) {
        this.outputFileLabel = outputFileLabel;
    }

    /**
     * @return the portField
     */
    public JTextField getPortField() {
        if (portField == null) {
            portField = new JTextField();
            portField.setSize(new Dimension(70, 30));
            portField.setPreferredSize(new Dimension(70, 30));
            portField.setText(String.valueOf(port));
        }
        return portField;
    }

    /**
     * @return the outputFileField
     */
    public JTextField getOutputFileField() {
        if (outputFileField == null) {
            outputFileField = new JTextField();
            Dimension d = outputFileField.getPreferredSize();
            d.width = 300;
            outputFileField.setSize(d);
            outputFileField.setPreferredSize(d);
            File selectedFile = new File(outputFile);
            try {
                outputFileField.setText(selectedFile.getCanonicalPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose Output File...");
            fileChooser.setCurrentDirectory(selectedFile.getParentFile());
            fileChooser.setFileFilter(new XmlFileFilter());

            browseButton = new JButton("Browse...");
            browseButton.addActionListener((ActionEvent e) -> {
                int showOpenDialog = fileChooser.showSaveDialog(GeneralConfigPanel.this);
                if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
                    try {
                        outputFileField.setText(fileChooser.getSelectedFile().getCanonicalPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });

        }
        return outputFileField;
    }

    public String getOutputFileName() {
        return getOutputFileField().getText();
    }

    public int getPort() {
        return Integer.parseInt(getPortField().getText());
    }

    public void update(int port, String fileName) {
        getPortField().setText(String.valueOf(port));
        getOutputFileField().setText(fileName);
    }

    /**
     * @return
     */
    public boolean getFollowRedirects() {
        return getFollowRedirectsCheckbox().isSelected();
    }

}
