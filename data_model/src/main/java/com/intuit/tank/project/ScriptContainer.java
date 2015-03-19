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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ScriptContainer
 * 
 * @author dangleton
 * 
 */
public class ScriptContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Date created;
    private Date modified;
    private String creator;
    private String name;
    private int runtime;
    private String productName;
    private String comments;
    private List<ScriptStep> steps = new ArrayList<ScriptStep>();

    public ScriptContainer() {
    }

    /**
     * 
     * @param script
     */
    public ScriptContainer(Script script) {
        this.comments = script.getComments();
        this.created = script.getCreated();
        this.creator = script.getCreator();
        this.id = script.getId();
        this.modified = script.getModified();
        this.name = script.getName();
        this.productName = script.getProductName();
        this.runtime = script.getRuntime();
        this.steps = script.getScriptSteps();
    }

    /**
     * create a Script Entity object form this Descriptor
     * 
     * @return the Script
     */
    public Script toScript() {
        Script s = new Script();
        s.setComments(comments);
        s.setCreated(created);
        s.setCreator(creator);
        s.setId(id != null ? id : 0);
        s.setModified(modified);
        s.setName(name);
        s.setProductName(productName);
        s.setRuntime(runtime);
        s.setScriptSteps(steps);
        return s;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Integer id) {
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
     * @return the runtime
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * @param runtime
     *            the runtime to set
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
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
     * @return the steps
     */
    public List<ScriptStep> getSteps() {
        return steps;
    }

    /**
     * @param steps
     *            the steps to set
     */
    public void setSteps(List<ScriptStep> steps) {
        this.steps = steps;
    }

}
