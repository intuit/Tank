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
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement(name = "workload", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlType(name = "workload", propOrder = { "name", "description", "tankHttpClientClass", "variables", "plans" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HDWorkload {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String description;

    @XmlElement(name = "client-class")
    private String tankHttpClientClass = "com.intuit.tank.httpclient3.TankHttpClient3";

    @XmlElement(name = "variables")
    private HDTestVariables variables;

    @XmlElement(name = "plan")
    private List<HDTestPlan> plans = new ArrayList<HDTestPlan>();

    /**
     * @return the variables
     */
    public HDTestVariables getVariables() {
        return variables;
    }

    /**
     * @return the tankHttpClientClass
     */
    public String getTankHttpClientClass() {
        return tankHttpClientClass;
    }

    /**
     * @param tankHttpClientClass
     *            the tankHttpClientClass to set
     */
    public void setTankHttpClientClass(String tankHttpClientClass) {
        this.tankHttpClientClass = tankHttpClientClass;
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(HDTestVariables variables) {
        this.variables = variables;
    }

    /**
     * @return the testPlanName
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the testPlanName to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the testPlanDescription
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param testPlanDescription
     *            the testPlanDescription to set
     */
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * @return the group
     */
    public List<HDTestPlan> getPlans() {
        return plans;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HDWorkload)) {
            return false;
        }
        HDWorkload o = (HDWorkload) obj;
        return new EqualsBuilder().append(name, o.name).append(description, o.description).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(description).append(plans).toHashCode();
    }

}
