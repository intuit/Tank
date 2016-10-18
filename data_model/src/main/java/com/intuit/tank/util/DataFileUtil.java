/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * DataFileUtil
 * 
 * @author dangleton
 * 
 */
public final class DataFileUtil {

    private static final Logger LOG = LogManager.getLogger(DataFileUtil.class);

    protected static final long MAX_AGE = 1000 * 60 * 60 * 24; // one day

    /**
     * 
     */
    private DataFileUtil() {
    }

    /**
     * @param id
     * @param version
     * @param numLines
     * @param offSet
     * @return
     */
    public static String getDataFileServiceUrl(int id, int version, int offSet, int numLines) {
        String baseUrl = new TankConfig().getControllerBase();
        return baseUrl + "/rest/v1/datafile-service/datafile/" + Integer.toString(id)
                + "/version/" + Integer.toString(version) + "?offset=" + offSet + "&num-lines=" + numLines;
    }

    /**
     * finds out how many lines are in the specified file.
     * 
     * @param dataFile
     *            the file to read
     * @return the number of lines.
     */
    public static int getNumLines(DataFile dataFile) {
        FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getDataFileStorageDir(), false);
        FileData fd = DataFileUtil.getFileData(dataFile);
        int ret = 0;
        if (fileStorage.exists(fd)) {
            try {
                for (LineIterator iterator = IOUtils.lineIterator(fileStorage.readFileData(fd), "utf-8"); iterator.hasNext(); iterator.nextLine()) {
                    ret++;
                }

            } catch (IOException e) {
                LOG.error("Error reading file: " + e, e);
            }
        }
        return ret;
    }

    public static FileData getFileData(DataFile df) {
        return new FileData(Integer.toString(df.getId()), df.getFileName());
    }

}
