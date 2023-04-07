/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * Gets a StreamingResponseBody from the passed in file from start to end or from beginning to end if start is greater than
     * end. if a negative number is passed, it will get the last n lines of the file. If 0 is passed it will return the
     * entire file.
     *
     * @param f
     *          file
     * 
     * @param total
     *          total number of lines
     *
     * @param start
     *          starting line number
     * 
     * @return a StreamingResponseBody
     */
    public static StreamingResponseBody getFileStreamingResponseBody(final File f, long total, String start) {

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

        StreamingResponseBody streamer = (final OutputStream output) -> {
            try (FileChannel inputChannel = new FileInputStream(f).getChannel();
                 WritableByteChannel outputChannel = Channels.newChannel(output)) {
                inputChannel.transferTo(from, to, outputChannel);
            }
            // closing the channels
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
