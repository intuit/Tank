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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Index;

/**
 * OwnableEntity entity that can be owned
 * 
 * @author dangleton
 * 
 */
@MappedSuperclass
public abstract class OwnableEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CREATOR = "creator";

    @Column(name = "creator", nullable = false)
    @Index(name = "IDX_CREATOR")
    private String creator;

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

}
