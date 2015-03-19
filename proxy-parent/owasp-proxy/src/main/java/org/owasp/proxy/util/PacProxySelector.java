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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.Proxy.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class PacProxySelector extends ProxySelector {

    protected static final Logger LOGGER = Logger
            .getLogger(PacProxySelector.class.getName());

    protected static final Pattern PAC_RESULT_PATTERN = Pattern
            .compile("(DIRECT|PROXY|SOCKS)(?:\\s+(\\S+):(\\d+))?(?:;|\\z)");

    protected Invocable js;

    /**
     * @param pacReader
     *            A {@link Reader} to a PAC script.
     */
    public PacProxySelector(Reader pacReader) throws Exception {
        init(pacReader);
    }

    protected void init(final Reader pacReader) throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        final ScriptEngine se = sem.getEngineByName("JavaScript");
        if (se instanceof Invocable) {
            js = (Invocable) se;
        } else {
            throw new RuntimeException(
                    "Bad script engine is not an instance of Invocable");
        }
        initPacFunctions(se);

        InputStreamReader isrUtils = new InputStreamReader(getClass()
                .getResourceAsStream("PacUtils.js"));
        try {
            se.eval(isrUtils);
        } finally {
            isrUtils.close();
        }

        try {
            se.eval(pacReader);
        } catch (ScriptException e) {
            LOGGER.log(Level.WARNING, "Error sourcing the PAC file: {1} ", e
                    .getLocalizedMessage());
        } finally {
            pacReader.close();
        }
    }

    protected void initPacFunctions(ScriptEngine se) throws ScriptException {
        se.put("pacFunctions", new PacFunctions());
        se.eval("function alert(message) { pacFunctions.alert(message); }");
        se.eval("function myIpAddress() { "
                + "return pacFunctions.myIpAddress(); }");
        se.eval("function dnsResolve(address) { "
                + "return pacFunctions.dnsResolve(address); }");
    }

    @Override
    public List<Proxy> select(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null.");
        }
        String pacResult = findProxyForUrl(uri);
        List<Proxy> result = convert(pacResult);
        LOGGER.log(Level.FINE, "Returning {0} for {1}.", new Object[] { result,
                uri });
        return result;
    }

    protected String findProxyForUrl(final URI uri) {
        Object o;
        try {
            o = js.invokeFunction("FindProxyForURL", uri.toString(), uri
                    .getHost());
        } catch (ScriptException e) {
            LOGGER.log(Level.WARNING,
                    "Error executing FindProxyForUrl({0}): {1} ", new Object[] {
                            uri, e.getLocalizedMessage() });
            return null;
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.WARNING, "FindProxyForUrl not found");
            return null;
        }
        if (o == null || o instanceof String)
            return (String) o;
        LOGGER.log(Level.WARNING, "FindProxyForURL({0}) returned a {1} ",
                new Object[] { uri, o.getClass() });
        return o.toString();
    }

    protected List<Proxy> convert(String pacResult) {
        List<Proxy> result = new LinkedList<Proxy>();
        if (pacResult != null) {
            convert(pacResult, result);
        }
        if (result.isEmpty()) {
            LOGGER.log(Level.WARNING, "No usable proxies returned: \"{0}\"",
                    pacResult);
            result.add(Proxy.NO_PROXY);
        }
        return result;
    }

    protected void convert(String pacResult, List<Proxy> result) {
        Matcher m = PAC_RESULT_PATTERN.matcher(pacResult);
        while (m.find()) {
            String scriptProxyType = m.group(1);
            if ("DIRECT".equals(scriptProxyType)) {
                result.add(Proxy.NO_PROXY);
            } else {
                Type proxyType;
                if ("PROXY".equals(scriptProxyType)) {
                    proxyType = Type.HTTP;
                } else if ("SOCKS".equals(scriptProxyType)) {
                    proxyType = Type.SOCKS;
                } else {
                    // Should never happen, already filtered by Pattern.
                    throw new RuntimeException("Unrecognized proxy type.");
                }
                result.add(new Proxy(proxyType, new InetSocketAddress(m
                        .group(2), Integer.parseInt(m.group(3)))));
            }
        }
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        LOGGER.log(Level.WARNING, "connectFailed: " + uri + ", " + sa, ioe);
    }

    protected static class PacFunctions {

        public void alert(String s) {
            LOGGER.log(Level.INFO, "PAC-alert: {0}", s);
        }

        public String myIpAddress() throws UnknownHostException {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "myIpAddress called.");
            }
            return InetAddress.getLocalHost().getHostAddress();
        }

        public String dnsResolve(String host) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "dnsResolve called for {0}", host);
            }
            try {
                return InetAddress.getByName(host).getHostAddress();
            } catch (UnknownHostException uhe) {
                LOGGER.log(Level.WARNING, "dnsResolve returning null for {0}",
                        host);
                return null;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("SecurityManager: " + System.getSecurityManager());
        long start = System.currentTimeMillis();
        ProxySelector ps = new PacProxySelector(new StringReader(
                "function FindProxyForURL(url, host) { "
                        + "return \"SOCKS localhost:1080\"; }"));
        System.out.println(ps.select(new URI("http://www.example.com/")));
        System.out.println("Elapsed: " + (System.currentTimeMillis() - start));
    }
}
