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
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

/**
 * 
 * Project top level Object in data model. All objects are eventually traversable from their project.
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "timing_result",
        indexes = { @Index(name = "IDX_CREATOR", columnList = ColumnPreferences.PROPERTY_CREATOR)})
public class TimingResult extends OwnableEntity {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "name";

    @Column(name = "name", nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "job_id", nullable = false)
    @NotNull
    @Size(max = 255)
    private String jobId;

    public TimingResult() {
    }

}
