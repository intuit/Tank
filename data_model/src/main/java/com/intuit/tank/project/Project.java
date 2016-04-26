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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.intuit.tank.vm.api.enumerated.ScriptDriver;

/**
 * 
 * Project top level Object in data model. All objects are eventually traversable from their project.
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "project")
public class Project extends OwnableEntity {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_SCRIPT_DRIVER = "scriptDriver";
    public static final String PROPERTY_PRODUCT_NAME = "productName";
    public static final String PROPERTY_COMMENTS = "comments";
    public static final String PROPERTY_WORKLOADS = "workloads";

    @Column(name = "name", unique = true, nullable = false)
    @NotEmpty
    @Length(max = 255)
    private String name;

    @Column(name = "script_driver", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ScriptDriver scriptDriver = ScriptDriver.Tank;

    @Column(name = "product_name")
    @Length(max = 255)
    private String productName;

    @Column(name = "comments", length = 1024)
    @Length(max = 1024)
    private String comments;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @OrderColumn(name = "position")
    @AuditMappedBy(mappedBy = "project", positionMappedBy = "position")
    private List<Workload> workloads = new ArrayList<Workload>();

    @Transient
    private String workloadNames;

    public Project() {
    }

    public void setWorkloads(List<Workload> workloads) {
        this.workloads = workloads;
    }

    public List<Workload> getWorkloads() {
        return this.workloads;
    }

    public void addWorkload(Workload userType) {
        userType.setProject(this);
        this.workloads.add(userType);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setScriptDriver(ScriptDriver driver) {
        this.scriptDriver = driver;
    }

    public ScriptDriver getScriptDriver() {
        return this.scriptDriver;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getComments() {
    	if(comments.isEmpty())
    		return "Enter Comment";
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the workloadNames
     */
    public String getWorkloadNames() {
        return workloadNames;
    }

    /**
     * @param workloadNames
     *            the workloadNames to set
     */
    public void setWorkloadNames(String workloadNames) {
        this.workloadNames = workloadNames;
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
        if (!(obj instanceof Project)) {
            return false;
        }
        Project o = (Project) obj;
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

    public static Builder builderFrom(Project p) {
        return new Builder(p);
    }

    public static class Builder extends ProjectBuilderBase<Builder> {

        private Builder(Project p) {
            super(p);
        }

        private Builder() {
            super(new Project());
        }

        public Project build() {
            return getInstance();
        }
    }

    static class ProjectBuilderBase<GeneratorT extends ProjectBuilderBase<GeneratorT>> {
        private Project instance;

        protected ProjectBuilderBase(Project aInstance) {
            instance = aInstance;
        }

        protected Project getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT workloads(List<Workload> aValue) {
            instance.setWorkloads(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addWorkload(Workload aValue) {
            if (instance.getWorkloads() == null) {
                instance.setWorkloads(new ArrayList<Workload>());
            }

            ((ArrayList<Workload>) instance.getWorkloads()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT scriptDriver(ScriptDriver aValue) {
            instance.setScriptDriver(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT productName(String aValue) {
            instance.setProductName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT comments(String aValue) {
            instance.setComments(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT creator(String aValue) {
            instance.setCreator(aValue);

            return (GeneratorT) this;
        }
    }

}
