/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
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
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 * ReportingConfig
 * 
 * @author dangleton
 * 
 */
public class ReportingConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportingConfig.class);

    private static final String KEY_PROVIDER = "provider";
    private static final String KEY_CLASSNAME = "@className";
    private static final String KEY_CONFIG = "provider/config";
    private static final String DEFAULT_PROVIDER = "com.intuit.tank.reporting.db.DatabaseResultsReporter";

    private HierarchicalConfiguration config;

    public ReportingConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
    }

    /**
     * @return the products
     */
    public String getProviderClass() {
        String ret = DEFAULT_PROVIDER;
        if (config != null) {
            try {
                SubnodeConfiguration configurationAt = config.configurationAt(KEY_PROVIDER);
                String string = configurationAt.getString(KEY_CLASSNAME);
                if (StringUtils.isNotBlank(string)) {
                    ret = string;
                } else {
                    LOG.warn("Provider not configured. Using default of " + ret);
                }
            } catch (Exception e) {
                LOG.warn("Provider specified more than once. Using default of " + ret);
            }
        }
        return ret;
    }
    /**
     * @return the products
     */
    public HierarchicalConfiguration getProviderConfig() {
        HierarchicalConfiguration ret = null;
        if (config != null) {
            try {
                ret = config.configurationAt(KEY_CONFIG);
            } catch (Exception e) {
                LOG.error("Provider config specifed more than once.");
            }
        }
        return ret;
    }

}
