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
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;

public class ProjectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    
    private Date created;
    
    private Date modified;
    
    private String creator;
    
    private String name;

    private ScriptDriver scriptDriver = ScriptDriver.Tank;

    private String productName;

    private String comments;
    
    public ProjectDTO() {
    }

    public ProjectDTO(int id, Date created, Date modified, String creator, String name, ScriptDriver scriptDriver, String productName, String comments) {
    	this.id = id;
    	this.created = created;
    	this.modified = modified;
    	this.creator = creator;
    	this.name = name;
    	this.scriptDriver = scriptDriver;
    	this.productName = productName;
    	this.comments = comments;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
    
    
    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return this.created;
    }
    
    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getModified() {
        return this.modified;
    }
    
    
    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return this.creator;   
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
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name)
                .toString();
    }

 }
