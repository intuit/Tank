/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.harness;

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

/**
 * CloudMetaDataType
 * 
 * @author dangleton
 * 
 */
public enum CloudMetaDataType {
    ami_id("ami-id"),
    ami_launch_index("ami-launch-index"),
    ami_manifest_path("ami-manifest-path"),
    hostname("hostname"),
    instance_action("instance-action"),
    instance_id("instance-id"),
    instance_type("instance-type"),
    kernel_id("kernel-id"),
    local_hostname("local-hostname"),
    local_ipv4("local-ipv4"),
    mac("mac"),
    zone("placement/availability-zone"),
    public_hostname("public-hostname"),
    public_ipv4("public-ipv4"),
    ramdisk_id("ramdisk-id"),
    reservation_id("reservation-id"),
    security_groups("security-groups");

    private String key;

    /**
     * @param key
     */
    private CloudMetaDataType(String key) {
        this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

}
