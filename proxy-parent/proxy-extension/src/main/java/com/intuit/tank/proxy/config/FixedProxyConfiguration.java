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

import java.util.HashSet;
import java.util.Set;

/**
 * FixedProxyConfiguration
 * 
 * @author dangleton
 * 
 */
public class FixedProxyConfiguration implements ProxyConfiguration {

    private int port = 8888;
    private String outputFile = "recordedOutput.xml";
    private String certificateAuthorityPath = "auto_generated_ca.p12";
    private Set<ConfigInclusionExclusionRule> exclusions = new HashSet<ConfigInclusionExclusionRule>();
    private Set<ConfigInclusionExclusionRule> inclusions = new HashSet<ConfigInclusionExclusionRule>();

    private Set<ConfigInclusionExclusionRule> bodyInclusions = new HashSet<ConfigInclusionExclusionRule>();
    private Set<ConfigInclusionExclusionRule> bodyExclusions = new HashSet<ConfigInclusionExclusionRule>();

    /**
     * @param port
     * @param outputFile
     */
    public FixedProxyConfiguration(int port, String outputFile) {
        super();
        this.port = port;
        this.outputFile = outputFile;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isFollowRedirects() {
        return true;
    }
    
    @Override
    public String getCertificateAuthorityPath() {
    	return certificateAuthorityPath;
    }
    

    /**
     * @{inheritDoc
     */
    @Override
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Set<ConfigInclusionExclusionRule> getExclusions() {
        return exclusions;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Set<ConfigInclusionExclusionRule> getInclusions() {
        return inclusions;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Set<ConfigInclusionExclusionRule> getBodyInclusions() {
        return bodyInclusions;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Set<ConfigInclusionExclusionRule> getBodyExclusions() {
        return bodyExclusions;
    }

    /**
     * @param exclusions
     *            the exclusions to set
     */
    public void addExclusions(ConfigInclusionExclusionRule exclusion) {
        this.exclusions.add(exclusion);
    }

    /**
     * @param inclusions
     *            the inclusions to set
     */
    public void addInclusions(ConfigInclusionExclusionRule inclusion) {
        this.inclusions.add(inclusion);
    }

    /**
     * @param bodyInclusions
     *            the bodyInclusions to set
     */
    public void addBodyInclusion(ConfigInclusionExclusionRule bodyInclusion) {
        this.bodyInclusions.add(bodyInclusion);
    }

    /**
     * @param bodyExclusions
     *            the bodyExclusions to set
     */
    public void addBodyExclusion(ConfigInclusionExclusionRule bodyExclusion) {
        this.bodyExclusions.add(bodyExclusion);
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param outputFile
     *            the outputFile to set
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
    
    /**
     * @param certificateAuthorityPath
     * 				the path to the certificate authority
     */
    public void setCertificateAuthorityPath(String certificateAuthorityPath) {
    	this.certificateAuthorityPath = certificateAuthorityPath;
    }

    /**
     * @param exclusions
     *            the exclusions to set
     */
    public void setExclusions(Set<ConfigInclusionExclusionRule> exclusions) {
        this.exclusions = exclusions;
    }

    /**
     * @param inclusions
     *            the inclusions to set
     */
    public void setInclusions(Set<ConfigInclusionExclusionRule> inclusions) {
        this.inclusions = inclusions;
    }

    /**
     * @param bodyInclusions
     *            the bodyInclusions to set
     */
    public void setBodyInclusions(Set<ConfigInclusionExclusionRule> bodyInclusions) {
        this.bodyInclusions = bodyInclusions;
    }

    /**
     * @param bodyExclusions
     *            the bodyExclusions to set
     */
    public void setBodyExclusions(Set<ConfigInclusionExclusionRule> bodyExclusions) {
        this.bodyExclusions = bodyExclusions;
    }

}
