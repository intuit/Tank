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
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;

/**
 * WatsRevisionInfo
 * 
 * @author dangleton
 * 
 */
@Entity
@Table(name = "revision_info")
@RevisionEntity(TankRevisionListener.class)
public class TankRevisionInfo extends TankDefaultRevisionEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "user_name")
    private String username;

    /**
     * 
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
