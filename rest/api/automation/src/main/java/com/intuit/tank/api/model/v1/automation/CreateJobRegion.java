/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.automation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Kevin McGoldrick
 * 
 */

@XmlRootElement
public class CreateJobRegion {
	
	@XmlElement(name="region")
	private String region;
	
	@XmlElement(name="users")
	private String users;
	
	public CreateJobRegion() {}
	
	public CreateJobRegion(String region, String users) {
		this.region = region;
		this.users = users;
	}
	
    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }
    
    /**
     * @return the users
     */
    public String getUsers() {
        return users;
    }
}