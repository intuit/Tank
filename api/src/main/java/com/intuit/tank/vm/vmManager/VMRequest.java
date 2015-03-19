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

import java.io.Serializable;
import java.util.HashMap;

import com.intuit.tank.vm.api.enumerated.VMProvider;

public abstract class VMRequest implements Serializable {

    /**
     * 
     */
    private static final String KEY_PROVIDER = "provider";
    /**
	 * 
	 */
    private static final long serialVersionUID = -2090082179155920305L;
    protected HashMap<String, Object> items = null;

    protected VMRequest() {
        items = new HashMap<String, Object>();
    }

    protected VMRequest(VMProvider provider) {
        items = new HashMap<String, Object>();
        setProvider(provider);
    }

    /**
     * Set the virtual machine provider
     * 
     * @param data
     *            The virtual machine provider
     */
    public void setProvider(VMProvider data) {
        this.items.put(KEY_PROVIDER, data);
    }

    /**
     * Get the virtual machine provider
     * 
     * @return The virtual machine provider
     */
    public VMProvider getProvider() {
        return (VMProvider) this.items.get(KEY_PROVIDER);
    }
}
