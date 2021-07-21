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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.harness.StopBehavior;

/**
 * StandaloneAgentRequest represents a request from the controller to start start a job with the pecified number of
 * users.
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "standalone-agent-request", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandaloneAgentRequest", namespace = Namespace.NAMESPACE_V1)
public class StandaloneAgentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "job-id", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String jobId;

    @XmlElement(name = "instance-id", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceId;

    @XmlElement(name = "users", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int users;

    @XmlElement(name = "stop-behavior", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String stopBehavior = StopBehavior.END_OF_SCRIPT_GROUP.name();

    /**
     * 
     */
    public StandaloneAgentRequest() {
        super();
    }

    /**
     * 
     * @param jobId
     * @param instanceId
     * @param users
     */
    public StandaloneAgentRequest(String jobId, String instanceId, int users) {
        super();
        this.jobId = jobId;
        this.instanceId = instanceId;
        this.users = users;
    }

    /**
     * 
     * @return
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * 
     * @return
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * 
     * @return
     */
    public int getUsers() {
        return users;
    }

    /**
     * 
     * @param users
     */
    public void setUsers(int users) {
        this.users = users;
    }

    /**
     * @return the stopBehavior
     */
    public String getStopBehavior() {
        return stopBehavior;
    }

    /**
     * @param stopBehavior
     *            the stopBehavior to set
     */
    public void setStopBehavior(String stopBehavior) {
        this.stopBehavior = stopBehavior;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StandaloneAgentRequest)) {
            return false;
        }
        StandaloneAgentRequest o = (StandaloneAgentRequest) obj;
        return new EqualsBuilder().append(o.instanceId, instanceId).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(21, 25).append(instanceId).toHashCode();
    }

}
