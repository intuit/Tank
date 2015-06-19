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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.owasp.proxy.daemon.Proxy;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.http.client.HttpClient;
import org.owasp.proxy.test.TraceServer;

public class TcpInterceptorTest {

    private static Logger logger = Logger.getAnonymousLogger();

    private static TraceServer ts;

    private InetSocketAddress listen;

    private TargetedConnectionHandler ch;

    private HashSet<StreamHandle> handlers = new HashSet<StreamHandle>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            ts = new TraceServer(9999);
            ts.setChunked(true);
            ts.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setup() throws Exception {
        listen = new InetSocketAddress("localhost", 9998);
        StreamInterceptor<InetSocketAddress, InetSocketAddress> si = new StreamInterceptor<InetSocketAddress, InetSocketAddress>() {
            public void connected(StreamHandle cs, StreamHandle sc,
                    InetSocketAddress cl, InetSocketAddress sl) {
                logger.info("Connected " + cl + " to " + sl);
                if (!handlers.contains(cs)) {
                    handlers.add(cs);
                } else {
                    fail("Connect called twice for the same handler");
                }
                if (!handlers.contains(sc)) {
                    handlers.add(sc);
                } else {
                    fail("Connect called twice for the same handler");
                }
            }

            public void inputClosed(StreamHandle handle) {
                logger.info(handle + " : input closed, closing output");
                handle.close();
                if (handlers.contains(handle)) {
                    handlers.remove(handle);
                } else {
                    fail("Closed called twice for the same handler");
                }
            }

            public void readException(StreamHandle handle, IOException ioe) {
                logger.info(handle + ": error reading: " + ioe);
                if (!handlers.contains(handle))
                    fail("readException called for nonexistent handler");
            }

            public void received(StreamHandle handle, byte[] b, int off, int len) {
                // logger.info(handle + ": received '" + new String(b, off, len)
                // + "'");
                if (!handlers.contains(handle))
                    fail("receive called for nonexistent handler");
                try {
                    handle.write(b, off, len);
                } catch (IOException ioe) {
                    logger.info(handle + ": error writing to the output: "
                            + ioe);
                }
            }

        };
        ch = new InterceptingConnectionHandler(si);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        ts.stop();
        Thread.sleep(1000);
        assertTrue("TraceServer shutdown failed!", ts.isStopped());
    }

    @Test
    public void testServerClose() throws Exception {
        InetSocketAddress target = new InetSocketAddress("127.0.0.1", 9999);
        Proxy proxy = new Proxy(listen, ch, target);
        proxy.start();

        HttpClient client = new HttpClient();
        client.setSoTimeout(0);
        client.connect(listen, false);

        assertEquals("Expected '200' response", 200, sendRequest(client, true));
        assertEquals("Expected '200' response", 200, sendRequest(client, false));

        client.disconnect();
        Thread.sleep(2000);
        assertEquals("Both handlers did not terminate", 0, handlers.size());
        proxy.stop();
        assertTrue("Listener didn't exit", proxy.isStopped());
    }

    @Test
    public void testClientClose() throws Exception {
        InetSocketAddress target = new InetSocketAddress("127.0.0.1", 9999);
        Proxy proxy = new Proxy(listen, ch, target);
        proxy.start();

        HttpClient client = new HttpClient();
        client.setSoTimeout(0);
        client.connect(listen, false);

        assertEquals("Expected '200' response", 200, sendRequest(client, true));
        assertEquals("Expected '200' response", 200, sendRequest(client, true));

        client.disconnect();
        Thread.sleep(2000);
        assertEquals("Both handlers did not terminate", 0, handlers.size());
        proxy.stop();
        assertTrue("Listener didn't exit", proxy.isStopped());
    }

    private int sendRequest(HttpClient client, boolean keepAlive)
            throws IOException, MessageFormatException {
        StreamingRequest req = new StreamingRequest.Impl();
        req.setTarget(listen);
        req.setSsl(false);
        req.setHeader(("GET / HTTP/1.1\r\n\r\n").getBytes());
        if (keepAlive)
            req.setHeader("Connection", "Keep-alive");

        StreamingResponse sr = client.fetchResponse(req);
        MutableBufferedResponse br = new MutableBufferedResponse.Impl();
        MessageUtils.buffer(sr, br, Integer.MAX_VALUE);
        System.out.println(br.getStartLine());
        return Integer.parseInt(br.getStatus());
    }
}
