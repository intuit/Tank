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

package org.owasp.proxy.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Pump extends Thread {
    private InputStream in;
    private OutputStream out;

    public static void connect(Socket a, Socket b) throws IOException {
        Pump ab = new Pump(a.getInputStream(), b.getOutputStream());
        Pump ba = new Pump(b.getInputStream(), a.getOutputStream());
        ab.start();
        ba.start();
        while (ab.isAlive()) {
            try {
                ab.join();
            } catch (InterruptedException ie) {
            }
        }
        while (ba.isAlive()) {
            try {
                ba.join();
            } catch (InterruptedException ie) {
            }
        }
    }

    public Pump(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        setDaemon(true);
    }

    public void run() {
        try {
            byte[] buff = new byte[4096];
            int got;
            while ((got = in.read(buff)) > -1)
                out.write(buff, 0, got);
        } catch (IOException ignore) {
        } finally {
            try {
                in.close();
            } catch (IOException ignore) {
            }
            try {
                out.close();
            } catch (IOException ignore) {
            }
        }
    }
}
