/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.report;

/*
 * #%L
 * Reporting Rest Service Implementation
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FileServiceV1
 * 
 * @author dangleton
 * 
 */
public final class FileReader {

    private FileReader() {
        // empty private constructor
    }

    private static final Logger LOG = LogManager.getLogger(FileReader.class);

    /**
     * Gets a StreamingOutput from the passedin file from start to end or from beginning to end if start is greater than
     * end. if a negative number is passed, it will get the last n lines of the file. If 0 is passed it will return the
     * entire file.
     * 
     * @param total
     * 
     * @return a StreamingOutput
     */
    public static StreamingOutput getFileStreamingOutput(final File f, long total, String start) {

        long l = 0;
        if (start != null) {
            try {
                l = Long.parseLong(start);
                // num lines to get from end
                if (l < 0) {
                    l = getStartChar(f, Math.abs(l), total);
                }
            } catch (Exception e) {
                LOG.error("Error parsing start " + start + ": " + e);
            }
        }
        final long to = l > total ? 0 : total;
        final long from = l;

        StreamingOutput streamer = (final OutputStream output) -> {
            final FileChannel inputChannel = new FileInputStream(f).getChannel();
            final WritableByteChannel outputChannel = Channels.newChannel(output);
            try {
                inputChannel.transferTo(from, to, outputChannel);
            } finally {
                // closing the channels
                inputChannel.close();
                outputChannel.close();
            }
        };
        LOG.debug("returning data from " + from + " - " + to + " of total " + total);
        return streamer;
    }

    /**
     * @param f
     * @param numLines
     * @param total
     * @return
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked" })
    private static long getStartChar(File f, long numLines, long total) throws IOException {
        List<String> lines = FileUtils.readLines(f, StandardCharsets.UTF_8);
        long count = 0;
        if (lines.size() > numLines) {
            Collections.reverse(lines);
            for (int i = 0; i < numLines; i++) {
                count += lines.get(i).length() + 1;
            }
            count = total - (count + 1);
        }
        return count;
    }

}
