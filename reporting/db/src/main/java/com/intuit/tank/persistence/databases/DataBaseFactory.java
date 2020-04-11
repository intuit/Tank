/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DataBaseFactory
 * 
 * @author dangleton
 * 
 */
public class DataBaseFactory {
    private static final Logger LOG = LogManager.getLogger(DataBaseFactory.class);
	private static final String resultsProvider = new TankConfig().getVmManagerConfig().getResultsProvider();

    public static IDatabase getDatabase() {
            return initProvider();
    }

    private static IDatabase initProvider() {
        try {
            return (IDatabase) Class.forName(resultsProvider).newInstance();
        } catch (Exception e) {
            LOG.error("Unable to create DataSource Provider : " + resultsProvider + ", update settings.xml", e);
        }
        return new CloudWatchDataSource();
    }
}
