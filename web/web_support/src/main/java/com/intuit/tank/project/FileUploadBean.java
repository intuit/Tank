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

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.auth.TankSecurityContext;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;
import org.primefaces.event.FileUploadEvent;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.UploadedFileIterator;
import com.intuit.tank.wrapper.FileInputStreamWrapper;
import org.primefaces.model.file.UploadedFile;

@Named
@RequestScoped
public class FileUploadBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(FileUploadBean.class);

    private boolean useFlash = true;

    @Inject
    @Modified
    private Event<ModifiedDatafileMessage> dataFileEvent;

    @Inject
    private TankSecurityContext securityContext;

    @Inject
    private Messages messages;

    public FileUploadBean() {
    }

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
                IOUtils.closeQuietly(item.getInputStream());
            }
        }
    }

    private void createDataFile(String fileName, InputStream is) {
        DataFile df = new DataFile();
        df.setPath(fileName);
        df.setCreator(securityContext.getCallerPrincipal().getName());
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