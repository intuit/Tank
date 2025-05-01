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

import java.util.Set;

/**
 * ProxyConfiguration
 * 
 * @author dangleton
 * 
 */
public interface ProxyConfiguration {

    /**
     * @return the port
     */
    public abstract int getPort();

    public abstract String getProxyHost();

    /**
     * @return the port
     */
    public abstract boolean isFollowRedirects();
    
    /**
     * @return the path to the certificate authority (if provided).
     */
    public abstract String getCertificateAuthorityPath();
    
    /**
     * @return the outputFile
     */
    public abstract String getOutputFile();

    /**
     * @return the exclusions
     */
    public abstract Set<ConfigInclusionExclusionRule> getExclusions();

    /**
     * @return the inclusions
     */
    public abstract Set<ConfigInclusionExclusionRule> getInclusions();

    /**
     * @return the bodyInclusions
     */
    public abstract Set<ConfigInclusionExclusionRule> getBodyInclusions();

    /**
     * @return the bodyExclusions
     */
    public abstract Set<ConfigInclusionExclusionRule> getBodyExclusions();

}