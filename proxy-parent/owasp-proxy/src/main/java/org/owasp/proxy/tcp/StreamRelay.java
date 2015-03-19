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

package org.owasp.proxy.tcp;

import java.io.InputStream;
import java.io.OutputStream;

public class StreamRelay<C, S> implements Runnable {

    private RelayInterceptor<C, S> cs, sc;

    /**
     * This class reads information from the client InputStream, and writes it to the server OutputStream via
     * interceptor, and reads data from the server InputStream and writes it to the client OutputStream via interceptor.
     * 
     * @param interceptor
     *            the interceptor that processes the data
     * @param clientLabel
     *            a label for the interceptor to use to identify the client
     * @param ci
     *            the client InputStream
     * @param co
     *            the client OutputStream
     * @param serverLabel
     *            a label for the interceptor to use to identify the server
     * @param si
     *            the server InputStream
     * @param so
     *            the server OutputStream
     */
    public StreamRelay(StreamInterceptor<C, S> interceptor, C clientLabel,
            InputStream ci, OutputStream co, S serverLabel, InputStream si,
            OutputStream so) {
        cs = new RelayInterceptor<C, S>(interceptor, ci, so);
        cs.setName("Client");
        sc = new RelayInterceptor<C, S>(interceptor, si, co);
        sc.setName("Server");
        interceptor.connected(cs, sc, clientLabel, serverLabel);
    }

    public void setCloseHandlers(Runnable cch, Runnable sch) {
        cs.setCloseHandler(cch);
        sc.setCloseHandler(sch);
    }

    public void run() {
        cs.start();
        sc.start();
        while (cs.isAlive() || sc.isAlive()) {
            try {
                if (cs.isAlive())
                    cs.join(100);
                if (sc.isAlive())
                    sc.join(100);
            } catch (InterruptedException ie) {
                return;
            }
        }
    }
}
