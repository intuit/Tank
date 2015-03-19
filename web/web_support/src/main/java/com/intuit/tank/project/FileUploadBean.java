/**
 * 
 */
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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.UploadedFileIterator;
import com.intuit.tank.wrapper.FileInputStreamWrapper;

@Named
public class FileUploadBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(FileUploadBean.class);

    private boolean useFlash = true;

    @Inject
    @Modified
    private Event<ModifiedDatafileMessage> dataFileEvent;

    @Inject
    private Identity identity;

    @Inject
    private Messages messages;

    public FileUploadBean() {
    }

    @TsLoggedIn
    public void handleFileUpload(FileUploadEvent event) throws Exception {
        UploadedFile item = event.getFile();
        if (item != null) {
            try {
                UploadedFileIterator uploadedFileIterator = new UploadedFileIterator(item, "csv", "txt", "xml");
                StringBuilder sb = new StringBuilder();
                int count = 0;
                FileInputStreamWrapper w = uploadedFileIterator.getNext();
                while (w != null) {
                    createDataFile(w.getFileName(), w.getInputStream());
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(w.getFileName());
                    w = uploadedFileIterator.getNext();
                    count++;
                }

                if (sb.length() == 0) {
                    messages.warn("The uploaded archive, '" + item.getFileName()
                            + "' file did not have any valid files.");
                } else {
                    messages.info("Added " + count + " files.");
                }
            } catch (Exception e) {
                LOG.error("Error extracting zip file: " + e.toString());
                messages.error("Error extracting zip file: " + e.toString());
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(item.getInputstream());
            }
        }
    }

    private void createDataFile(String fileName, InputStream is) {
        DataFile df = new DataFile();
        df.setPath(fileName);
        df.setCreator(identity.getUser().getId());
        df.setModified(new Date());
        df.setCreated(new Date());
        df = new DataFileDao().storeDataFile(df, is);
        dataFileEvent.fire(new ModifiedDatafileMessage(df, this));
    }

    public boolean isUseFlash() {
        return useFlash;
    }

    public void setUseFlash(boolean useFlash) {
        this.useFlash = useFlash;
    }

}