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
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;
import com.intuit.tank.proxy.config.ProxyConfiguration;

public class ProxyConfigPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private GeneralConfigPanel generalConfigPanel;
    private GeneralInclusionPanel generalInclusionPanel;
    private GeneralInclusionPanel generalExclusionPanel;
    private GeneralInclusionPanel bodyInclusionPanel;
    private GeneralInclusionPanel bodyExclusionPanel;
    private ConfigHandler configHandler;
    private ProxyConfigDialog pcd;

    public ProxyConfigPanel(ConfigHandler configHandler, ProxyConfigDialog proxyConfigDialog) {
        super();
        this.configHandler = configHandler;
        this.pcd = proxyConfigDialog;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("General", getGeneralConfigPanel());
        tabbedPane.add("Inclusions", getInclusionPanel());
        tabbedPane.add("Exclusions", getExclusionPanel());
        tabbedPane.add("body-inclusions", getBodyInclusionPanel());
        tabbedPane.add("body-exclusions", getBodyExclusionPanel());
        this.add(tabbedPane, BorderLayout.CENTER);

    }

    public GeneralInclusionPanel getRulePanel(IncludeType type) {
        if (type == IncludeType.BODY_EXCLUDE) {
            return getBodyExclusionPanel();
        }
        if (type == IncludeType.BODY_INCLUDE) {
            return getBodyInclusionPanel();
        }
        if (type == IncludeType.TRANSACTION_EXCLUDE) {
            return getExclusionPanel();
        }
        return getInclusionPanel();
    }

    private GeneralInclusionPanel getBodyExclusionPanel() {
        if (bodyExclusionPanel == null) {
            bodyExclusionPanel = new GeneralInclusionPanel(new BodyExclusionHandler(configHandler), pcd);
        }
        return bodyExclusionPanel;
    }

    private GeneralInclusionPanel getBodyInclusionPanel() {
        if (bodyInclusionPanel == null) {
            bodyInclusionPanel = new GeneralInclusionPanel(new BodyInclusionHandler(configHandler), pcd);
        }
        return bodyInclusionPanel;
    }

    private GeneralInclusionPanel getExclusionPanel() {
        if (generalExclusionPanel == null) {
            generalExclusionPanel = new GeneralInclusionPanel(new ExclusionHandler(configHandler), pcd);
        }
        return generalExclusionPanel;
    }

    private GeneralInclusionPanel getInclusionPanel() {
        if (generalInclusionPanel == null) {
            generalInclusionPanel = new GeneralInclusionPanel(new InclusionHandler(configHandler), pcd);
        }
        return generalInclusionPanel;
    }

    private GeneralConfigPanel getGeneralConfigPanel() {
        if (generalConfigPanel == null) {
            ProxyConfiguration pc = configHandler.getConfiguration();
            generalConfigPanel = new GeneralConfigPanel(pc.getPort(), pc.isFollowRedirects(),
                    pc.getOutputFile());
        }
        return generalConfigPanel;
    }

    public int getPort() {
        return getGeneralConfigPanel().getPort();
    }

    /**
     * @return
     */
    public boolean getFollowRedirects() {
        return getGeneralConfigPanel().getFollowRedirects();
    }

    public String getOutputFileName() {
        return getGeneralConfigPanel().getOutputFileName();
    }

    public Set<ConfigInclusionExclusionRule> getInclusionData() {
        return getInclusionPanel().getData();
    }

    public Set<ConfigInclusionExclusionRule> getExclusionData() {
        return getExclusionPanel().getData();
    }

    public Set<ConfigInclusionExclusionRule> getBodyInclusionData() {
        return getBodyInclusionPanel().getData();
    }

    public Set<ConfigInclusionExclusionRule> getBodyExclusionData() {
        return getBodyExclusionPanel().getData();
    }

    public void update() {
        getGeneralConfigPanel().update(configHandler.getConfiguration().getPort(),
                configHandler.getConfiguration().getOutputFile());
        getInclusionPanel().update(configHandler.getConfiguration().getInclusions());
        getExclusionPanel().update(configHandler.getConfiguration().getExclusions());
        getBodyInclusionPanel().update(configHandler.getConfiguration().getBodyInclusions());
        getBodyExclusionPanel().update(configHandler.getConfiguration().getBodyExclusions());
    }

}
