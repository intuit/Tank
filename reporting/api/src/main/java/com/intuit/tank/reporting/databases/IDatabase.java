package com.intuit.tank.reporting.databases;

/*
 * #%L
 * Reporting API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.intuit.tank.results.TankResult;

public interface IDatabase {

    /**
     * Creates the table if needed no-op if table already exists.
     * 
     * @param tableName
     *            the name of the table this db instance is dealing with
     */
    public void createTable(@Nonnull String tableName);

    /**
     * Deletes the table from the datastore.
     * 
     * @param tableName
     *            the name of the table this db instance is dealing with
     */
    public void deleteTable(@Nonnull String tableName);

    /**
     * Deletes the table from the datastore.
     * 
     * @param tableName
     *            the name of the table this db instance is dealing with
     */
    public void deleteForJob(@Nonnull String tableName, @Nonnull String jobId, boolean asynch);

    /**
     * Test if the table exists in the datastore.
     * 
     * @param tableName
     *            the name of the table this db instance is dealing with
     * @return true if the table exists
     */
    public boolean hasTable(@Nonnull String tableName);

    /**
     * Test if the table exists in the datastore.
     * 
     * @param tableName
     *            the name of the table this db instance is dealing with
     * @return true if the table exists
     */
    public boolean hasJobData(@Nonnull String tableName, String jobId);

    /**
     * Adds the results to the datastore in batch mode.
     * 
     * @param tableName
     *            the name of the table this db instance is dealing with
     * @param results
     *            the results to add
     * @param asynch
     *            whether this method should execute asynchronously
     */
    public void addTimingResults(@Nonnull String tableName, @Nonnull List<TankResult> results, boolean asynch);

    /**
     * Gets all the domains that match the given regex.
     * 
     * @param match
     *            a regular expression to match the domain name
     * @return the list of domains that match the given regex
     */
    public Set<String> getTables(String regex);

    /**
     * 
     * @param tableName
     * @param items
     */
    public void addItems(String tableName, List<Item> items, boolean asynch);

    /**
     * Gets all the items in the range.
     * 
     * @param tableName
     *            the table to fetch items from
     * @param jobId
     *            the jobId of the items
     * @param minRange
     *            the minRange as a String in format DATE_FORMAT
     * @param maxRange
     *            the maxRange as a String in format DATE_FORMAT
     * @return
     */
    public List<Item> getItems(String tableName, String minRange, String maxRange, String instanceId, String... jobId);

    /**
     * 
     * @param tableName
     * @param jobId
     * @param nextToken
     * @param minRange
     * @param maxRange
     * @return
     */
    public PagedDatabaseResult getPagedItems(String tableName, Object nextToken, String minRange,
            String maxRange, String instanceId, String jobId);

    /**
     * 
     * @param type
     * @param jobId
     * @return
     */
    public String getDatabaseName(TankDatabaseType type, String jobId);

}
