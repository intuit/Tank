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

import java.net.InetSocketAddress;

public interface MutableRequestHeader extends RequestHeader,
        MutableMessageHeader {

    void setTarget(InetSocketAddress target);

    void setSsl(boolean ssl);

    void setMethod(String method) throws MessageFormatException;

    void setResource(String resource) throws MessageFormatException;

    void setVersion(String version) throws MessageFormatException;

    void setTime(long time);

    public static class Impl extends MutableMessageHeader.Impl implements
            MutableRequestHeader {

        private InetSocketAddress target;

        private boolean ssl;

        private long time = 0;

        public InetSocketAddress getTarget() {
            return target;
        }

        public void setTarget(InetSocketAddress target) {
            this.target = target;
        }

        public boolean isSsl() {
            return ssl;
        }

        public void setSsl(boolean ssl) {
            this.ssl = ssl;
        }

        @Override
        protected String[] getStartParts() throws MessageFormatException {
            String[] parts = super.getStartParts();
            if (parts.length == 3 && parts[2] != null
                    && parts[2].matches(" \t"))
                throw new MessageFormatException(
                        "HTTP Version may not contain whitespace", header);
            return parts;
        }

        public void setMethod(String method) throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 1) {
                setStartParts(new String[] { method });
            } else {
                parts[0] = method;
                setStartParts(parts);
            }
        }

        public String getMethod() throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length == 0)
                return null;
            return "".equals(parts[0]) ? null : parts[0];
        }

        public void setResource(String resource) throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 2) {
                String[] p = new String[2];
                p[0] = parts.length >= 1 ? parts[0] : "GET";
                parts = p;
            }
            parts[1] = resource;
            setStartParts(parts);
        }

        public String getResource() throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 2)
                return null;
            return "".equals(parts[1]) ? null : parts[1];
        }

        public void setVersion(String version) throws MessageFormatException {
            if (version != null && version.matches(" \t"))
                throw new MessageFormatException(
                        "HTTP version may not contain whitespace", header);
            String[] parts = getStartParts();
            if (parts.length < 3) {
                String[] p = new String[3];
                p[0] = parts.length >= 1 ? parts[0] : "GET";
                p[1] = parts.length >= 2 ? parts[1] : "/";
                parts = p;
            }
            parts[2] = version;
            setStartParts(parts);
        }

        public String getVersion() throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 3)
                return null;
            return "".equals(parts[2]) ? null : parts[2];
        }

        @Override
        public String toString() {
            return (ssl ? "SSL " : "") + target + "\n" + super.toString();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.httpclient.RequestHeader#getSubmissionTime()
         */
        public long getTime() {
            return time;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.httpclient.MutableRequestHeader#setSubmissionTime(long)
         */
        public void setTime(long time) {
            this.time = time;
        }

    }
}
