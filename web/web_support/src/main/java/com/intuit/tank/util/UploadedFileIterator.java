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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.UploadedFile;

import com.intuit.tank.wrapper.FileInputStreamWrapper;

/**
 * 
 * @author pquinn
 * 
 */
public class UploadedFileIterator {
    private static final Logger LOG = LogManager.getLogger(UploadedFileIterator.class);

    private String[] extension;
    private FileInputStreamWrapper next;
    private MyInputStream in = null;
    private InputStream itemInputStream;
    private ZipArchiveEntry entry;

    private boolean isZip;

    public UploadedFileIterator(UploadedFile item, String... extension) throws IOException {
        super();
        this.extension = extension;
        isZip = item.getFileName().toLowerCase().endsWith(".zip");
        itemInputStream = item.getInputstream();
        if (isZip) {
            try {
                in = new MyInputStream(
                        new ArchiveStreamFactory().createArchiveInputStream("zip", itemInputStream));
                // moveNext();
            } catch (ArchiveException e) {
                throw new IOException(e);
            }
        } else if (isValid(item.getFileName())) {
            next = new FileInputStreamWrapper(item.getFileName(), itemInputStream);
        }
    }

    /**
     * @param name
     * @return
     */
    private boolean isValid(String name) {
        return Arrays.stream(extension).anyMatch(ext -> name.toLowerCase().endsWith(ext));
    }

    private void moveNext() {
        if (in != null) {
            try {
                entry = (ZipArchiveEntry) in.getNextEntry();
                while (entry != null) {
                    if (!entry.getName().startsWith("_") && !entry.getName().startsWith(".")
                            && isValid(entry.getName())) {
                        next = new FileInputStreamWrapper(FilenameUtils.getName(entry.getName()), in);
                        return;
                    }
                    entry = (ZipArchiveEntry) in.getNextEntry();
                }
            } catch (IOException e) {
                LOG.warn("Error in zip: " + e);
            }
            in.forceClose();
        }
    }

    public FileInputStreamWrapper getNext() {
        if (isZip) {
            moveNext();
        }
        if (next == null) {
            IOUtils.closeQuietly(itemInputStream);
        }
        FileInputStreamWrapper ret = next;
        next = null;
        return ret;
    }

    public static class MyInputStream extends FilterInputStream {
        public MyInputStream(ArchiveInputStream in) {
            super(in);
        }

        public ZipArchiveEntry getNextEntry() throws IOException {
            return (ZipArchiveEntry) ((ArchiveInputStream) in).getNextEntry();
        }

        public void forceClose() {
            IOUtils.closeQuietly(in);
        }

        @Override
        public void close() {
            // do nothing
        }
    }

}
