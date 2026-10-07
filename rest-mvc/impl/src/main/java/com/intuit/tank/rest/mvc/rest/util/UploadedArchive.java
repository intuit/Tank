/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The files in one upload: the upload itself, or each entry of a {@code .zip} upload. Shared by the web UI
 * and the REST API so both accept the same files.
 *
 * <p>Only names with one of the given extensions are returned; zip entries whose names start with
 * {@code _} or {@code .} (such as {@code __MACOSX/} and {@code .DS_Store}) are skipped, and folder names
 * are dropped.</p>
 *
 * <p>Read each file's stream before asking for the next one: zip entries are read in sequence from the
 * upload. Closing an entry's stream does not close the upload.</p>
 */
public class UploadedArchive implements Closeable {

    private static final Logger LOG = LogManager.getLogger(UploadedArchive.class);

    /** Zip archives with more entries than this are rejected, to bound the work one upload can cause. */
    public static final int MAX_ENTRIES = 1000;

    /**
     * One file of the upload.
     *
     * @param name the file name without folders
     */
    public record UploadedFile(String name, InputStream content) {
    }

    private final String[] extensions;
    private final InputStream upload;
    private final ArchiveInputStream<?> zip;
    private UploadedFile single;
    private int entries;
    private final List<String> skipped = new ArrayList<>();

    /**
     * @param fileName   the uploaded file's name; a name ending in {@code .zip} is read as an archive
     * @param extensions the accepted file extensions, such as {@code "csv"}
     */
    public UploadedArchive(String fileName, InputStream upload, String... extensions) throws IOException {
        this.extensions = extensions;
        this.upload = upload;
        String name = fileName != null ? fileName : "";
        if (isZip(name)) {
            try {
                zip = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, upload);
            } catch (ArchiveException e) {
                throw new IOException("Not a valid zip archive: " + name, e);
            }
        } else {
            zip = null;
            String plain = FilenameUtils.getName(name);
            if (isAccepted(plain)) {
                single = new UploadedFile(plain, upload);
            } else {
                skipped.add(plain);
            }
        }
    }

    public static boolean isZip(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".zip");
    }

    /**
     * @return the next accepted file, or null when there are no more
     * @throws IOException when the archive cannot be read or has more than {@value #MAX_ENTRIES} entries
     */
    public UploadedFile next() throws IOException {
        if (zip == null) {
            UploadedFile ret = single;
            single = null;
            return ret;
        }
        ZipArchiveEntry entry = (ZipArchiveEntry) zip.getNextEntry();
        while (entry != null) {
            if (++entries > MAX_ENTRIES) {
                throw new IOException("The archive has more than " + MAX_ENTRIES + " entries");
            }
            String name = FilenameUtils.getName(entry.getName());
            if (!entry.isDirectory() && !entry.getName().startsWith("_") && !entry.getName().startsWith(".")
                    && !name.startsWith(".") && isAccepted(name)) {
                return new UploadedFile(name, CloseShieldInputStream.wrap(zip));
            }
            LOG.debug("Skipping archive entry {}", entry.getName());
            if (!entry.isDirectory()) {
                skipped.add(entry.getName());
            }
            entry = (ZipArchiveEntry) zip.getNextEntry();
        }
        return null;
    }

    /**
     * @return the files and archive entries passed over so far because they are not accepted
     */
    public List<String> skipped() {
        return Collections.unmodifiableList(skipped);
    }

    private boolean isAccepted(String name) {
        return !name.isEmpty() && Arrays.stream(extensions).anyMatch(ext -> name.toLowerCase().endsWith(ext.toLowerCase()));
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(zip);
        IOUtils.closeQuietly(upload);
    }
}
