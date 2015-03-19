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

import java.util.Set;

import javax.swing.JDialog;

import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;

public abstract class Handler {

    protected ConfigHandler handler;

    public Handler(ConfigHandler handler) {
        this.handler = handler;
    }

    public void addRule(RuleTableModel rtm, JDialog parent) {
        RuleEditorDialog red = new RuleEditorDialog(parent);
        red.setVisible(true);
        if (!red.isCancelFlag()) {
            ConfigInclusionExclusionRule rule = red.getRule();
            rtm.addRule(rule);
        }
    }

    public void removeRule(RuleTableModel rtm, ConfigInclusionExclusionRule rule, int rowIndex) {
        rtm.removeRule(rule, rowIndex);
    }

    public void editRule(RuleTableModel rtm, ConfigInclusionExclusionRule rule, int rowIndex, JDialog parent) {
        RuleEditorDialog red = new RuleEditorDialog(parent, rule);
        red.setVisible(true);
        if (!red.isCancelFlag()) {
            ConfigInclusionExclusionRule updatedRule = red.getRule();
            rtm.replaceRule(rule, updatedRule, rowIndex);
        }
    }

    public abstract Set<ConfigInclusionExclusionRule> getData();

}
