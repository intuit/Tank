/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.datafile.util;

/*
 * #%L
 * Datafile Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.service.api.v1.DataFileService;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * DataFileServiceUtil
 * 
 * @author dangleton
 * 
 */
public final class DataFileServiceUtil {

    private static TankConfig config = new TankConfig();

    private DataFileServiceUtil() {
    }

    /**
     * 
     * @param dataFile
     *            the datafile to convert
     * @return the Descriptor
     */
    public static DataFileDescriptor dataFileToDescriptor(DataFile dataFile) {
        DataFileDescriptor ret = new DataFileDescriptor();
        ret.setId(dataFile.getId());
        ret.setCreated(dataFile.getCreated());
        ret.setModified(dataFile.getModified());
        ret.setCreator(dataFile.getCreator());
        ret.setName(dataFile.getPath());
        ret.setDataUrl(getRelativeDataUrl(dataFile.getId()));
        ret.setComments(dataFile.getComments());
        return ret;
    }

    /**
     * @param dataFile
     * @return
     */
    private static String getRelativeDataUrl(int id) {
        String url = config.getControllerBase() + TankConstants.REST_SERVICE_CONTEXT
                + DataFileService.SERVICE_RELATIVE_PATH + DataFileService.METHOD_GET_DATA_FILE_DATA;
        return url.replace("{id}", Integer.toString(id));
    }

    /**
     * @param dataFile
     * @return
     */
    public static String getDownloadUrl(int id) {
        String url = config.getControllerBase() + TankConstants.REST_SERVICE_CONTEXT
                + DataFileService.SERVICE_RELATIVE_PATH + DataFileService.METHOD_GET_DATA_FILE_DATA_DOWNLOAD;
        return url.replace("{id}", Integer.toString(id));
    }

    /**
     * 
     * @param to
     *            the descriptor to convert
     * @return the datafile
     */
    public static DataFile descriptorToDataFile(DataFileDescriptor to) {
        DataFile result = new DataFile();
        result.setComments(to.getComments());
        result.setCreated(to.getCreated());
        result.setCreator(to.getCreator());
        result.setId(to.getId() != null ? to.getId() : 0);
        result.setModified(to.getModified());
        result.setPath(to.getName());
        return result;
    }
}
