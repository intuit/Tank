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

import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 * Preferences
 * 
 * @author dangleton
 * 
 */
@Entity
@Table(name = "table_preferences",
        indexes = { @Index(name = "IDX_CREATOR", columnList = ColumnPreferences.PROPERTY_CREATOR)})
public class Preferences extends OwnableEntity {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "project_position")
    @JoinColumn(name = "project_table_id", referencedColumnName = "id")
    @BatchSize(size=10)
    private List<ColumnPreferences> projectTableColumns = new ArrayList<ColumnPreferences>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "scripts_position")
    @JoinColumn(name = "scripts_table_id", referencedColumnName = "id")
    @BatchSize(size=10)
    private List<ColumnPreferences> scriptsTableColumns = new ArrayList<ColumnPreferences>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "scripts_position")
    @JoinColumn(name = "script_table_id", referencedColumnName = "id")
    @BatchSize(size=10)
    private List<ColumnPreferences> scriptStepTableColumns = new ArrayList<ColumnPreferences>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "datafiles_position")
    @JoinColumn(name = "datafiles_table_id", referencedColumnName = "id")
    private List<ColumnPreferences> datafilesTableColumns = new ArrayList<ColumnPreferences>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "jobs_position")
    @JoinColumn(name = "jobs_table_id", referencedColumnName = "id")
    @BatchSize(size=10)
    private List<ColumnPreferences> jobsTableColumns = new ArrayList<ColumnPreferences>();

    /**
     * @return the projectTableColumns
     */
    public List<ColumnPreferences> getProjectTableColumns() {
        return projectTableColumns;
    }

    /**
     * @return the scriptsTableColumns
     */
    public List<ColumnPreferences> getScriptsTableColumns() {
        return scriptsTableColumns;
    }

    /**
     * @return the scriptStepTableColumns
     */
    public List<ColumnPreferences> getScriptStepTableColumns() {
        return scriptStepTableColumns;
    }

    /**
     * @return the datafilesTableColumns
     */
    public List<ColumnPreferences> getDatafilesTableColumns() {
        return datafilesTableColumns;
    }

    /**
     * @return the jobsTableColumns
     */
    public List<ColumnPreferences> getJobsTableColumns() {
        return jobsTableColumns;
    }

}
