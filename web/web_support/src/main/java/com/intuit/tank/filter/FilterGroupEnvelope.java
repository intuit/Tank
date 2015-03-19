package com.intuit.tank.filter;

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

import com.intuit.tank.project.ScriptFilterGroup;

public class FilterGroupEnvelope implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean checked;
    private ScriptFilterGroup filterGroup;

    public FilterGroupEnvelope(ScriptFilterGroup scriptFilterGroup) {
        this.filterGroup = scriptFilterGroup;
        this.checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ScriptFilterGroup getFilterGroup() {
        return filterGroup;
    }

    public void setFilterGroup(ScriptFilterGroup filter) {
        this.filterGroup = filter;
    }

    public String getName() {
        return filterGroup.getName();
    }

    public void setName(String name) {
        filterGroup.setName(name);
    }

    public String getProductName() {
        return filterGroup.getProductName();
    }

    public void setProductName(String productName) {
        filterGroup.setProductName(productName);
    }

}
