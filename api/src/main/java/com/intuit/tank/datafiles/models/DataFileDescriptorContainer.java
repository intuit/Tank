/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.datafiles.models;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "dataFiles"
})
public class DataFileDescriptorContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Singular(ignoreNullCollections = true)
    @XmlElementWrapper(name = "data-files", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "data-file", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<DataFileDescriptor> dataFiles;

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
