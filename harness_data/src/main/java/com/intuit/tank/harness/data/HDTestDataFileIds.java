/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.intuit.tank.vm.agent.messages.Namespace;

/**
 * HDTestDataFileIds
 * 
 * @author kmcgoldrick
 * 
 */
@XmlType(name = "dataFileIds", propOrder = { "allowOverride", "dataFileIds" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class HDTestDataFileIds {

    @XmlAttribute
    private boolean allowOverride;


    @XmlElementWrapper(name = "data-file-ids", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "data-file-id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<Integer> dataFileIds = new ArrayList<Integer>();

    /**
     * 
     */
    public HDTestDataFileIds() {
        super();
    }

    /**
     * @param allowOverride
     */
    public HDTestDataFileIds(boolean allowOverride) {
        super();
        this.allowOverride = allowOverride;
    }

    /**
     * @param allowOverride
     */
    public HDTestDataFileIds(boolean allowOverride, Set<Integer> ids) {
        super();
        this.allowOverride = allowOverride;
        List<Integer> lids = new ArrayList<Integer>();
        lids.addAll(ids);
        setDataFiles(lids);
    }

    /**
     * @return the allowOverride
     */
    public boolean isAllowOverride() {
        return allowOverride;
    }

    /**
     * @param allowOverride
     *            the allowOverride to set
     */
    public void setAllowOverride(boolean allowOverride) {
        this.allowOverride = allowOverride;
    }

    /**
     * @return the dataFiles
     */
    public List<Integer> getDataFileIds() {
        return dataFileIds;
    }
    

    /**
     * @param dataFiles
     *            the dataFiles to set
     */
    public void setDataFiles(List<Integer> dataFileIds) {
        this.dataFileIds = dataFileIds;
    }
}
