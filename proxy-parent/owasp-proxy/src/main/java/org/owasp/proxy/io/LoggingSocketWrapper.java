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

package org.owasp.proxy.io;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;

import org.owasp.proxy.util.DebugUtils;

public class LoggingSocketWrapper extends SocketWrapper {

    public LoggingSocketWrapper(Socket socket, PrintStream log,
            String inputPrefix, String outputPrefix) throws IOException {
        super(socket, new LoggingInputStream(socket.getInputStream(), log,
                inputPrefix), new LoggingOutputStream(socket.getOutputStream(),
                log, outputPrefix));
    }

    public static class LoggingInputStream extends FilterInputStream {

        private long last = System.currentTimeMillis();

        private PrintStream log;

        private String prefix;

        private byte[] b = new byte[1];

        private boolean eof = false;

        public LoggingInputStream(InputStream in, PrintStream log, String prefix) {
            super(in);
            this.log = log;
            this.prefix = prefix;
            log.println(prefix + "log started at " + new Date());
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterInputStream#read()
         */
        @Override
        public int read() throws IOException {
            int ret = super.read();
            if (ret > -1) {
                b[0] = (byte) ret;
                log.println(prefix + "read 1 byte at +"
                        + (System.currentTimeMillis() - last));
                DebugUtils.write(log, prefix, b, 0, 1);
            } else {
                log.println(prefix + "read EOF at +"
                        + (System.currentTimeMillis() - last));
                eof = true;
            }
            last = System.currentTimeMillis();
            return ret;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterInputStream#read(byte[], int, int)
         */
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int ret = super.read(b, off, len);
            if (ret > -1) {
                log.println(prefix + "read " + ret + " byte(s) at +"
                        + (System.currentTimeMillis() - last));
                DebugUtils.write(log, prefix, b, off, ret);
            } else {
                log.println(prefix + "read EOF at +"
                        + (System.currentTimeMillis() - last));
                eof = true;
            }
            last = System.currentTimeMillis();
            return ret;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterInputStream#close()
         */
        @Override
        public void close() throws IOException {
            super.close();
            log.println(prefix + "closed "
                    + (eof ? "" : "without reading EOF ") + "at " + new Date());
        }

    }

    public static class LoggingOutputStream extends FilterOutputStream {

        private long last = System.currentTimeMillis();

        private PrintStream log;

        private String prefix;

        private byte[] b = new byte[1];

        public LoggingOutputStream(OutputStream out, PrintStream log,
                String prefix) {
            super(out);
            this.log = log;
            this.prefix = prefix;
            log.println(prefix + "log started at " + new Date());
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterOutputStream#write(byte[], int, int)
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            log.println(prefix + "wrote " + len + " byte(s) at +"
                    + (System.currentTimeMillis() - last));
            DebugUtils.write(log, prefix, b, off, len);
            last = System.currentTimeMillis();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterOutputStream#write(int)
         */
        @Override
        public void write(int b) throws IOException {
            out.write(b);
            this.b[0] = (byte) b;
            log.println(prefix + "wrote 1 byte at +"
                    + (System.currentTimeMillis() - last));
            DebugUtils.write(log, prefix, this.b, 0, this.b.length);
            last = System.currentTimeMillis();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterOutputStream#close()
         */
        @Override
        public void close() throws IOException {
            super.close();
            log.println(prefix + "closed at " + new Date());
        }

    }

}
