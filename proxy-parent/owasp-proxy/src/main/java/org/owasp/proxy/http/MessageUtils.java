/*
 * This file is part of the OWASP Proxy, a free intercepting proxy library.
 * Copyright (C) 2008-2010 Rogan Dawes <rogan@dawes.za.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to:
 * The Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.owasp.proxy.http;

import static org.owasp.proxy.http.HttpConstants.CHUNKED;
import static org.owasp.proxy.http.HttpConstants.CONTENT_ENCODING;
import static org.owasp.proxy.http.HttpConstants.CONTENT_LENGTH;
import static org.owasp.proxy.http.HttpConstants.DEFLATE;
import static org.owasp.proxy.http.HttpConstants.GZIP;
import static org.owasp.proxy.http.HttpConstants.IDENTITY;
import static org.owasp.proxy.http.HttpConstants.TRANSFER_ENCODING;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.owasp.proxy.io.ChunkedInputStream;
import org.owasp.proxy.io.ChunkingInputStream;
import org.owasp.proxy.io.CopyInputStream;
import org.owasp.proxy.io.CountingInputStream;
import org.owasp.proxy.io.DeflaterInputStream;
import org.owasp.proxy.io.FixedLengthInputStream;
import org.owasp.proxy.io.GunzipInputStream;
import org.owasp.proxy.io.GzipInputStream;
import org.owasp.proxy.io.SizeLimitExceededException;
import org.owasp.proxy.io.SizeLimitedByteArrayOutputStream;

public class MessageUtils {

    /**
     * @param content
     * @param codings
     * @return
     * @throws MessageFormatException
     */
    public static InputStream decode(StreamingMessage message)
            throws IOException, MessageFormatException {
        return decode(message, message.getContent());
    }

    public static byte[] decode(BufferedMessage message)
            throws MessageFormatException {
        return decode(message, message.getContent());
    }

    @SuppressWarnings("resource")
    public static byte[] decode(MessageHeader message, byte[] content)
            throws MessageFormatException {
        try {
            if (content == null || content.length == 0) {
                return new byte[0];
            }
            InputStream is = new ByteArrayInputStream(content);
            is = decode(message, is);
            ByteArrayOutputStream copy = new ByteArrayOutputStream();
            byte[] buff = new byte[4096];
            int got;
            while ((got = is.read(buff)) > 0) {
                copy.write(buff, 0, got);
            }
            return copy.toByteArray();
        } catch (IOException ioe) {
            throw new MessageFormatException("Malformed encoded content: "
                    + ioe.getMessage(), ioe);
        }
    }

    public static InputStream decode(MessageHeader message, InputStream content)
            throws IOException, MessageFormatException {
        if (content == null)
            return content;
        String codings = message.getHeader(TRANSFER_ENCODING);
        content = decode(codings, content);
        codings = message.getHeader(CONTENT_ENCODING);
        content = decode(codings, content);
        return content;
    }

    public static InputStream decode(String codings, InputStream content)
            throws IOException, MessageFormatException {
        if (codings == null || codings.trim().isEmpty())
            return content;
        String[] algos = codings.split("[ \t]*,[ \t]*");
        if (algos.length == 1 && IDENTITY.equalsIgnoreCase(algos[0]))
            return content;
        for (String algo : algos) {
            if (CHUNKED.equalsIgnoreCase(algo)) {
                content = new ChunkedInputStream(content);
            } else if (DEFLATE.equalsIgnoreCase(algo)) {
                content = new DeflaterInputStream(content);
            } else if (GZIP.equalsIgnoreCase(algo)) {
                content = new GunzipInputStream(content);
            } else if (IDENTITY.equalsIgnoreCase(algo)) {
                // nothing to do
            } else
                throw new MessageFormatException("Unsupported coding : "
                        + algo);
        }
        return content;
    }

    /**
     * @param content
     * @param codings
     * @return
     * @throws MessageFormatException
     */
    public static InputStream encode(StreamingMessage message)
            throws MessageFormatException {
        return encode(message, message.getContent());
    }

    public static byte[] encode(BufferedMessage message)
            throws MessageFormatException {
        return encode(message, message.getContent());
    }

    public static byte[] encode(MessageHeader header, byte[] content)
            throws MessageFormatException {
        InputStream contentStream = new ByteArrayInputStream(content);
        contentStream = encode(header, contentStream);
        ByteArrayOutputStream copy = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int got;
        try {
            while ((got = contentStream.read(buff)) > 0)
                copy.write(buff, 0, got);
        } catch (IOException ioe) {
            throw new MessageFormatException("Error encoding content", ioe);
        }
        return copy.toByteArray();
    }

    /**
     * @param content
     * @param codings
     * @return
     * @throws MessageFormatException
     */
    public static InputStream encode(MessageHeader header, InputStream content)
            throws MessageFormatException {
        String codings = header.getHeader("Content-Encoding");
        content = encode(codings, content);
        codings = header.getHeader("Transfer-Encoding");
        content = encode(codings, content);
        return content;
    }

    public static InputStream encode(String codings, InputStream content)
            throws MessageFormatException {
        if (codings == null || codings.trim().isEmpty())
            return content;
        String[] algos = codings.split("[ \t]*,[ \t]*");
        if (algos.length == 1 && IDENTITY.equalsIgnoreCase(algos[0]))
            return content;
        for (String algo : algos) {
            if (CHUNKED.equalsIgnoreCase(algo)) {
                content = new ChunkingInputStream(content);
            } else if (GZIP.equalsIgnoreCase(algo)) {
                content = new GzipInputStream(content);
            } else if (IDENTITY.equalsIgnoreCase(algo)) {
                // nothing to do
            } else
                throw new MessageFormatException("Unsupported coding : "
                        + algo);
        }
        return content;
    }

    public static boolean flushContent(MutableMessageHeader header,
            InputStream in) throws MessageFormatException, IOException {
        return flushContent(header, in, null);
    }

    public static boolean flushContent(MutableMessageHeader header,
            InputStream in, OutputStream out) throws MessageFormatException,
            IOException {
        boolean read = false;
        String te = header.getHeader(TRANSFER_ENCODING);
        if (CHUNKED.equalsIgnoreCase(te)) {
            in = new ChunkedInputStream(in);
        } else if (te != null) {
            throw new IOException("Unknown Transfer-Encoding '" + te + "'");
        } else {
            String cl = header.getHeader(CONTENT_LENGTH);
            if (cl != null) {
                try {
                    int l = Integer.parseInt(cl.trim());
                    if (l == 0)
                        return read;
                    in = new FixedLengthInputStream(in, l);
                } catch (NumberFormatException nfe) {
                    throw new MessageFormatException(
                            "Invalid Content-Length header: " + cl, nfe);
                }
            }
        }
        byte[] buff = new byte[2048];
        int got;
        while ((got = in.read(buff)) > 0) {
            read = true;
            if (out != null)
                out.write(buff, 0, got);
        }

        return read;
    }

    /**
     * Get the exact representation of the message
     * 
     * @return the internal byte[] representing the contents of this message.
     */
    public static byte[] getMessage(MutableBufferedMessage message) {
        byte[] header = message.getHeader();
        byte[] content = message.getContent();
        if (content != null && content.length > 0) {
            byte[] bytes = new byte[header.length + content.length];
            System.arraycopy(header, 0, bytes, 0, header.length);
            System.arraycopy(content, 0, bytes, header.length, content.length);
            return bytes;
        }
        return header;
    }

    public static boolean expectContent(RequestHeader request)
            throws MessageFormatException {
        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method))
            return false;
        String contentLength = request.getHeader(CONTENT_LENGTH);
        String transferEncoding = request.getHeader(TRANSFER_ENCODING);
        if (transferEncoding != null)
            return true;
        if (contentLength == null)
            throw new MessageFormatException(
                    "Request content expected, but no length specified!",
                    request.getHeader());
        try {
            int cl = Integer.parseInt(contentLength);
            if (cl < 0)
                throw new MessageFormatException("Negative content length: "
                        + contentLength, request.getHeader());
            if (cl == 0)
                return false;
            return true;
        } catch (NumberFormatException nfe) {
            throw new MessageFormatException("Invalid content length: "
                    + contentLength, nfe, request.getHeader());
        }
    }

    public static boolean expectContent(RequestHeader request,
            ResponseHeader response) throws MessageFormatException {
        String method = request.getMethod();
        String status = response.getStatus();
        return !("HEAD".equalsIgnoreCase(method) || "204".equals(status) || "304"
                .equals(status));
    }

    public static void buffer(StreamingRequest request,
            MutableBufferedRequest buff, int max) throws IOException,
            SizeLimitExceededException {
        buff.setTarget(request.getTarget());
        buff.setSsl(request.isSsl());
        buffer((StreamingMessage) request, buff, max);
    }

    public static void buffer(StreamingResponse response,
            MutableBufferedResponse buff, int max) throws IOException,
            SizeLimitExceededException {
        buffer((StreamingMessage) response, buff, max);
    }

    private static void buffer(StreamingMessage message,
            MutableBufferedMessage buffered, int max) throws IOException,
            SizeLimitExceededException {
        buffered.setHeader(message.getHeader());

        InputStream in = message.getContent();
        if (in != null) {
            ByteArrayOutputStream copy;
            copy = new SizeLimitedByteArrayOutputStream(max);
            byte[] b = new byte[1024];
            int got;
            try {
                while ((got = in.read(b)) > -1) {
                    copy.write(b, 0, got);
                }
                buffered.setContent(copy.toByteArray());
            } catch (SizeLimitExceededException slee) {
                buffered.setContent(copy.toByteArray());
                throw slee;
            } catch (IOException e) {
                System.err.println("Error while reading content: " + e);
                // no content to set
            }
        }
    }

    public static void stream(BufferedRequest request, StreamingRequest stream) {
        stream.setTarget(request.getTarget());
        stream.setSsl(request.isSsl());
        stream((BufferedMessage) request, stream);
    }

    public static void stream(BufferedResponse response,
            StreamingResponse stream) {
        stream((BufferedMessage) response, stream);
    }

    private static void stream(BufferedMessage message, StreamingMessage stream) {
        stream.setHeader(message.getHeader());
        byte[] content = message.getContent();
        if (content != null && content.length > 0)
            stream.setContent(new ByteArrayInputStream(content));
    }

    public static void delayedCopy(final StreamingRequest message,
            final MutableBufferedRequest copy, int max,
            final DelayedCopyObserver observer) {
        copy.setTarget(message.getTarget());
        copy.setSsl(message.isSsl());
        delayedCopy((StreamingMessage) message, (MutableBufferedMessage) copy,
                max, new DelayedCopyObserver() {
                    @Override
                    public void copyCompleted(boolean overflow, int size) {
                        copy.setTime(message.getTime());
                        observer.copyCompleted(overflow, size);
                    }
                });
    }

    public static void delayedCopy(final StreamingResponse message,
            final MutableBufferedResponse copy, int max,
            final DelayedCopyObserver observer) {
        copy.setHeaderTime(message.getHeaderTime());
        delayedCopy((StreamingMessage) message, (MutableBufferedMessage) copy,
                max, new DelayedCopyObserver() {
                    @Override
                    public void copyCompleted(boolean overflow, int size) {
                        copy.setHeaderTime(message.getHeaderTime());
                        copy.setContentTime(message.getContentTime());
                        observer.copyCompleted(overflow, size);
                    }

                });
    }

    private static void delayedCopy(StreamingMessage message,
            final MutableBufferedMessage copy, final int max,
            final DelayedCopyObserver observer) {
        if (observer == null)
            throw new NullPointerException("Observer may not be null");

        copy.setHeader(message.getHeader());
        InputStream content = message.getContent();
        if (content == null) {
            observer.copyCompleted(false, 0);
        } else {
            final ByteArrayOutputStream copyContent = new SizeLimitedByteArrayOutputStream(
                    max) {
                @Override
                protected void overflow() {
                    // do not throw an exception
                }
            };
            content = new CopyInputStream(content, copyContent);
            content = new CountingInputStream(content) {
                protected void eof() {
                    copy.setContent(copyContent.toByteArray());
                    observer.copyCompleted(getCount() > max, getCount());
                }
            };
            message.setContent(content);
        }
    }

    public static abstract class DelayedCopyObserver {

        public abstract void copyCompleted(boolean overflow, int size);

    }

    public static boolean isExpectContinue(RequestHeader request)
            throws MessageFormatException {
        return "100-continue".equalsIgnoreCase(request.getHeader("Expect"));
    }

}
