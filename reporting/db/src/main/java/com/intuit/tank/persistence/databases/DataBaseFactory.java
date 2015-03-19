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
import com.intuit.tank.vm.settings.VmManagerConfig;

/**
 * DataBaseFactory
 * 
 * @author dangleton
 * 
 */
public class DataBaseFactory {
    
    public static final VmManagerConfig config = new TankConfig().getVmManagerConfig();
    
    private static IDatabase provider;
    private static String providerClass;

    public static final IDatabase getDatabase() {
        if (provider == null || !provider.getClass().getName().equals(providerClass)) {
            initProvider();
        }
        return provider;
    }

    private static synchronized void initProvider() {
        String resultsProvider = new TankConfig().getVmManagerConfig().getResultsProvider();
        try {
            provider = (IDatabase) Class.forName(resultsProvider).newInstance();
            providerClass = provider.getClass().getName();
        } catch (Exception e) {
            provider = new AmazonDynamoDatabaseDocApi();
            providerClass = provider.getClass().getName();
        }
    }
}
