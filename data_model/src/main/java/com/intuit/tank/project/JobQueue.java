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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Index;

/**
 * TestInstance
 * 
 * @author dangleton
 * 
 */
@Entity
@Table(name = "job_queue")
public class JobQueue extends BaseEntity {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_PROJECT_ID = "projectId";
    public static final String PROPERTY_JOBS = "jobs";

    @Column(name = "project_id")
    @Index(name = "IDX_PROJ_ID")
    private int projectId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "test_instance_jobs", joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id"))
    private Set<JobInstance> jobs = new HashSet<JobInstance>();

    /**
     * 
     */
    public JobQueue() {
        super();
    }

    /**
     * @param projectId
     */
    public JobQueue(int projectId) {
        super();
        this.projectId = projectId;
    }

    /**
     * @return the projectId
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * @param projectId
     *            the projectId to set
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the jobs
     */
    public Set<JobInstance> getJobs() {
        return jobs;
    }

    /**
     * @param jobs
     *            the jobs to set
     */
    protected void setJobs(Set<JobInstance> jobs) {
        this.jobs = jobs;
    }

    /**
     * @param jobs
     *            the jobs to set
     */
    public void addJob(JobInstance job) {
        this.jobs.add(job);
        setModified(new Date());
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
        if (!(obj instanceof JobQueue)) {
            return false;
        }
        JobQueue o = (JobQueue) obj;
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
