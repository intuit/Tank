package com.intuit.tank.project;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import com.intuit.tank.util.Messages;

@Named
@ConversationScoped
public class AddTestPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Messages messages;

    @Inject
    private WorkloadScripts workloadScripts;

    private String name;
    private int percentage;
    private boolean hasErrors = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPercentage() {
        return percentage;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void clear() {
        this.name = null;
        this.percentage = 0;
        this.hasErrors = false;
    }

    public void save() {
        if (StringUtils.isBlank(name)) {
            messages.error("Name is required.");
        }
        if (percentage < 0 || percentage > 100) {
            messages.error("User Percentage must be between 0 and 100.");
        }

        if (messages.isEmpty()) {
            workloadScripts.addTestPlan(TestPlan.builder().name(name).usersPercentage(percentage).build());
            clear();
        } else {
            hasErrors = true;
        }
    }

}
