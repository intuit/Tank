package com.intuit.tank.vm.vmManager;

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

import java.util.ArrayList;
import java.util.List;

import com.intuit.tank.vm.api.enumerated.VMProvider;

public class VMKillRequest extends VMRequest {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2848010681876648090L;

    /**
     * Constructor
     */
    public VMKillRequest() {
        super();
    }

    /**
     * Constructor
     * 
     * @param provider
     *            The virtual machine environment
     * @param region
     *            The region for the virtual machine
     * @param size
     *            The size of the virtual machine
     */
    public VMKillRequest(VMProvider provider, String instanceId) {
        super(provider);
        List<String> instances = new ArrayList<String>();
        instances.add(instanceId);
        this.setInstances(instances);
    }

    /**
     * Constructor
     * 
     * @param provider
     *            The virtual provider
     * @param instanceIds
     *            A list of instance ids
     */
    public VMKillRequest(VMProvider provider, List<String> instanceIds) {
        super(provider);
        this.setInstances(instanceIds);
    }

    /**
     * Set the list of instance ids
     * 
     * @param data
     *            A list of instance ids
     */
    public void setInstances(List<String> data) {
        this.items.put("instances", data);
    }

    /**
     * Get the list of instance ids
     * 
     * @return The list of instance ids
     */
    @SuppressWarnings("unchecked")
    public List<String> getInstances() {
        return (List<String>) this.items.get("instances");
    }
}