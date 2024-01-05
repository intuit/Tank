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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "testPlan", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlType(name = "testPlan", propOrder = { "testPlanName", "userPercentage", "group" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HDTestPlan {

    @XmlAttribute
    private String testPlanName;

    @XmlAttribute
    private int userPercentage = 100;

    @XmlElement(name = "testSuite")
    private List<HDScriptGroup> group = new ArrayList<HDScriptGroup>();

    @XmlTransient
    private HDTestVariables variables;

    public HDTestPlan() {
        super();
    }

    public HDTestPlan(String testPlanName, int userPercentage, List<HDScriptGroup> group) {
        super();
        this.testPlanName = testPlanName;
        this.userPercentage = userPercentage;
        this.group = group;
    }

    /**
     * 
     * @return
     */
    public HDTestVariables getVariables() {
        return variables;
    }

    /**
     * 
     * @param variables
     */
    public void setVariables(HDTestVariables variables) {
        this.variables = variables;
    }

    /**
     * 
     * @return
     */
    public int getUserPercentage() {
        return userPercentage;
    }

    /**
     * 
     * @param userPercentage
     */
    public void setUserPercentage(int userPercentage) {
        this.userPercentage = userPercentage;
    }

    /**
     * @return the testPlanName
     */
    public String getTestPlanName() {
        return testPlanName;
    }

    /**
     * @param testPlanName
     *            the testPlanName to set
     */
    public void setTestPlanName(String testPlanName) {
        this.testPlanName = testPlanName;
    }

    /**
     * @return the group
     */
    public List<HDScriptGroup> getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return testPlanName + " (" + userPercentage + "%)";
    }

}
