package com.intuit.tank.vm.perfManager;

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

public class Environment implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6948829175395858814L;
    private HashMap<String, Object> items = null;

    /**
     * Constructor
     */
    public Environment() {
        this.items = new HashMap<String, Object>();
    }

    /**
     * Constructor
     * 
     * @param frontEndUrl
     *            The front end URL
     * @param backEndUrl
     *            The back end URL
     */
    public Environment(String frontEndUrl, String backEndUrl) {
        this.items = new HashMap<String, Object>();
        this.setFrontEndUrl(frontEndUrl);
        this.setBackEndUrl(backEndUrl);
    }

    /**
     * Set the front end URL
     * 
     * @param data
     *            The front end URL
     */
    public void setFrontEndUrl(String data) {
        this.items.put("frontEndUrl", data);
    }

    /**
     * Get the front end URL
     * 
     * @return The front end URL
     */
    public String getFrontEndUrl() {
        return (String) this.items.get("frontEndUrl");
    }

    /**
     * Set the back end URL
     * 
     * @param data
     *            The back end URL
     */
    public void setBackEndUrl(String data) {
        this.items.put("backEndUrl", data);
    }

    /**
     * Get the back end URL
     * 
     * @return The back end URL
     */
    public String getBackEndUrl() {
        return (String) this.items.get("backEndUrl");
    }
}
