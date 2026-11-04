package com.intuit.tank.util;

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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.file.UploadedFile;

import com.intuit.tank.rest.mvc.rest.util.UploadedArchive;
import com.intuit.tank.wrapper.FileInputStreamWrapper;

/**
 * The files in a web UI upload: the file itself, or each accepted entry of a zip. The rules are shared with
 * the REST API through {@link UploadedArchive}.
 */
public class UploadedFileIterator {
    private static final Logger LOG = LogManager.getLogger(UploadedFileIterator.class);

    private final UploadedArchive archive;

    /**
     * @param extension the accepted file extensions
     */
    public UploadedFileIterator(UploadedFile item, String... extension) throws IOException {
        archive = new UploadedArchive(item.getFileName(), item.getInputStream(), extension);
    }

    /**
     * @return the next accepted file, or null when there are no more (or the archive cannot be read further)
     */
    public FileInputStreamWrapper getNext() {
        try {
            UploadedArchive.UploadedFile next = archive.next();
            if (next != null) {
                return new FileInputStreamWrapper(next.name(), next.content());
            }
        } catch (IOException e) {
            LOG.warn("Error in zip: {}", String.valueOf(e));
        }
        archive.close();
        return null;
    }
}
