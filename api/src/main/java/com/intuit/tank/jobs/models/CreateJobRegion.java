/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.jobs.models;

import com.intuit.tank.projects.models.Namespace;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateJobRegion {
	
	@XmlElement(name="region")
	private String region;
	
	@XmlElement(name="users")
	private String users;

	@XmlElement(name = "percentage")
	private String percentage;
	
	public CreateJobRegion() {}
	
	public CreateJobRegion(String region, String users, String percentage) {
		this.region = region;
		this.users = users;
		this.percentage = percentage;
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

	/**
	 * @return the percentage
	 */
	public String getPercentage() {
		return percentage;
	}
}
