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

import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * InstanceDescription
 * 
 * @author dangleton
 * 
 */
public class InstanceDescriptionDefaults {

    private HierarchicalConfiguration config;
    private HierarchicalConfiguration defaultInstance;

    public InstanceDescriptionDefaults(HierarchicalConfiguration config, HierarchicalConfiguration defaultInstance) {
        this.config = config;
        this.defaultInstance = defaultInstance;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return get("location");
    }

    /**
     * @return the securityGroup
     */
    public String getSecurityGroup() {
        return get("security-group");
    }

    /**
     * @return the securityGroup
     */
    public String getSubnetId() {
        return get("vpc-subnet");
    }
    /**
     * @return the securityGroup
     */
    public List<String> getSecurityGroupIds() {
        return getList("security-group-ids");
    }
    
    /**
     * @return the securityGroup
     */
    public String getIamRole() {
        return get("iam-role");
    }

    /**
     * @return the securityGroup
     */
    public String getZone() {
        return get("zone");
    }

    /**
     * @return the keypair
     */
    public String getKeypair() {
        return get("keypair");
    }
    
    /**
     * @return the VPC
     */
    public Boolean isVPC() {
        return Boolean.valueOf(get("vpc"));
    }

    String get(String key) {
        return config.getString(key, defaultInstance != null ? defaultInstance.getString(key) : null);
    }
    
    @SuppressWarnings("unchecked")
	List<String> getList(String key) {
        return config.getList(key, defaultInstance != null ? defaultInstance.getList(key) : null);
    }

}
