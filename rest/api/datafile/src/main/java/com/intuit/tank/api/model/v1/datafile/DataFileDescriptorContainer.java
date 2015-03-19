/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.datafile;

/*
 * #%L
 * Datafile Rest API
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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * ScriptStepContainer jaxb container for script steps
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "dataFiles"
})
public class DataFileDescriptorContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "data-files", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "data-file", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<DataFileDescriptor> dataFiles = new ArrayList<DataFileDescriptor>();

    public DataFileDescriptorContainer() {

    }

    public DataFileDescriptorContainer(List<DataFileDescriptor> list) {
        this.dataFiles = list;
    }

    /**
     * @return the dataFiles
     */
    public List<DataFileDescriptor> getDataFiles() {
        return dataFiles;
    }

    /**
     * @param dataFiles
     *            the dataFiles to set
     */
    public void setDataFiles(List<DataFileDescriptor> dataFiles) {
        this.dataFiles = dataFiles;
    }

}
