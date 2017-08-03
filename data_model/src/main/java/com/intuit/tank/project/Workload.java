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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;

/**
 * 
 * Workload represents a group of scripts to run. can be many to one for a project but usually is one to one.
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "workload")
public class Workload extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "project_id", updatable = false, insertable = false)
    private Project project;

    @Column(name = "position", insertable = false, updatable = false)
    private Integer position;

    @Column(name = "name", nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;// name

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "workload_id", referencedColumnName = "id")
    @OrderColumn(name = "position")
    @AuditMappedBy(mappedBy = "workload", positionMappedBy = "position")
    private List<TestPlan> testPlans = new ArrayList<TestPlan>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_configuration_id")
    private JobConfiguration jobConfiguration = new JobConfiguration();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setTestPlan(List<TestPlan> plans) {
        this.testPlans = plans;
    }

    public List<TestPlan> getTestPlans() {
        return this.testPlans;
    }

    public void addTestPlan(TestPlan plan) {
        addTestGroupAt(plan, -1);
    }

    public void addTestGroupAt(TestPlan testPlan, int index) {

        testPlan.setWorkload(this);
        if (index >= 0 && index < testPlans.size()) {
            this.testPlans.add(index, testPlan);
        } else {
            this.testPlans.add(testPlan);
        }
    }

    protected void setProject(Project myProject) {
        this.project = myProject;
    }

    public void setParent(Project myProject) {
        this.project = myProject;
    }

    public Project getProject() {
        return this.project;
    }

    /**
     * @return the jobConfiguration
     */
    public JobConfiguration getJobConfiguration() {
        return jobConfiguration;
    }

    /**
     * @param jobConfiguration
     *            the jobConfiguration to set
     */
    public void setJobConfiguration(JobConfiguration jobConfiguration) {
        this.jobConfiguration = jobConfiguration;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name)
                .toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Workload)) {
            return false;
        }
        Workload o = (Workload) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(Workload w) {
        return new Builder(w);
    }

    /**
     * Fluent Builder for Workloads Workload Builder
     * 
     * @author dangleton
     * 
     */
    public static class Builder extends WorkloadBuilderBase<Builder> {

        private Builder() {
            super(new Workload());
        }

        private Builder(Workload w) {
            super(w);
        }

        public Workload build() {
            return getInstance();
        }
    }

    private static class WorkloadBuilderBase<GeneratorT extends WorkloadBuilderBase<GeneratorT>> {
        private Workload instance;

        protected WorkloadBuilderBase(Workload aInstance) {
            instance = aInstance;
        }

        protected Workload getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT tetPlans(List<TestPlan> aValue) {
            instance.setTestPlan(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addTestPlans(TestPlan aValue) {
            if (instance.getTestPlans() == null) {
                instance.setTestPlan(new ArrayList<TestPlan>());
            }

            ((ArrayList<TestPlan>) instance.getTestPlans()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT project(Project aValue) {
            instance.setProject(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT jobConfiguration(JobConfiguration jobConfiguration) {
            instance.setJobConfiguration(jobConfiguration);

            return (GeneratorT) this;
        }

    }

}