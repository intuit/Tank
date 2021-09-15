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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.ScriptDriver;

/**
 * 
 * Project top level Object in data model. All objects are eventually traversable from their project.
 * 
 * @author dangleton
 * 
 */
public class ProjectDescription extends OwnableEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    private ScriptDriver scriptDriver = ScriptDriver.Tank;
    private String productName;
    private String comments;
    private String creator;
    private int id;
    private Date created;
    private Date modified;
    private List<String> workloadNames;

    public ProjectDescription() {
    }

    public ProjectDescription(Project p) {
        this.id = p.getId();
        this.modified = p.getModified();
        this.created = p.getCreated();
        this.comments = p.getComments();
        this.name = p.getName();
        this.productName = p.getProductName();
        this.scriptDriver = p.getScriptDriver();
        workloadNames = new ArrayList<String>();
        for (Workload w : p.getWorkloads()) {
            workloadNames.add(w.getName());
        }

    }

    /**
     * @return the workloadNames
     */
    public List<String> getWorkloadNames() {
        return workloadNames;
    }

    /**
     * @param workloadNames
     *            the workloadNames to set
     */
    public void setWorkloadNames(List<String> workloadNames) {
        this.workloadNames = workloadNames;
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
     * @return the scriptDriver
     */
    public ScriptDriver getScriptDriver() {
        return scriptDriver;
    }

    /**
     * @param scriptDriver
     *            the scriptDriver to set
     */
    public void setScriptDriver(ScriptDriver scriptDriver) {
        this.scriptDriver = scriptDriver;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
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
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProjectDescription)) {
            return false;
        }
        ProjectDescription o = (ProjectDescription) obj;
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
