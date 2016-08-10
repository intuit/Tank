/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.config;

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
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * ProxyConfiguration
 * 
 * @author dangleton
 * 
 */
public class CommonsProxyConfiguration implements ProxyConfiguration {

    private static final Logger LOG = LogManager.getLogger(CommonsProxyConfiguration.class);

    public static final String DEFAULT_CONFIG = "recording-proxy-config-default.xml";
    public static final String SUGGESTED_CONFIG_NAME = "recording-proxy-config.xml";

    private String configPath = "recording-proxy-config.xml";    
    private XMLConfiguration config;
    private FileChangedReloadingStrategy reloadingStrategy;

    private Set<ConfigInclusionExclusionRule> exclusions = new HashSet<ConfigInclusionExclusionRule>();
    private Set<ConfigInclusionExclusionRule> inclusions = new HashSet<ConfigInclusionExclusionRule>();

    private Set<ConfigInclusionExclusionRule> bodyInclusions = new HashSet<ConfigInclusionExclusionRule>();
    private Set<ConfigInclusionExclusionRule> bodyExclusions = new HashSet<ConfigInclusionExclusionRule>();

    /**
     * test constructor
     * 
     */
    public CommonsProxyConfiguration(String configPath) {
        this.configPath = configPath != null ? configPath : DEFAULT_CONFIG;
        readConfig();
    }

    /**
     * 
     */
    public CommonsProxyConfiguration() {
        this(null);
    }

