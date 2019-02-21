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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.proxy.config.CommonsProxyConfiguration;
import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;
import com.intuit.tank.proxy.config.MatchType;
import com.intuit.tank.proxy.config.ProxyConfiguration;
import com.intuit.tank.proxy.config.TransactionPart;

public class ProxyConfigDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JToolBar toolbar;
    private ProxyConfigPanel proxyConfigPanel;
    private ConfigHandler configHandler = new ConfigHandler();

    public ProxyConfigDialog(JFrame parent) {
        super(parent, true);
        configHandler = new ConfigHandler();
        initialize();
    }

    private void initialize() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getProxyConfigPanel(), BorderLayout.CENTER);
        getContentPane().add(getToolbar(), BorderLayout.NORTH);
        setPreferredSize(new Dimension(600, 300));
        setMinimumSize(new Dimension(600, 300));
    }

    private ProxyConfigPanel getProxyConfigPanel() {
        if (proxyConfigPanel == null) {
            proxyConfigPanel = new ProxyConfigPanel(configHandler, this);
        }
        return proxyConfigPanel;
    }

    public static void main(String[] args) {
        ProxyConfigDialog pcd = new ProxyConfigDialog(null);
        pcd.showDialog();
    }

    private JToolBar getToolbar() {
        if (toolbar == null) {
            toolbar = new JToolBar();
            JButton saveButton = new JButton("Save", new ImageIcon(
                    ProxyConfigDialog.class.getResource("/icons/16/save_as.png")));
            saveButton.addActionListener((ActionEvent e) -> {
                try {
                    saveConfig(false);
                } catch (IOException e1) {
                    throw new IllegalArgumentException(e1);
                }
            });
            JButton saveasButton = new JButton("Save As...", new ImageIcon(
                    ProxyConfigDialog.class.getResource("/icons/16/save_as.png")));
            saveasButton.addActionListener( (ActionEvent e) -> {
                try {
                    saveConfig(true);
                } catch (IOException e1) {
                    throw new IllegalArgumentException(e1);
                }
            });
            JButton openButton = new JButton("Open", new ImageIcon(
                    ProxyConfigDialog.class.getResource("/icons/16/open_folder.png")));
            openButton.addActionListener((ActionEvent e) -> {
                openConfig();
            });
            toolbar.add(Box.createHorizontalStrut(5));
            toolbar.add(openButton);
            toolbar.add(Box.createHorizontalStrut(5));
            toolbar.add(saveButton);
            toolbar.add(Box.createHorizontalStrut(5));
            toolbar.add(saveasButton);
        }

        return toolbar;
    }

    protected void openConfig() {
        JFileChooser fileChooser = new JFileChooser();
        File file = new File(".");
        fileChooser.setCurrentDirectory(file);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new XmlFileFilter());
        int showOpenDialog = fileChooser.showOpenDialog(this);

        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            configHandler.setConfigFile(fileChooser.getSelectedFile().getAbsolutePath());
            getProxyConfigPanel().update();
        }
    }

    protected void saveConfig(boolean saveAs) throws IOException {

        int port = getProxyConfigPanel().getPort();
        boolean followRedirecs = getProxyConfigPanel().getFollowRedirects();
        String outputFile = getProxyConfigPanel().getOutputFileName();

        File file = new File(outputFile);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (file.isDirectory()) {
            file = new File(file, CommonsProxyConfiguration.SUGGESTED_CONFIG_NAME);
        } else if (!file.getName().toLowerCase().endsWith(".xml")) {
            String fileName = file.getName();
            if (StringUtils.isEmpty(file.getName())) {
                fileName = CommonsProxyConfiguration.SUGGESTED_CONFIG_NAME;
            } else {
                fileName = file.getName() + ".xml";
            }
            file = new File(file.getParentFile(), fileName);
        }
        if ((file.exists() && !file.canWrite()) || !file.getParentFile().canWrite()) {
            JOptionPane.showMessageDialog(this, "Cannot write to file - " + file.getCanonicalPath(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        outputFile = file.getCanonicalPath();

        Set<ConfigInclusionExclusionRule> inclusions = getProxyConfigPanel().getInclusionData();
        Set<ConfigInclusionExclusionRule> exclusions = getProxyConfigPanel().getExclusionData();
        Set<ConfigInclusionExclusionRule> bodyInclusions = getProxyConfigPanel().getBodyInclusionData();
        Set<ConfigInclusionExclusionRule> bodyExclusions = getProxyConfigPanel().getBodyExclusionData();
        String configFile = saveAs ? null : configHandler.getConfigFile();
        save(port, followRedirecs, outputFile, inclusions, exclusions, bodyInclusions, bodyExclusions, configFile);
    }

    public Set<ConfigInclusionExclusionRule> getExclusions() {
        return getProxyConfigPanel().getExclusionData();
    }

    public Set<ConfigInclusionExclusionRule> getInclusions() {
        return getProxyConfigPanel().getInclusionData();
    }

    private void save(int port, boolean followRedirecs, String outputFile,
            Set<ConfigInclusionExclusionRule> inclusions,
            Set<ConfigInclusionExclusionRule> exclusions, Set<ConfigInclusionExclusionRule> bodyInclusions,
            Set<ConfigInclusionExclusionRule> bodyExclusions, String configFileName) {
        String fileName = "";
        if (StringUtils.isEmpty(configFileName)) {
            JFileChooser fileChooser = new JFileChooser();
            File file = new File(".");
            fileChooser.setCurrentDirectory(file);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new XmlFileFilter());
            int showSaveDialog = fileChooser.showSaveDialog(this);
            if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
                String selectedFile = fileChooser.getSelectedFile().getName();
                if (!selectedFile.endsWith(".xml")) {
                    selectedFile = selectedFile + ".xml";
                }
                fileName = fileChooser.getSelectedFile().getParent() + "/" + selectedFile;
                configHandler.setConfigFile(fileName);
            } else {
                return;
            }
        } else {
            fileName = configFileName;
        }
        CommonsProxyConfiguration.save(port, followRedirecs, outputFile, inclusions, exclusions, bodyInclusions,
                bodyExclusions,
                fileName);
        getProxyConfigPanel().update();
        configHandler.setConfigFile(fileName);
    }

    public void showDialog() {
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setVisible(true);
    }

    public ProxyConfiguration getConfiguration() {
        return configHandler.getConfiguration();
    }

    public void addHostRule(String match, IncludeType include) {
        GeneralInclusionPanel rulePanel = proxyConfigPanel.getRulePanel(include);
        rulePanel.addRule(new ConfigInclusionExclusionRule(TransactionPart.request, "host", MatchType.equals, match));
    }

}
