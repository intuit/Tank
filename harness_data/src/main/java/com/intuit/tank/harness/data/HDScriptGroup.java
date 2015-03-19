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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "testSuite", propOrder = { "name", "description", "loop", "variable", "groupSteps" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class HDScriptGroup {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String description;
    @XmlAttribute
    private int loop;
    @XmlElement
    private HDVariable variable;
    @XmlElement(name = "testGroup")
    private List<HDScript> groupSteps = new ArrayList<HDScript>();

    @XmlTransient
    private HDTestPlan parent;

    public HDTestPlan getParent() {
        return parent;
    }

    public void setParent(HDTestPlan parent) {
        this.parent = parent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the loop
     */
    public int getLoop() {
        return loop;
    }

    /**
     * @param loop
     *            the loop to set
     */
    public void setLoop(int loop) {
        this.loop = loop;
    }

    /**
     * @return the variable
     */
    public HDVariable getVariable() {
        return variable;
    }

    /**
     * @param variable
     *            the variable to set
     */
    public void setVariable(HDVariable variable) {
        this.variable = variable;
    }

    /**
     * @return the groupSteps
     */
    public List<HDScript> getGroupSteps() {
        return groupSteps;
    }

}
