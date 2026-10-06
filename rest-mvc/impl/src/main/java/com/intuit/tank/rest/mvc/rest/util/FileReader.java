/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public final class FileReader {

    private static final int TAIL_BUFFER_SIZE = 8192;

    public record FileStream(
            StreamingResponseBody body,
            long totalLength,
            long startOffset) {
    }

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
     *          total number of bytes
     *
     * @param start
     *          starting byte offset, or a negative number of lines from the end
     * 
     * @return a StreamingResponseBody
     */
    public static FileStream getFileStream(final File f, long total, String start) {
        final long from = resolveStartOffset(f, total, start);
        final long count = total - from;

        StreamingResponseBody body = (final OutputStream output) -> {
            try (FileInputStream inputStream = new FileInputStream(f);
                 FileChannel inputChannel = inputStream.getChannel();
                 WritableByteChannel outputChannel = Channels.newChannel(output)) {
                inputChannel.transferTo(from, count, outputChannel);
            }
        };
        LOG.debug("returning data from " + from + " - " + total + " of total " + total);
        return new FileStream(body, total, from);
    }

    public static StreamingResponseBody getFileStreamingResponseBody(final File f, long total, String start) {
        return getFileStream(f, total, start).body();
    }

    private static long resolveStartOffset(File f, long total, String start) {
        if (start == null) {
            return 0;
        }

        try {
            long requestedOffset = Long.parseLong(start);
            if (requestedOffset < 0) {
                return getStartByte(f, Math.abs(requestedOffset), total);
            }
            // Offsets past EOF yield an empty slice at EOF so clients can detect
            // truncation via X-Content-Start without replaying the whole file.
            return Math.min(Math.max(0, requestedOffset), total);
        } catch (Exception e) {
            LOG.error("Error parsing start " + start + ": " + e);
            return 0;
        }
    }

    /**
     * @param f
     * @param numLines
     * @param total
     * @return
     * @throws IOException
     */
    private static long getStartByte(File f, long numLines, long total) throws IOException {
        if (numLines == 0 || total == 0) {
            return 0;
        }

        long newlines = 0;
        try (RandomAccessFile file = new RandomAccessFile(f, "r")) {
            long blockEnd = total;
            byte[] buffer = new byte[TAIL_BUFFER_SIZE];
            while (blockEnd > 0) {
                int blockLength = (int) Math.min(TAIL_BUFFER_SIZE, blockEnd);
                long blockStart = blockEnd - blockLength;
                file.seek(blockStart);
                file.readFully(buffer, 0, blockLength);

                for (int i = blockLength - 1; i >= 0; i--) {
                    long position = blockStart + i;
                    if (buffer[i] == '\n') {
                        if (position == total - 1) {
                            continue;
                        }
                        newlines++;
                        if (newlines == numLines) {
                            return position + 1;
                        }
                    }
                }
                blockEnd = blockStart;
            }
        }
        return 0;
    }

}
