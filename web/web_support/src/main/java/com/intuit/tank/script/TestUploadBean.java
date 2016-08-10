package com.intuit.tank.script;

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

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;
import org.primefaces.model.UploadedFile;

@Named
@ApplicationScoped
public class TestUploadBean implements Serializable {

    private static final Logger LOG = LogManager.getLogger(TestUploadBean.class);

    private static final long serialVersionUID = 1L;
    private UploadedFile file;

    @Inject
    private Messages messages;

    /**
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file
     *            the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * Saves the script in the database.
     */
    public String save() {
        UploadedFile item = getFile();
        try {
            String fileName = item.getFileName();
            fileName = FilenameUtils.getBaseName(fileName) + "-" + UUID.randomUUID().toString() + "."
                    + FilenameUtils.getExtension(fileName);
            File parent = new File("uploads");
            parent.mkdirs();
            File f = new File(parent, fileName);
            LOG.info("Writing file to " + f.getAbsolutePath());
            FileUtils.writeByteArrayToFile(f, item.getContents());
            messages.info("Wrote file to " + f.getAbsolutePath());
        } catch (Exception e) {
            messages.error(e.getMessage());
        }
        return null;
    }

}
