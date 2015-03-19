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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import com.intuit.tank.vm.vmManager.JobUtil;

@Entity
@Audited
@Table(name = "job_configuration")
public class JobConfiguration extends BaseJob {

    private static final long serialVersionUID = 1L;

    @OneToOne
    private Workload workload;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "job_configuration_to_job_notification", joinColumns = @JoinColumn(name = "job_configuration_id"),
            inverseJoinColumns = @JoinColumn(name = "notification_id"))
    private Set<JobNotification> notifications = new HashSet<JobNotification>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_configuration_to_data_file", joinColumns = @JoinColumn(name = "job_configuration_id"))
    @Column(name = "data_file_id")
    private Set<Integer> dataFileIds = new HashSet<Integer>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "job_configuration_to_job_regions", joinColumns = @JoinColumn(name = "job_configuration_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id"))
    private Set<JobRegion> jobRegions = new HashSet<JobRegion>();

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "job_config_varibles", joinColumns = @JoinColumn(name = "job_configuration_id"))
    Map<String, String> variables = new HashMap<String, String>(); // maps from attribute name to value

    /**
     * 
     */
    public JobConfiguration() {
        super();
    }

    /**
     * @return the workload
     */
    public Workload getWorkload() {
        return workload;
    }

    /**
     * @param workload
     *            the workload to set
     */
    protected void setWorkload(Workload workload) {
        this.workload = workload;
    }

    /**
     * @param workload
     *            the workload to set
     */
    public void setParent(Workload workload) {
        this.workload = workload;
    }

    /**
     * @return the notifications
     */
    public Set<JobNotification> getNotifications() {
        return notifications;
    }

    /**
     * @param notifications
     *            the notifications to set
     */
    public void setNotifications(Set<JobNotification> notifications) {
        this.notifications = notifications;
    }

    /**
     * @return the jobRegions
     */
    public Set<JobRegion> getJobRegions() {
        return jobRegions;
    }

    /**
     * @param jobRegions
     *            the jobRegions to set
     */
    public void setJobRegions(Set<JobRegion> jobRegions) {
        this.jobRegions = jobRegions;
    }

    /**
     * @return the dataFileIds
     */
    public Set<Integer> getDataFileIds() {
        return dataFileIds;
    }

    /**
     * @param dataFileIds
     *            the dataFileIds to set
     */
    public void setDataFileIds(Set<Integer> dataFileIds) {
        this.dataFileIds = dataFileIds;
    }

    /**
     * calculates total number of virtual users by summing the regions.
     * 
     * @return
     */
    public int getTotalVirtualUsers() {
        return JobUtil.calculateTotalVirtualUsers(jobRegions);
    }

    /**
     * @return the variables
     */
    public Map<String, String> getVariables() {
        if (variables == null) {
            variables = new HashMap<String, String>();
        }
        return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId())
                .toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobConfiguration)) {
            return false;
        }
        JobConfiguration o = (JobConfiguration) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }
}