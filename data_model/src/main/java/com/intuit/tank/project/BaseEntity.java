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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * BaseEntity base class for hibernate entities.
 * 
 * @author dangleton
 * 
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_CREATE = "created";
    public static final String PROPERTY_MODIFIED = "modified";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Transient
    private Date forceCreateDate;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the create
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * sets the created and modified dates to now. Called on PrePersist event
     */
    @PrePersist
    public void initializeDates() {
        if (forceCreateDate != null) {
            this.created = forceCreateDate;
        } else {
            this.created = new Date();
        }
        this.modified = new Date();
    }

    /**
     * updates the modified date to now. Called on preUpdate event.
     */
    @PreUpdate
    public void updateModified() {
        this.modified = new Date();
    }

    /**
     * @param creater
     *            the create to set
     */
    public void setCreated(Date creater) {
        this.created = creater;
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
    public String reflectionToString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Test use only. Hack to set the create date to some other time than now.
     * 
     * @param forceCreateDate
     */
    public void setForceCreateDate(Date forceCreateDate) {
        this.forceCreateDate = forceCreateDate;
    }

}
