package com.intuit.tank.vm.agent.messages;

/*
 * #%L
 * Intuit Tank Api
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name = "watsAgentStartRequest", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WatsAgentStartRequest", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "concurrentUsers",
        "scriptUrl",
        "rampTime",
        "jobId",
        "simulationTime",
        "startUsers",
        "userIntervalIncrement",
        "agentInstanceNum",
        "totalAgents"
})
public class WatsAgentStartRequest implements Serializable {

    private static final long serialVersionUID = -8730856220203938997L;

    @XmlElement(name = "concurrentUsers", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int concurrentUsers;

    @XmlElement(name = "scriptUrl", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String scriptUrl;

    @XmlElement(name = "rampTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private long rampTime;

    @XmlElement(name = "jobId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String jobId;

    @XmlElement(name = "simulationTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private long simulationTime;

    @XmlElement(name = "startUsers", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int startUsers;

    @XmlElement(name = "userIntervalIncrement", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int userIntervalIncrement;

    @XmlElement(name = "agentInstanceNum", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int agentInstanceNum;

    @XmlElement(name = "totalAgents", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int totalAgents;

    public WatsAgentStartRequest() {
    }

    public WatsAgentStartRequest(String scriptUrl, int users, long ramp) {
        this.scriptUrl = scriptUrl;
        concurrentUsers = users;
        rampTime = ramp;
    }

    public WatsAgentStartRequest(WatsAgentStartRequest copy) {
        this.scriptUrl = copy.scriptUrl;
        this.concurrentUsers = copy.concurrentUsers;
        this.rampTime = copy.rampTime;
        this.jobId = copy.jobId;
        this.simulationTime = copy.simulationTime;
        this.startUsers = copy.startUsers;
        this.userIntervalIncrement = copy.userIntervalIncrement;
        this.agentInstanceNum = copy.agentInstanceNum;
        this.totalAgents = copy.totalAgents;
    }

    /**
     * @return the agentInstanceNum
     */
    public int getAgentInstanceNum() {
        return agentInstanceNum;
    }

    /**
     * @param agentInstanceNum
     *            the agentInstanceNum to set
     */
    public void setAgentInstanceNum(int agentInstanceNum) {
        this.agentInstanceNum = agentInstanceNum;
    }

    /**
     * @return the scriptUrl
     */
    public String getScriptUrl() {
        return scriptUrl;
    }

    /**
     * @param scriptUrl
     *            the scriptUrl to set
     */
    public void setScriptUrl(String scriptUrl) {
        this.scriptUrl = scriptUrl;
    }

    public int getConcurrentUsers() {
        return concurrentUsers;
    }

    public int getStartUsers() {
        return startUsers;
    }

    public long getRampTime() {
        return rampTime;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(long simulationTime) {
        this.simulationTime = simulationTime;
    }

    public void setStartUsers(int start) {
        this.startUsers = start;
    }

    public void setUserIntervalIncrement(int userIntervalIncrement) {
        this.userIntervalIncrement = userIntervalIncrement;
    }

    public int getUserIntervalIncrement() {
        return userIntervalIncrement > 0 ? userIntervalIncrement : 1;
    }

    /**
     * @return
     */
    public int getTotalAgents() {
        return totalAgents;
    }

    /**
     * @param totalAgents
     *            the totalAgents to set
     */
    public void setTotalAgents(int totalAgents) {
        this.totalAgents = totalAgents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
