/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;


import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;

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

    private static String getRelativeDataUrl(int id) {
        String url = config.getControllerBase() + "/api/v2/datafiles/content?id={id}";
        return url.replace("{id}", Integer.toString(id));
    }

    /**
     * @param dao
     * @param to  the descriptor to convert
     * @return the datafile
     */
    public static DataFile descriptorToDataFile(DataFileDao dao, DataFileDescriptor to) {
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
