/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.projects.models;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.RegionRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "automationJobRegion", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationJobRegion", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "region",
        "users"
})
public class AutomationJobRegion implements Serializable, RegionRequest {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @XmlElement(name = "region", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private VMRegion region = VMRegion.US_WEST_1;

    @XmlElement(name = "users", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String users;

    @XmlElement(name = "percentage", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String percentage;

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
     * @return the percentage
     */
    public String getPercentage() {
        return percentage;
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
