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
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;

public class VMInformation implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2960315644666847545L;
    private HashMap<String, Object> items = null;

    /**
     * Constructor
     */
    public VMInformation() {
        this.items = new HashMap<String, Object>();
    }

    /**
     * Set the virtual machine provider
     * 
     * @param data
     *            The virtual machine provider
     */
    public void setProvider(VMProvider data) {
        this.items.put("provider", data);
    }

    /**
     * Get the virtual machine provider
     * 
     * @return The virtual machine provider
     */
    public VMProvider getProvider() {
        return (VMProvider) this.items.get("provider");
    }

    /**
     * Set the virtual machine public URL
     * 
     * @param data
     *            The virtual machine's public URL
     */
    public void setPublicDNS(String data) {
        this.items.put("publicDns", data);
    }

    /**
     * Get the virtual machine public URL
     * 
     * @return The virtual machine's public URL
     */
    public String getPublicDNS() {
        return (String) this.items.get("publicDns");
    }

    /**
     * Set the virtual machine private DNS
     * 
     * @param data
     *            The virtual machine's private DNS
     */
    public void setPrivateDNS(String data) {
        this.items.put("privateDns", data);
    }

    /**
     * Get the virtual machine private DNS
     * 
     * @return The virtual machine's private DNS
     */
    public String getPrivateDNS() {
        return (String) this.items.get("privateDns");
    }

    public void setLaunchTime(Calendar data) {
        this.items.put("launchTime", data);
    }

    public Calendar getLaunchTime() {
        return (Calendar) this.items.get("launchTime");
    }

    public void setState(String data) {
        this.items.put("state", data);
    }

    public String getState() {
        return (String) this.items.get("state");
    }

    public void setPlatform(String data) {
        this.items.put("platform", data);
    }

    public String getPlatform() {
        return (String) this.items.get("platform");
    }

    public void setRequestId(String data) {
        this.items.put("requestId", data);
    }

    public String getRequestId() {
        return (String) this.items.get("requestId");
    }

    public void setKeyName(String data) {
        this.items.put("keyName", data);
    }

    public String getKeyName() {
        return (String) this.items.get("keyName");
    }

    public void setInstanceId(String data) {
        this.items.put("instanceId", data);
    }

    public String getInstanceId() {
        return (String) this.items.get("instanceId");
    }

    public void setImageId(String data) {
        this.items.put("imageId", data);
    }

    public String getImageId() {
        return (String) this.items.get("imageId");
    }

    public void setSize(String size) {
        this.items.put("size", size);
    }

    public String getSize() {
        return (String) this.items.get("size");
    }

    public void setRegion(VMRegion region) {
        this.items.put("region", region);
    }

    public VMRegion getRegion() {
        return (VMRegion) this.items.get("region");
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
