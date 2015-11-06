/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.util.DataFileUtil;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class DataFileDao extends BaseDao<DataFile> {

    private static final Logger LOG = Logger.getLogger(DataFileDao.class);

    /**
     * @param entityClass
     */
    public DataFileDao() {
        super();
    }

    /**
     * Stores the datafile by saving the content from the input stream to persistent storage. Currently a mounted S3
     * directory.
     * 
     * @param df
     *            the datafile to store
     * @param is
     *            the stream of the content of the file to store. pass in null for no update to the file. Note, that new
     *            datafiles must have an inputStream.
     * @return the persisted datafile
     */
    @Nonnull
    public DataFile storeDataFile(@Nonnull DataFile df, @Nullable InputStream is) {
        df = saveOrUpdate(df);
        if (is != null) {

            // store file and
            boolean delete = true;
            try {
                storeFile(is, df);
                df = saveOrUpdate(df);
                delete = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                if (delete) {
                    delete(df);
                }
            }
        } else if (df.getId() == 0) {
            LOG.error("New Datafiles must have a file associated with them.");
            // throw new IllegalArgumentException("New Datafiles must have a file associated with them.");
        }
        return df;
    }

    // /**
    // * @{inheritDoc}
    // * Don't want to delete files because we may be using a version of the datafile.
    // */
    // @Override
    // public void delete(Integer id) throws HibernateException {
    // super.delete(id);
    // File parent = DataFileUtil.getParentDirectory(id);
    // if (parent.exists()) {
    // try {
    // FileUtils.deleteDirectory(parent);
    // } catch (IOException e) {
    // LOG.error("Cannot delete directory " + parent.getAbsolutePath(), e);
    // }
    // }
    // }

    /**
     * @param is
     * @param fileName
     */
    private void storeFile(InputStream is, DataFile df) throws IOException, IllegalAccessException {
        FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getDataFileStorageDir(), false);
        String fileName = UUID.randomUUID().toString() + "_" + df.getPath();
        df.setFileName(fileName);
        FileData fd = DataFileUtil.getFileData(df);
        fileStorage.storeFileData(fd, is);
    }

}
