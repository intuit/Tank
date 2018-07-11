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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.owasp.proxy.util.AsciiString;

/**
 * The MessageHeader class is the base class for the HTTP Message, Request and Response classes.
 * 
 * It attempts to be binary clean when the &quot;lowest-level&quot; methods are used, namely:
 * <ul>
 * <li>{@link #getHeader()}</li>
 * <li>{@link #setHeader(byte[])}</li>
 * </ul>
 * 
 * Getting more convenient, there are methods that parse the &quot;header&quot; into lines and allow manipulation
 * thereof:
 * <ul>
 * <li>{@link #getHeaders()}</li>
 * <li>{@link #setHeaders(NamedValue[])}</li>
 * <li>{@link #addHeader(String, String)}</li>
 * <li>{@link #deleteHeader(String)}</li>
 * </ul>
 * 
 */
public interface MutableMessageHeader extends MessageHeader {

    void setId(int id);

    void setHeader(byte[] header);

    void setStartLine(String line) throws MessageFormatException;

    void setHeaders(NamedValue[] headers) throws MessageFormatException;

    void setHeader(String name, String value) throws MessageFormatException;

    void addHeader(String name, String value) throws MessageFormatException;

    String deleteHeader(String name) throws MessageFormatException;

    public static class Impl implements MutableMessageHeader {

        private static final byte[] CRLF = { '\r', '\n' };

        private int id = -1;

        protected byte[] header = null;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setHeader(byte[] header) {
            this.header = header;
        }

        public byte[] getHeader() {
            return header;
        }

        /**
         * Finds the first occurrence of separator, starting at start
         * 
         * @param separator
         * @param start
         * @return
         */
        protected int indexOf(byte[] separator, int start) {
            if (header == null)
                throw new NullPointerException("array is null");
            if (header.length - start < separator.length)
                return -1;
            int sep = start;
            int i = 0;
            while (sep <= header.length - separator.length
                    && i < separator.length) {
                if (header[sep + i] == separator[i]) {
                    i++;
                } else {
                    i = 0;
                    sep++;
                }
            }
            if (i == separator.length)
                return sep;
            return -1;
        }

        /**
         * Breaks the header byte[] up into lines, separated by \r\n. The lines will include the trailing blank line
         * expected as part of the header. The lines do NOT include the actual \r\n separator
         * 
         * @return
         * @throws MessageFormatException
         */
        protected String[] getHeaderLines() throws MessageFormatException {
            if (header == null)
                return null;
            List<String> lines = new LinkedList<String>();
            int sep, start = 0;
            while ((sep = indexOf(CRLF, start)) > -1) {
                lines.add(AsciiString.create(header, start, sep - start));
                start = sep + CRLF.length;
            }
            return lines.toArray(new String[0]);
        }

        /**
         * Converts an array of String to a suitable byte[], by separating each with \r\n. The provided lines should
         * include the trailing blank line
         * 
         * @param lines
         *            including the trailing empty line
         * @throws MessageFormatException
         */
        protected void setHeaderLines(String[] lines)
                throws MessageFormatException {
            ByteArrayOutputStream buff = new ByteArrayOutputStream();
            try {
                for (String line : lines) {
                    buff.write(AsciiString.getBytes(line));
                    buff.write(CRLF);
                }
            } catch (IOException ioe) {
                // impossible for ByteArrayOutputStream
            }
            setHeader(buff.toByteArray());
        }

        /**
         * @return
         * @throws MessageFormatException
         */
        protected String[] getStartParts() throws MessageFormatException {
            String startLine = getStartLine();
            if (startLine == null)
                return new String[0];
            return startLine.split("[ \t]+", 3);
        }

        /**
         * @param parts
         * @throws MessageFormatException
         */
        protected void setStartParts(String[] parts)
                throws MessageFormatException {
            if (parts == null || parts.length == 0) {
                setStartLine(null);
            } else {
                String b = IntStream.range(1, parts.length).mapToObj(i -> " " + (parts[i] == null ? "" : parts[i])).collect(Collectors.joining("", parts[0] == null ? ""
                        : parts[0], ""));
                setStartLine(b);
            }
        }

