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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMRole;

@Entity
@Table(name = "job_vm_instance")
public class JobVMInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "job_id", updatable = false, insertable = false)
    private JobConfiguration job;

    @ManyToOne
    @JoinColumn(name = "vm_instance_id")
    private VMInstance vmInstance;

    @Column(name = "user_count")
    private int userCount;

    @Column(name = "status")
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "vm_role")
    private VMRole vmRole;

    public JobConfiguration getJob() {
        return job;
    }

    protected void setJob(JobConfiguration job) {
        this.job = job;
    }

    public void setParent(JobConfiguration job) {
        this.job = job;
    }

    public VMInstance getVMInstance() {
        return vmInstance;
    }

    public void setVMInstance(VMInstance instance) {
        this.vmInstance = instance;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the vmRole
     */
    public VMRole getVmRole() {
        return vmRole;
    }

    /**
     * @param vmRole
     *            the vmRole to set
     */
    public void setVmRole(VMRole vmRole) {
        this.vmRole = vmRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("status", status)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobVMInstance)) {
            return false;
        }
        JobVMInstance o = (JobVMInstance) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }
}
