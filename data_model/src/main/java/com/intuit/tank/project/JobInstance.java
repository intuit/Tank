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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Index;

import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.vmManager.JobUtil;

@Entity
@Table(name = "job_instance")
public class JobInstance extends BaseJob {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_STATUS = "status";
    public static final String PROPERTY_START_TIME = "startTime";
    public static final String PROPERTY_END_TIME = "endTime";

    @Column(name = "workload_id")
    private int workloadId;

    @Column(name = "total_virtual_users")
    private int totalVirtualUsers;

    @Column(name = "name")
    private String name;

    @Column(name = "scheduled_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledTime;

    @Column(name = "creator")
    private String creator;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Index(name = "IDX_JQ_STATUS")
    private JobQueueStatus status = JobQueueStatus.Created;

    @Column(name = "start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "job_details")
    @Lob
    private String jobDetails;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", referencedColumnName = "id")
    private Set<JobVMInstance> vmInstances = new HashSet<JobVMInstance>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_instance_to_job_notification_version", joinColumns = @JoinColumn(name = "job_instance_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "objectId", column = @Column(name = "notification_id")),
            @AttributeOverride(name = "versionId", column = @Column(name = "notification_version_id")),
            @AttributeOverride(name = "objectClass", column = @Column(name = "notification_object_class")) })
    private Set<EntityVersion> notificationVersions = new HashSet<EntityVersion>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_instance_to_data_file", joinColumns = @JoinColumn(name = "job_instance_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "objectId", column = @Column(name = "datafile_id")),
            @AttributeOverride(name = "versionId", column = @Column(name = "datafile_version_id")),
            @AttributeOverride(name = "objectClass", column = @Column(name = "datafile_object_class")) })
    private Set<EntityVersion> dataFileVersions = new HashSet<EntityVersion>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_instance_to_job_regions", joinColumns = @JoinColumn(name = "job_instance_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "objectId", column = @Column(name = "jobregion_id")),
            @AttributeOverride(name = "versionId", column = @Column(name = "jobregion_version_id")),
            @AttributeOverride(name = "objectClass", column = @Column(name = "jobregion_object_class")) })
    private Set<EntityVersion> jobRegionVersions = new HashSet<EntityVersion>();

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "job_instance_varibles", joinColumns = @JoinColumn(name = "job_id"))
    Map<String, String> variables = new HashMap<String, String>(); // maps from attribute name to value

    /**
     * 
     */
    public JobInstance() {
        super();
    }

    /**
     * @param workload
     * @param name
     */
    public JobInstance(Workload workload, String name) {
        super(workload.getJobConfiguration());
        // this.workloadVersion = workloadVersion;
        this.workloadId = workload.getId();
        this.name = name;
        this.totalVirtualUsers = JobUtil.calculateTotalVirtualUsers(workload.getJobConfiguration().getJobRegions());
    }

    /**
     * 
     * @return
     */
    public String getJobDetails() {
        return jobDetails;
    }

    /**
     * 
     * @param jobDetails
     */
    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator
     *            the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the variables
     */
    public Map<String, String> getVariables() {
        return variables != null ? variables : new HashMap<String, String>();
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
    
    /**
     * @return the dataFileIds
     */
    public Set<Integer> getDataFileIds() {
        return dataFileVersions.stream().map(EntityVersion::getObjectId).collect(Collectors.toSet());
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the workloadId
     */
    public int getWorkloadId() {
        return workloadId;
    }

    /**
     * @param workloadId
     *            the workloadId to set
     */
    public void setWorkloadId(int workloadId) {
        this.workloadId = workloadId;
    }

    /**
     * @return the totalVirtualUsers
     */
    public int getTotalVirtualUsers() {
        return totalVirtualUsers;
    }

    /**
     * @return the notificationVersions
     */
    public Set<EntityVersion> getNotificationVersions() {
        return notificationVersions;
    }

    /**
     * @param notificationVersions
     *            the notificationVersions to set
     */
    protected void setNotificationVersions(Set<EntityVersion> notificationVersions) {
        this.notificationVersions = notificationVersions;
    }

    /**
     * @return the dataFileVersions
     */
    public Set<EntityVersion> getDataFileVersions() {
        return dataFileVersions;
    }

    /**
     * @param dataFileVersions
     *            the dataFileVersions to set
     */
    protected void setDataFileVersions(Set<EntityVersion> dataFileVersions) {
        this.dataFileVersions = dataFileVersions;
    }

    /**
     * @return the jobRegionVersions
     */
    public Set<EntityVersion> getJobRegionVersions() {
        return jobRegionVersions;
    }

    /**
     * @param jobRegionVersions
     *            the jobRegionVersions to set
     */
    protected void setJobRegionVersions(Set<EntityVersion> jobRegionVersions) {
        this.jobRegionVersions = jobRegionVersions;
    }

    /**
     * @param totalVirtualUsers
     *            the totalVirtualUsers to set
     */
    public void setTotalVirtualUsers(int totalVirtualUsers) {
        this.totalVirtualUsers = totalVirtualUsers;
    }

    /**
     * @return the scheduledTime
     */
    public Date getScheduledTime() {
        return scheduledTime;
    }

    /**
     * @param scheduledTime
     *            the scheduledTime to set
     */
    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the status
     */
    public JobQueueStatus getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(JobQueueStatus status) {
        this.status = status;
    }

    /**
     * @return the vmInstances
     */
    public Set<JobVMInstance> getVmInstances() {
        return vmInstances;
    }

    /**
     * @param vmInstances
     *            the vmInstances to set
     */
    public void setVmInstances(Set<JobVMInstance> vmInstances) {
        this.vmInstances = vmInstances;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("status", status).append("name", name)
                .append("reportingMode", getReportingMode())
                .toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobInstance)) {
            return false;
        }
        JobInstance o = (JobInstance) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

}