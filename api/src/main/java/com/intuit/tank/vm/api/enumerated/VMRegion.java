package com.intuit.tank.vm.api.enumerated;

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
 * represents a geographic data center that the vm will run in.
 * 
 * @author dangleton
 * 
 */
public enum VMRegion {
    // East("us-east-1", "US East (Northern Virginia)", "ec2.us-east-1.amazonaws.com"),//legacy
    // West("us-west-1", "US West (Northern California)", "ec2.us-west-1.amazonaws.com"),//legacy
    US_EAST("us-east-1", "US East (Northern Virginia)", "ec2.us-east-1.amazonaws.com"),
    US_WEST_1("us-west-1", "US West (Northern California)", "ec2.us-west-1.amazonaws.com"),
    US_WEST_2("us-west-2", "US West (Oregon)", "ec2.us-west-2.amazonaws.com"),
    EUROPE("eu-west-1", "EU (Ireland)", "ec2.eu-west-1.amazonaws.com"),
    ASIA_1("ap-southeast-1", "Asia Pacific (Singapore)", "ec2.ap-southeast-1.amazonaws.com"),
    ASIA_2("ap-southeast-2", "Asia Pacific (Sydney)", "ec2.ap-southeast-2.amazonaws.com"),
    ASIA_3("ap-northeast-1", "Asia Pacific (Tokyo)", "ec2.ap-northeast-1.amazonaws.com"),
    SOUTH_AMERICA("sa-east-1", "South America (Sao Paulo)", "ec2.sa-east-1.amazonaws.com");

    private String description;
    private String endpoint;
    private String region;

    /**
     * 
     * @param region
     * @param desc
     * @param endpoint
     */
    private VMRegion(String region, String desc, String endpoint) {
        this.description = desc;
        this.endpoint = endpoint;
        this.region = region;
    }

    /**
     * 
     * @param zone
     * @return
     */
    public static VMRegion getRegionFromZone(String zone) {
        for (VMRegion vmr : VMRegion.values()) {
            if (zone.toLowerCase().startsWith(vmr.region.toLowerCase())) {
                return vmr;
            }
        }
        return VMRegion.US_EAST;
    }

    /**
     * the human description of the region
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @{inheritDoc
     */
    public String toString() {
        return description;
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

}
