/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.automation;

/*
 * #%L
 * Automation Rest Api
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.api.model.v1.automation.adapter.XmlGenericMapAdapter;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.vm.api.enumerated.Location;

/**
 * 
 * @author Kevin McGoldrick
 * 
 */

@XmlRootElement
public class ApplyFiltersRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="scriptId")
    private String scriptId;
    
    @XmlElement(name="filterIds")
    private List<Integer> filterIds = new ArrayList<Integer>();
    
    @XmlElement(name="filterGroupIds")
    private List<Integer> filterGroupIds = new ArrayList<Integer>();

    /**
     * 
     */
    public ApplyFiltersRequest() {
    }
    
    public ApplyFiltersRequest(String scriptId) {
    	this.scriptId = scriptId;
    }
   
    public ApplyFiltersRequest(String scriptId, List<Integer> filterIds, List<Integer> filterGroupIds) {
    	this.scriptId = scriptId;
    	this.filterIds = filterIds;
    	this.filterGroupIds = filterGroupIds;
    }
    
    /**
     * @return the scriptId
     */
    public String getScriptId() {
        return scriptId;
    }

    /**
     * @return the filterIds
     */
    public List<Integer> getFilterIds() {
        return filterIds;
    }

    /**
     * @return the groupFilterIds
     */
    public List<Integer> getFilterGroupIds() {
        return filterGroupIds;
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}