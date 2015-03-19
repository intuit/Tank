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

public interface MutableResponseHeader extends ResponseHeader,
        MutableMessageHeader {

    void setVersion(String version) throws MessageFormatException;

    void setStatus(String status) throws MessageFormatException;

    void setReason(String reason) throws MessageFormatException;

    void setHeaderTime(long time);

    void setContentTime(long time);

    public static class Impl extends MutableMessageHeader.Impl implements
            MutableResponseHeader {

        private long headerTime = 0, contentTime = 0;

        protected String[] get100Header(String[] lines) {
            if (lines == null || lines.length < 3)
                // "100 Continue"
                // BLANK
                // BLANK
                return null;
            String[] parts = lines[0].split("[ \t]+", 3);
            if (parts.length < 2 || !"100".equals(parts[1]))
                return null;

            // It's a 100 response, now look for a blank line
            int sep = -1;
            for (int i = 0; i < lines.length; i++) {
                if ("".equals(lines[i])) {
                    sep = i;
                    break;
                }
            }
            if (sep == -1 || sep == lines.length - 1)
                return null;
            String[] h = new String[sep + 1];
            System.arraycopy(lines, 0, h, 0, sep + 1);
            return h;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.proxy.httpclient.MutableMessageHeader.Impl#getHeaderLines (byte[])
         */
        @Override
        protected String[] getHeaderLines() throws MessageFormatException {
            String[] lines = super.getHeaderLines();
            String[] contHeader = get100Header(lines);
            if (contHeader != null) {
                String[] t = new String[lines.length - contHeader.length];
                System.arraycopy(lines, contHeader.length, t, 0, t.length);
                return t;
            }
            return lines;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.proxy.httpclient.MutableMessageHeader.Impl#setHeaderLines (java.lang.String[], byte[])
         */
        @Override
        protected void setHeaderLines(String[] lines)
                throws MessageFormatException {
            String[] contHeader = get100Header(super.getHeaderLines());
            if (contHeader != null) {
                String[] t = new String[contHeader.length + lines.length];
                System.arraycopy(contHeader, 0, t, 0, contHeader.length);
                System.arraycopy(lines, 0, t, contHeader.length, lines.length);
                lines = t;
            }
            super.setHeaderLines(lines);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.proxy.httpclient.ResponseHeader#has100Continue()
         */
        public boolean has100Continue() throws MessageFormatException {
            String[] contHeader = get100Header(super.getHeaderLines());
            return contHeader != null;
        }

        public void setVersion(String version) throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 1) {
                setStartParts(new String[] { version });
            } else {
                parts[0] = version;
                setStartParts(parts);
            }
        }

        public String getVersion() throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length == 0)
                return null;
            return "".equals(parts[0]) ? null : parts[0];
        }

        public void setStatus(String status) throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 2) {
                String[] p = new String[2];
                p[0] = parts.length >= 1 ? parts[0] : "HTTP/1.0";
                parts = p;
            }
            parts[1] = status;
            setStartParts(parts);
        }

        public String getStatus() throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 2)
                return null;
            return "".equals(parts[1]) ? null : parts[1];
        }

        public void setReason(String reason) throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 3) {
                String[] p = new String[3];
                p[0] = parts.length >= 1 ? parts[0] : "HTTP/1.0";
                p[1] = parts.length >= 2 ? parts[1] : "200";
                parts = p;
            }
            parts[2] = reason;
            setStartParts(parts);
        }

        public String getReason() throws MessageFormatException {
            String[] parts = getStartParts();
            if (parts.length < 3)
                return null;
            return "".equals(parts[2]) ? null : parts[2];
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.httpclient.MutableResponseHeader#setHeaderStartedTime(long)
         */
        public void setHeaderTime(long time) {
            headerTime = time;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.httpclient.ResponseHeader#getHeaderCompletedTime()
         */
        public long getHeaderTime() {
            return headerTime;
        }

        public void setContentTime(long time) {
            contentTime = time;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.httpclient.StreamingResponse#getContentCompletedTime()
         */
        public long getContentTime() {
            return contentTime;
        }

    }
}
