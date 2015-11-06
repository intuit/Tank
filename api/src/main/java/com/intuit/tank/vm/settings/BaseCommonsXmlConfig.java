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

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.log4j.Logger;

/**
 * BaseCommonsConfig
 * 
 * @author dangleton
 */
public abstract class BaseCommonsXmlConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(BaseCommonsXmlConfig.class);

    protected XMLConfiguration config;
    protected File configFile;

    /**
     * Constructor pulls file out of the jar or reads from disk and sets up refresh policy.
     * 
     * @param expressionEngine
     *            the expression engine to use. Null results in default expression engine
     */
    protected void readConfig() {
        try {
            ExpressionEngine expressionEngine = new XPathExpressionEngine();
            String configPath = getConfigName();
            FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();

            File dataDirConfigFile = new File(configPath);
//            LOG.info("Reading settings from " + dataDirConfigFile.getAbsolutePath());
            if (!dataDirConfigFile.exists()) {
                // Load a default from the classpath:
                // Note: we don't let new XMLConfiguration() lookup the resource
                // url directly because it may not be able to find the desired
                // classloader to load the URL from.
                URL configResourceUrl = this.getClass().getClassLoader().getResource(configPath);
                if (configResourceUrl == null) {
                    throw new RuntimeException("unable to load resource: " + configPath);
                }

                XMLConfiguration tmpConfig = new XMLConfiguration(configResourceUrl);
                // Copy over a default configuration since none exists:
                // Ensure data dir location exists:
                if (dataDirConfigFile.getParentFile() != null && !dataDirConfigFile.getParentFile().exists()
                        && !dataDirConfigFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("could not create directories.");
                }
                tmpConfig.save(dataDirConfigFile);
                LOG.info("Saving settings file to " + dataDirConfigFile.getAbsolutePath());
            }

            if (dataDirConfigFile.exists()) {
                config = new XMLConfiguration(dataDirConfigFile);
            } else {
                // extract from jar and write to
                throw new IllegalStateException("Config file does not exist or cannot be created");
            }
            if (expressionEngine != null) {
                config.setExpressionEngine(expressionEngine);
            }
            configFile = dataDirConfigFile;
            // reload at most once per thirty seconds on configuration queries.
            config.setReloadingStrategy(reloadingStrategy);
            initConfig(config);
        } catch (ConfigurationException e) {
            LOG.error("Error reading settings file: " + e, e);
            throw new RuntimeException(e);
        }
    }

    public File getSourceConfigFile() {
        return configFile;
    }

    @SuppressWarnings("unchecked")
    public static HierarchicalConfiguration getChildConfigurationAt(HierarchicalConfiguration config, String key) {
        if (config == null) {
            return null;
        }
        List<HierarchicalConfiguration> configs = config.configurationsAt(key);
        if (configs.size() > 1) {
            LOG.warn("Child configuration with key " + key + " matches more than one node.");
        } else if (configs.size() == 0) {
            LOG.warn("Child configuration with key " + key + " has no entry in config file.");
        }
        return configs.size() != 0 ? configs.get(0) : null;
    }

    /**
     * @return the config
     */
    protected XMLConfiguration getConfig() {
        checkReload();
        return config;
    }

    /**
     * 
     * @return the name of the config to fetch. e.g. SitesConfig.xml
     */
    @Nonnull
    protected abstract String getConfigName();

    /**
     * initialize the configuration form the passed in config.
     * 
     * @param configuration
     *            the configuration to initialize from
     */
    protected abstract void initConfig(@Nonnull XMLConfiguration configuration);

    /**
     * checks if the config needs to be reloaded and calls initConfig on it. should be called if parsing of the config
     * is needed.
     */
    protected synchronized void checkReload() {
        if (config == null) {
            readConfig();
        } else if (config.getReloadingStrategy().reloadingRequired()) {
            config.reload();
            initConfig(config);
            config.getReloadingStrategy().reloadingPerformed();
        }
    }

    /**
     * checks if the config needs to be reloaded.
     */
    public boolean needsReload() {
        return (config == null || config.getReloadingStrategy().reloadingRequired());
    }

}