        /**
         * @return
         * @throws MessageFormatException
         */
        public String getStartLine() throws MessageFormatException {
            String[] lines = getHeaderLines();
            if (lines == null || lines.length == 0)
                return null;
            return lines[0];
        }

        /**
         * @param line
         * @throws MessageFormatException
         */
        public void setStartLine(String line) throws MessageFormatException {
            if (line == null)
                line = "";
            String[] lines = getHeaderLines();
            if (lines == null || lines.length <= 1) {
                lines = new String[2];
                lines[1] = "";
            }
            lines[0] = line;
            setHeaderLines(lines);
        }

        /**
         * @return
         * @throws MessageFormatException
         */
        public NamedValue[] getHeaders() throws MessageFormatException {
            String[] lines = getHeaderLines();
            if (lines == null || lines.length <= 1)
                return null;
            NamedValue[] headers = new NamedValue[lines.length - 2];
            for (int i = 0; i < headers.length; i++)
                headers[i] = NamedValue.parse(lines[i + 1], " *: *");
            return headers;
        }

        /**
         * @param headers
         * @throws MessageFormatException
         */
        public void setHeaders(NamedValue[] headers)
                throws MessageFormatException {
            String[] lines = new String[(headers == null ? 0 : headers.length) + 2];
            lines[0] = getStartLine();
            if (lines[0] == null)
                throw new MessageFormatException(
                        "No start line found, can't set headers without one!",
                        header);
            if (headers != null) {
                for (int i = 0; i < headers.length; i++) {
                    lines[i + 1] = headers[i].toString();
                }
            }
            lines[lines.length - 1] = "";
            setHeaderLines(lines);
        }

        /**
         * @param name
         * @return
         * @throws MessageFormatException
         */
        public String getHeader(String name) throws MessageFormatException {
            NamedValue[] headers = getHeaders();
            if (headers == null || headers.length == 0)
                return null;
            return Arrays.stream(headers).filter(header1 -> name.equalsIgnoreCase(header1.getName())).findFirst().map(NamedValue::getValue).orElse(null);
        }

        /**
         * @param name
         * @param value
         * @throws MessageFormatException
         */
        public void setHeader(String name, String value)
                throws MessageFormatException {
            NamedValue[] headers = getHeaders();
            if (headers != null && headers.length != 0) {
                for (int i = 0; i < headers.length; i++)
                    if (name.equalsIgnoreCase(headers[i].getName())) {
                        headers[i] = new NamedValue(name, headers[i]
                                .getSeparator(), value);
                        setHeaders(headers);
                        return;
                    }
            }
            addHeader(headers, name, value);
        }

        /**
         * @param name
         * @param value
         * @throws MessageFormatException
         */
        public void addHeader(String name, String value)
                throws MessageFormatException {
            addHeader(getHeaders(), name, value);
        }

        /**
         * @param headers
         * @param name
         * @param value
         * @throws MessageFormatException
         */
        private void addHeader(NamedValue[] headers, String name, String value)
                throws MessageFormatException {
            if (headers == null) {
                headers = new NamedValue[1];
            } else {
                NamedValue[] nh = new NamedValue[headers.length + 1];
                System.arraycopy(headers, 0, nh, 0, headers.length);
                headers = nh;
            }
            headers[headers.length - 1] = new NamedValue(name, ": ", value);
            setHeaders(headers);
        }

        /**
         * @param name
         * @return
         * @throws MessageFormatException
         */
        public String deleteHeader(String name) throws MessageFormatException {
            NamedValue[] headers = getHeaders();
            if (headers == null || headers.length == 0)
                return null;
            for (int i = 0; i < headers.length; i++)
                if (name.equalsIgnoreCase(headers[i].getName())) {
                    String ret = headers[i].getValue();
                    NamedValue[] nh = new NamedValue[headers.length - 1];
                    if (i > 0)
                        System.arraycopy(headers, 0, nh, 0, i);
                    if (i < headers.length - 1)
                        System.arraycopy(headers, i + 1, nh, i, headers.length
                                - i - 1);
                    setHeaders(nh);
                    return ret;
                }
            return null;
        }

        @Override
        public String toString() {
            return AsciiString.create(header);
        }

    }
}
