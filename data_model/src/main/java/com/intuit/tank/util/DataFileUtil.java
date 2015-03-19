/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * DataFileUtil
 * 
 * @author dangleton
 * 
 */
public final class DataFileUtil {
    private static final String DATA_FILE_PREFIX = "DataFile-";

    private static final Logger LOG = Logger.getLogger(DataFileUtil.class);

    protected static final long MAX_AGE = 1000 * 60 * 60 * 24; // one day

    private static File tmpDir = null;

    static {
        try {
            tmpDir = File.createTempFile("tf", ".tmp").getParentFile();
        } catch (Exception e) {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
        }
    }

    /**
     * 
     */
    private DataFileUtil() {
    }

    /**
     * Gets the File representing the datafile contents.
     * 
     * @param df
     *            the datafile to get the file path for
     * @return the File
     */
    @Nonnull
    public static File getDataFilePath(@Nonnull DataFile df) {
        File f = new File(getParentDirectory(df.getId()), df.getFileName());
        if (f.exists() && tmpDir.exists() && tmpDir.canWrite()) {
            f = copyToTmp(df.getId(), f);
        }
        return f;
    }

    private synchronized static File copyToTmp(int id, File f) {
        File ret = f;
        try {
            File copy = new File(tmpDir, DATA_FILE_PREFIX + Integer.toString(id));
            if (!copy.exists()) {
                FileUtils.copyFile(f, copy);
                startCleanup();
            } else {
                copy.setLastModified(System.currentTimeMillis());
            }
            ret = copy;
        } catch (IOException e) {
            LOG.error("Error copying file returning orignal: " + e, e);
        }
        return ret;
    }

    private static void startCleanup() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    if (tmpDir != null) {
                        File[] files = tmpDir.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.startsWith(DATA_FILE_PREFIX);
                            }
                        });
                        for (File f : files) {
                            if (f.isFile() && f.lastModified() + MAX_AGE < System.currentTimeMillis()) {
                                f.delete();
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error cleaning tmp dir: " + e, e);
                }

            }
        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.setDaemon(true);
        t.start();

    }

    /**
     * Get the path to the directory where all revisinos of the datafile content will be stored.
     * 
     * @param id
     *            the id of the DataFile
     * @return the File to the directory
     */
    @Nonnull
    public static File getParentDirectory(int id) {
        String path = new TankConfig().getDataFileStorageDir();
        File parent = new File(path, Integer.toString(id));
        return parent;
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
        File f = getDataFilePath(dataFile);
        int ret = 0;
        if (f.exists()) {
            try {
                for (LineIterator iterator = FileUtils.lineIterator(f); iterator.hasNext(); iterator.nextLine()) {
                    ret++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

}
