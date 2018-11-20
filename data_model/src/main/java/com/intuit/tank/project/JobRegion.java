/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.envers.Audited;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.RegionRequest;

/**
 * JobRegionDescription describes a setting for the number of users to run on a specific AWS region.
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "job_region")
public class JobRegion extends BaseEntity implements RegionRequest, Comparable<JobRegion> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(JobRegion.class);
    private static final long serialVersionUID = 1L;

    @Column(name = "region")
    @Enumerated(EnumType.STRING)
    private VMRegion region = VMRegion.US_WEST_2;

    @NotNull
    @Column(name = "users", nullable = false)
    private String users;

    /**
     * 
     */
    public JobRegion() {

    }

    /**
     * @param region
     * @param users
     */
    public JobRegion(VMRegion region, String users) {
        this.region = region;
        this.users = users;
    }

    /**
     * @inheritDoc
     */
    @Override
    public VMRegion getRegion() {
        return region;
    }

    /**
     * @param region
     *            the region to set
     */
    public void setRegion(VMRegion region) {
        this.region = region;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getUsers() {
        if(StringUtils.isEmpty(users)) {
            return "0";
        }
        return users;
    }

    /**
     * @param users
     *            the users to set
     */
    public void setUsers(String users) {
        this.users = users;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobRegion)) {
            return false;
        }
        JobRegion o = (JobRegion) obj;
        if (getId() == 0) {
            return new EqualsBuilder().append(o.getRegion(), getRegion()).append(o.getUsers(), getUsers()).isEquals();
        }
        return new EqualsBuilder().append(getId(), o.getId()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 41).append(getRegion()).append(getUsers()).toHashCode();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int compareTo(JobRegion o) {
        return this.region.compareTo(o.region);
    }

}
