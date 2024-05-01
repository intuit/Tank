/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.filters.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class ApplyFiltersRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
