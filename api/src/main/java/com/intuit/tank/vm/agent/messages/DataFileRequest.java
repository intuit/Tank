package com.intuit.tank.vm.agent.messages;

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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "dataFileRequest", namespace = DataFileRequest.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataFileRequest", namespace = DataFileRequest.NAMESPACE_V1, propOrder = {
        "fileName",
        "isDefaultDataFile",
        "fileUrl"
})
public class DataFileRequest implements Serializable {

    public static final String NAMESPACE_V1 = "urn:wats/agent/v1";

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "fileName", namespace = NAMESPACE_V1, required = true, nillable = false)
    private String fileName = null;

    @XmlElement(name = "isDefaultDataFile", namespace = NAMESPACE_V1, required = true, nillable = false)
    @JsonProperty("isDefaultDataFile")
    private boolean isDefaultDataFile;

    @XmlElement(name = "fileUrl", namespace = NAMESPACE_V1, required = true, nillable = false)
    private String fileUrl;

    /**
     * 
     */
    public DataFileRequest() {
        super();
    }

    /**
     * @param fileName
     * @param isDefaultDataFile
     * @param fileUrl
     */
    public DataFileRequest(String fileName, boolean isDefaultDataFile, String fileUrl) {
        super();
        this.fileName = fileName;
        this.isDefaultDataFile = isDefaultDataFile;
        this.fileUrl = fileUrl;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the isDefaultDataFile
     */
    @JsonProperty("isDefaultDataFile")
    public boolean isDefaultDataFile() {
        return isDefaultDataFile;
    }

    /**
     * @return the fileUrl
     */
    public String getFileUrl() {
        return fileUrl;
    }

}
