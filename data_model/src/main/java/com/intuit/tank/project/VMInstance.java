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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMRegion;

@Entity
@Table(name = "vm_instance")
public class VMInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_INSTANCE_ID = "instanceId";

    @Column(name = "job_id")
    private String jobId;

    @Column(name = "size")
    private String size;

    @Column(name = "ami_id")
    private String amiId;

    @Column(name = "status")
    private String status;

    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "region")
    @Enumerated(EnumType.STRING)
    private VMRegion region;

    @OneToMany(mappedBy = "vmInstance", fetch = FetchType.EAGER)
    private Set<JobVMInstance> vmInstances = new HashSet<JobVMInstance>();

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAmiId() {
        return amiId;
    }

    public void setAmiId(String amiId) {
        this.amiId = amiId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public VMRegion getRegion() {
        return region;
    }

    public void setRegion(VMRegion region) {
        this.region = region;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the vmImages
     */
    public Set<JobVMInstance> getVMInstances() {
        return vmInstances;
    }

    /**
     * @param vmImages
     *            the vmImages to set
     */
    public void setVMInstances(Set<JobVMInstance> vmImages) {
        this.vmInstances = vmImages;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("region", region).append("amiId", amiId)
                .toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VMInstance)) {
            return false;
        }
        VMInstance o = (VMInstance) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(VMInstance image) {
        return new Builder(image);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(65, 33).append(getId()).toHashCode();
    }

    public static class Builder extends VMImageBuilderBase<Builder> {

        private Builder() {
            super(new VMInstance());
        }

        private Builder(VMInstance img) {
            super(img);
        }

        public VMInstance build() {
            return getInstance();
        }
    }

    static class VMImageBuilderBase<GeneratorT extends VMImageBuilderBase<GeneratorT>> {
        private VMInstance instance;

        protected VMImageBuilderBase(VMInstance aInstance) {
            instance = aInstance;
        }

        protected VMInstance getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT size(String aValue) {
            instance.setSize(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT amiId(String aValue) {
            instance.setAmiId(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT status(String aValue) {
            instance.setStatus(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT instanceId(String aValue) {
            instance.setInstanceId(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT jobId(String aValue) {
            instance.setJobId(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT region(VMRegion aValue) {
            instance.setRegion(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT startTime(Date aValue) {
            instance.setStartTime(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT endTime(Date aValue) {
            instance.setEndTime(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT vmImages(Set<JobVMInstance> aValue) {
            instance.setVMInstances(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addVmImage(JobVMInstance aValue) {
            if (instance.getVMInstances() == null) {
                instance.setVMInstances(new HashSet<JobVMInstance>());
            }

            ((HashSet<JobVMInstance>) instance.getVMInstances()).add(aValue);

            return (GeneratorT) this;
        }
    }

}
