/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.automation;

/*
 * #%L
 * Automation Rest Api
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

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.RegionRequest;

/**
 * region
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "automationJobRegion", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationJobRegion", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "region",
        "users"
})
public class AutomationJobRegion implements Serializable, RegionRequest {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "region", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private VMRegion region = VMRegion.US_WEST_1;

    @XmlElement(name = "users", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String users;

    /**
     * @param region
     * @param users
     */
    public AutomationJobRegion(VMRegion region, String users) {
        this.region = region != null ? region : VMRegion.US_EAST;
        this.users = users;
    }

    /**
     * {@Framework_use_only}
     */
    protected AutomationJobRegion() {
    }

    /**
     * @return the region
     */
    public VMRegion getRegion() {
        return region;
    }

    /**
     * @return the users
     */
    public String getUsers() {
        return users;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("region", region).append("users", users)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AutomationJobRegion)) {
            return false;
        }
        AutomationJobRegion o = (AutomationJobRegion) obj;
        return new EqualsBuilder().append(o.region, region).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(27, 45).append(getRegion()).append(getUsers()).toHashCode();
    }

}
