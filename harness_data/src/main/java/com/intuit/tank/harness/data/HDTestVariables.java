/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * HDTestVariables
 * 
 * @author dangleton
 * 
 */
@XmlType(name = "variables", propOrder = { "allowOverride", "variables" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class HDTestVariables {

    @XmlAttribute
    private boolean allowOverride;

    @XmlElementWrapper(name = "variables")
    private List<HDVariable> variables = new ArrayList<HDVariable>();

    /**
     * 
     */
    public HDTestVariables() {
        super();
    }

    /**
     * @param allowOverride
     */
    public HDTestVariables(boolean allowOverride) {
        super();
        this.allowOverride = allowOverride;
    }

    /**
     * @param allowOverride
     */
    public HDTestVariables(boolean allowOverride, Map<String, String> vars) {
        super();
        this.allowOverride = allowOverride;
        addVariables(vars);
    }

    /**
     * @return the allowOverride
     */
    public boolean isAllowOverride() {
        return allowOverride;
    }

    /**
     * @param allowOverride
     *            the allowOverride to set
     */
    public void setAllowOverride(boolean allowOverride) {
        this.allowOverride = allowOverride;
    }

    /**
     * @return the variables
     */
    public List<HDVariable> getVariables() {
        return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(List<HDVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(String key, String value) {
        this.variables.add(new HDVariable(key, value));
    }

    public void addVariables(Map<String, String> vars) {
        for (Entry<String, String> var : vars.entrySet()) {
            this.variables.add(new HDVariable(var.getKey(), var.getValue()));
        }

    }

}