    /**
     * @{inheritDoc
     */
    public int getPort() {
        return config.getInt("proxy-port", 8888);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isFollowRedirects() {
        return config.getBoolean("follow-redirects", true);
    }

    /**
     * @{inheritDoc
     */
    public String getOutputFile() {
        return config.getString("output-file", new File("recordedOutput.xml").getAbsolutePath());
    }
    
    /**
     * @{inheritDoc
     */
    @Override
    public String getCertificateAuthorityPath() {
    	return config.getString("certificate-authority-path", new File("auto_generated_ca.p12").getAbsolutePath());
    }

    /**
     * @{inheritDoc
     */
    public Set<ConfigInclusionExclusionRule> getExclusions() {
        if (needsReload()) {
            readConfig();
        }
        return exclusions;
    }

    /**
     * @{inheritDoc
     */
    public Set<ConfigInclusionExclusionRule> getInclusions() {
        if (needsReload()) {
            readConfig();
        }
        return inclusions;
    }

    /**
     * @{inheritDoc
     */
    public Set<ConfigInclusionExclusionRule> getBodyInclusions() {
        if (needsReload()) {
            readConfig();
        }
        return bodyInclusions;
    }

    /**
     * @{inheritDoc
     */
    public Set<ConfigInclusionExclusionRule> getBodyExclusions() {
        if (needsReload()) {
            readConfig();
        }
        return bodyExclusions;
    }

    /**
     * Constructor pulls file out of the jar or reads from disk and sets up refresh policy.
     * 
     * @param expressionEngine
     *            the expression engine to use. Null results in default expression engine
     */
    private void readConfig() {
        try {
            XPathExpressionEngine expressionEngine = new XPathExpressionEngine();
            if (reloadingStrategy == null) {
                reloadingStrategy = new FileChangedReloadingStrategy();
                reloadingStrategy.setRefreshDelay(0);
            }

            File configFile = new File(configPath);
            System.out.println(configFile.getAbsolutePath());
            if (configFile.exists() && configFile.isFile()) {
                try {
                    config = new XMLConfiguration(configFile);
                } catch (Exception e) {
                    LOG.error("Error parsing configFile " + configFile.getAbsolutePath() + ": " + e, e);
                }
            }
            if (config == null) {
                // Load a default from the classpath:
                LOG.info("Reading default configuration " + DEFAULT_CONFIG + " from classpath...");
                // Note: we don't let new XMLConfiguration() lookup the resource
                // url directly because it may not be able to find the desired
                // classloader to load the URL from.
                URL configResourceUrl = this.getClass().getClassLoader().getResource(DEFAULT_CONFIG);
                if (configResourceUrl == null) {
                    throw new RuntimeException("unable to load resource: " + configPath);
                }

                config = new XMLConfiguration(configResourceUrl);
                // // Copy over a default configuration since none exists:
                // // Ensure data dir location exists:
                // configFile = new File(DEFAULT_CONFIG);
                // // if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
                // // throw new RuntimeException("could not create directories.");
                // // }
                // LOG.info("Saving default configuration to file " + configFile.getAbsolutePath());
                // config.save(configFile);
            }

            if (expressionEngine != null) {
                config.setExpressionEngine(expressionEngine);
            }

            // reload at most once per thirty seconds on configuration queries.
            config.setReloadingStrategy(reloadingStrategy);
            initConfig();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig(File configFile) throws ConfigurationException {
        config.save(configFile);
    }

    /**
     * 
     */
    private void initConfig() {
        exclusions = parseInclusionExclusions("exclusions");
        inclusions = parseInclusionExclusions("inclusions");
        bodyExclusions = parseInclusionExclusions("body-exclusions");
        bodyInclusions = parseInclusionExclusions("body-inclusions");
    }

    /**
     * @param string
     * @return
     */
    private Set<ConfigInclusionExclusionRule> parseInclusionExclusions(String key) {
        Set<ConfigInclusionExclusionRule> ret = new HashSet<ConfigInclusionExclusionRule>();
        SubnodeConfiguration groupConfig = config.configurationAt(key);
        if (groupConfig != null) {
            @SuppressWarnings("unchecked") List<HierarchicalConfiguration> list = groupConfig.configurationsAt("rule");
            for (HierarchicalConfiguration c : list) {
                ret.add(new ConfigInclusionExclusionRule(getTransactionPart(c), c.getString("@header", "all"),
                        getMatchType(c), c.getString("")));
            }
        }
        return ret;
    }

    /**
     * @param c
     * @return
     */
    private MatchType getMatchType(HierarchicalConfiguration c) {
        MatchType ret = MatchType.contains;
        String string = c.getString("@match");
        if (string != null) {
            try {
                ret = MatchType.valueOf(string);
            } catch (Exception e) {
                LOG.warn("Illegal MatchType value: " + string);
            }
        }
        return ret;
    }

    /**
     * @param c
     * @return
     */
    private TransactionPart getTransactionPart(HierarchicalConfiguration c) {
        TransactionPart ret = TransactionPart.both;
        String string = c.getString("@check");
        if (string != null) {
            try {
                ret = TransactionPart.valueOf(string);
            } catch (Exception e) {
                LOG.warn("Illegal TransactionPart value: " + string);
            }
        }
        return ret;
    }

    /**
     * checks if the config needs to be reloaded.
     */
    public boolean needsReload() {
        return (config == null || config.getReloadingStrategy().reloadingRequired());
    }

    public static boolean save(int port, boolean followRedirect, String outputFile,
            Set<ConfigInclusionExclusionRule> inclusions,
            Set<ConfigInclusionExclusionRule> exclusions,
            Set<ConfigInclusionExclusionRule> bodyInclusions,
            Set<ConfigInclusionExclusionRule> bodyExclusions,
            String fileName) {

        ConfigurationNode node = getConfNode("recording-proxy-config", "", false);
        ConfigurationNode portNode = getConfNode("proxy-port", String.valueOf(port), false);
        ConfigurationNode followRedirectNode = getConfNode("follow-redirects", Boolean.toString(followRedirect), false);
        ConfigurationNode outputFileNode = getConfNode("output-file", outputFile, false);
        ConfigurationNode inclusionsNode = getConfNode("inclusions", "", false);
        ConfigurationNode exclusionsNode = getConfNode("exclusions", "", false);
        ConfigurationNode bodyInclusionsNode = getConfNode("body-inclusions", "", false);
        ConfigurationNode bodyExclusionsNode = getConfNode("body-exclusions", "", false);

        updateRuleParentNode(inclusions, inclusionsNode);
        updateRuleParentNode(exclusions, exclusionsNode);
        updateRuleParentNode(bodyInclusions, bodyInclusionsNode);
        updateRuleParentNode(bodyExclusions, bodyExclusionsNode);

        node.addChild(portNode);
        node.addChild(followRedirectNode);
        node.addChild(outputFileNode);
        node.addChild(inclusionsNode);
        node.addChild(exclusionsNode);
        node.addChild(bodyInclusionsNode);
        node.addChild(bodyExclusionsNode);

        HierarchicalConfiguration hc = new HierarchicalConfiguration();
        hc.setRootNode(node);
        XMLConfiguration xmlConfiguration = new XMLConfiguration(hc);
        xmlConfiguration.setRootNode(node);

        try {

            xmlConfiguration.save(new File(fileName));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return true;

    }

    public static void updateRuleParentNode(Set<ConfigInclusionExclusionRule> rule, ConfigurationNode parentNode) {
        for (ConfigInclusionExclusionRule configInclusionExclusionRule : rule) {
            ConfigurationNode ruleNode = getConfNode("rule", configInclusionExclusionRule.getValue(), false);
            ConfigurationNode checkNode = getConfNode("check", configInclusionExclusionRule.getTransactionPart()
                    .toString(), true);
            ConfigurationNode matchNode = getConfNode("match", configInclusionExclusionRule.getMatch().toString(), true);
            ConfigurationNode headerNode = getConfNode("header", configInclusionExclusionRule.getHeader(), true);

            ruleNode.addAttribute(checkNode);
            ruleNode.addAttribute(matchNode);
            ruleNode.addAttribute(headerNode);

            parentNode.addChild(ruleNode);
        }
    }

    public static ConfigurationNode getConfNode(String name, String value, boolean attributeFlag) {
        ConfigurationNode confNode = new HierarchicalConfiguration.Node(name, value);
        confNode.setAttribute(attributeFlag);
        return confNode;
    }

}
