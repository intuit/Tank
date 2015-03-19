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

import com.intuit.tank.project.ScriptFilter;

public class ScriptfilterEnvelope implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean checked;
    private ScriptFilter filter;

    public ScriptfilterEnvelope(ScriptFilter scriptFilter) {
        this.filter = scriptFilter;
        this.checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ScriptFilter getFilter() {
        return filter;
    }

    public void setFilter(ScriptFilter filter) {
        this.filter = filter;
    }

    public String getName() {
        return filter.getName();
    }

    public void setName(String name) {
        filter.setName(name);
    }

    public String getProductName() {
        return filter.getProductName();
    }

    public void setProductName(String productName) {
        filter.setProductName(productName);
    }

}
