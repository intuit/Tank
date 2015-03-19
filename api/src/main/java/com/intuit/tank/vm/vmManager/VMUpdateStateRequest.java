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

import com.intuit.tank.vm.api.enumerated.VMProvider;

public class VMUpdateStateRequest extends VMRequest {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6842160300874741595L;

    /**
     * Constructor
     */
    public VMUpdateStateRequest() {
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
    public VMUpdateStateRequest(VMProvider provider, String instanceId) {
        super(provider);
        String[] instances = new String[] { instanceId };
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
    public VMUpdateStateRequest(VMProvider provider, String... instanceIds) {
        super(provider);
        this.setInstances(instanceIds);
    }

    /**
     * Set the array of instance ids
     * 
     * @param instances
     *            The instance ids to update
     */
    public void setInstances(String... instances) {
        this.items.put("instances", instances);
    }

    /**
     * Get the array of instance ids
     * 
     * @return The array of instance ids
     */
    public String[] getInstances() {
        return (String[]) this.items.get("instances");
    }
}