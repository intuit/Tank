/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service;

/*
 * #%L
 * JSF Support Beans
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
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.PropertyComparer;
import com.intuit.tank.PropertyComparer.SortOrder;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
@Named
public class DataFileService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private static DataFileDao dao;

    /**
     * Returns the list of all DataFiles
     * 
     * @return
     */
    public List<DataFile> getDataFiles() {
        List<DataFile> files = dao.findAll();
        files.sort(new PropertyComparer<DataFile>("fileName",
                SortOrder.ASCENDING));
        return files;
    }

    /**
     * Saves/Updaes a given dataFile in the database.
     * 
     * @param dataFile
     *            dataFile that needs to be updated in the database.
     * @return
     */
    public DataFile saveOrUpdateDataFile(DataFile dataFile) {
        dataFile = dao.saveOrUpdate(dataFile);
        return dataFile;
    }

    /**
     * Deletes a datafile with the given id from the database
     * 
     * @param id
     *            id of the datafile that needs to be deleted from the database.
     */
    public void delete(int id) {
        dao.delete(id);
    }

}
