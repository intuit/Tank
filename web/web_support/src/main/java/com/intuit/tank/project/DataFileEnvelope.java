package com.intuit.tank.project;

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

import com.intuit.tank.project.DataFile;

public class DataFileEnvelope implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean checked;
    private DataFile dataFile;

    public DataFileEnvelope(DataFile df) {
        this.dataFile = df;
    }

    public DataFileEnvelope() {
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked
     *            the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the dataFile
     */
    public DataFile getDataFile() {
        return dataFile;
    }

    /**
     * @param dataFile
     *            the dataFile to set
     */
    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return dataFile.getFileName();
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        dataFile.setFileName(fileName);
    }

    public String getPath() {
        return dataFile.getPath();
    }

    public void setPath(String path) {
        dataFile.setPath(path);
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return dataFile.getComments();
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        dataFile.setComments(comments);
    }

}
