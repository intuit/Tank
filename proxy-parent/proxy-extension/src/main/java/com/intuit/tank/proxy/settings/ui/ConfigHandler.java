package com.intuit.tank.proxy.settings.ui;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.IOException;

import com.intuit.tank.proxy.config.CommonsProxyConfiguration;
import com.intuit.tank.proxy.config.ProxyConfiguration;

public class ConfigHandler {

    private String configFile;

    public ConfigHandler() {
        try {
            File file = new File(CommonsProxyConfiguration.DEFAULT_CONFIG);
            if (file.exists()) {
                configFile = file.getCanonicalPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProxyConfiguration getConfiguration() {
        return new CommonsProxyConfiguration(configFile);
    }

    /**
     * @return the configFile
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * @param configFile
     *            the configFile to set
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

}
