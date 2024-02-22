/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.results;

/*
 * #%L
 * Cloud Rest API
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
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.intuit.tank.reporting.api.Namespace;

/**
 * TankResultPackage
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "TankResultPackage", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TankResultPackageType", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "jobId",
        "instanceId",
        "results"
})
public class TankResultPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "jobId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String jobId;

    @XmlElement(name = "instanceId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceId;

    @XmlElementWrapper(name = "results", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "TankResult", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private List<TankResult> results = new ArrayList<TankResult>();

    /**
     * @FrameworkUseOnly
     */
    public TankResultPackage() {
    }

    public TankResultPackage(String jobId, String instanceId, List<TankResult> results) {
        super();
        this.jobId = jobId;
        this.instanceId = instanceId;
        this.results = results;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return the instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @return the results
     */
    public List<TankResult> getResults() {
        return results;
    }

}
