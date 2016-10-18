package com.intuit.tank.proxy;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.owasp.proxy.daemon.LoopAvoidingTargetedConnectionHandler;
import org.owasp.proxy.daemon.Proxy;
import org.owasp.proxy.daemon.ServerGroup;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.client.HttpClient;
import org.owasp.proxy.http.server.BufferedMessageInterceptor;
import org.owasp.proxy.http.server.DefaultHttpRequestHandler;
import org.owasp.proxy.http.server.HttpProxyConnectionHandler;
import org.owasp.proxy.http.server.HttpRequestHandler;
import org.owasp.proxy.http.server.LoggingHttpRequestHandler;
import org.owasp.proxy.socks.SocksConnectionHandler;
import org.owasp.proxy.ssl.AutoGeneratingContextSelector;
import org.owasp.proxy.ssl.SSLConnectionHandler;
import org.owasp.proxy.ssl.SSLContextSelector;
import org.owasp.proxy.util.TextFormatter;

import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.entity.Application;
import com.intuit.tank.handler.BufferingHttpRequestHandler;
import com.intuit.tank.proxy.config.CommonsProxyConfiguration;
import com.intuit.tank.proxy.config.ProxyConfiguration;
import com.intuit.tank.proxy.table.TransactionRecordedListener;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main implements TransactionRecordedListener {

    private static Logger logger = Logger.getLogger("org.owasp.proxy");

    private Proxy p;
    private Application application;

    private int port;
    private int apiPort;
    private String dumpFileName;
    static {
        System.setProperty("https.protocols", "TLSv1.1");
    }

    /**
     * 
     */
    public Main(ProxyConfiguration config, int apiPort) {
        this.apiPort = apiPort;
        this.application = new Application(config);
        port = config.getPort();
        dumpFileName = config.getOutputFile();
        try {
            startHttpServer();
        } catch (IOException e) {
            e.printStackTrace();
            quit();
        }
    }

    public void start() {
        try {
            application.startSession(this);
            logger.setUseParentHandlers(false);
            Handler ch = new ConsoleHandler();
            ch.setFormatter(new TextFormatter());
            logger.addHandler(ch);

            InetSocketAddress listen;
            try {
                listen = new InetSocketAddress("localhost", port);
            } catch (NumberFormatException nfe) {
                usage();
                return;
            }
            String proxy = "DIRECT";
            final ProxySelector ps = getProxySelector(proxy);

            DefaultHttpRequestHandler drh = new DefaultHttpRequestHandler() {
                @Override
                protected HttpClient createClient() {
                    HttpClient client = super.createClient();
                    client.setProxySelector(ps);
                    client.setSoTimeout(20000);
                    return client;
                }
            };
            ServerGroup sg = new ServerGroup();
            sg.addServer(listen);
            drh.setServerGroup(sg);
            HttpRequestHandler rh = drh;
            rh = new LoggingHttpRequestHandler(rh);

            BufferedMessageInterceptor bmi = new BufferedMessageInterceptor() {
                @Override
                public Action directResponse(RequestHeader request,
                        MutableResponseHeader response) {
                    // System.err.println(new String(request.getHeader())
                    // + "+++++++++++\n" + new String(response.getHeader())
                    // + "===========");
                    return Action.BUFFER;
                }

                @Override
                public Action directRequest(MutableRequestHeader request) {
                    // System.err.println(new String(request.getHeader())
                    // + "===========");
                    return Action.BUFFER;
                }
            };
            rh = new BufferingHttpRequestHandler(rh, bmi, 1024 * 1024, application);

            HttpProxyConnectionHandler hpch = new HttpProxyConnectionHandler(rh);
            SSLContextSelector cp = getSSLContextSelector();
            TargetedConnectionHandler tch = new SSLConnectionHandler(cp, true, hpch);
            tch = new LoopAvoidingTargetedConnectionHandler(sg, tch);
            hpch.setConnectHandler(tch);
            TargetedConnectionHandler socks = new SocksConnectionHandler(tch, true);
            p = new Proxy(listen, socks, null);
            p.setSocketTimeout(90000);
            p.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void stop() {
        if (p != null) {
            try {
                p.stop();
                application.endSession();
                System.out.println("Terminated");
                p = null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void quit() {
        try {
            stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @SuppressWarnings("restriction")
	public void startHttpServer() throws IOException {
        InetSocketAddress addr = new InetSocketAddress(apiPort);
        HttpServer server = HttpServer.create(addr, 0);

        HttpHandler handler = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String requestMethod = exchange.getRequestMethod();
                URI requestURI = exchange.getRequestURI();
                String msg = "unknown path";
                if (requestMethod.equalsIgnoreCase("GET")) {
                    // Thread t = null;
                    if (requestURI.getPath().startsWith("/start")) {
                        msg = "starting proxy on port " + port + " outputting to " + dumpFileName;
                        // t = new Thread(new Runnable() {
                        // @Override
                        // public void run() {
                        start();
                        // }
                        // });
                    } else if (requestURI.getPath().startsWith("/stop")) {
                        msg = "stopping proxy and finalizing output in file " + dumpFileName;
                        // t = new Thread(new Runnable() {
                        // @Override
                        // public void run() {
                        stop();
                        // }
                        // });
                    } else if (requestURI.getPath().startsWith("/quit")) {
                        msg = "quitting proxy and finalizing output in file " + dumpFileName;
                        // t = new Thread(new Runnable() {
                        // @Override
                        // public void run() {
                        quit();
                        // }
                        // });
                    }
                    byte[] bytes = msg.getBytes();
                    Headers responseHeaders = exchange.getResponseHeaders();
                    responseHeaders.set("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream responseBody = exchange.getResponseBody();

                    responseBody.write(bytes);
                    responseBody.close();
                    // if (t != null) {
                    // t.start();
                    // }
                }
            }
        };

        server.createContext("/", handler);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port " + apiPort);
    }

    /**
     * Prints the usage of the program.
     */
    private static void usage() {
        System.err
                .println("Usage: java -jar proxy.jar port [\"proxy instruction\"] [ <JDBC Driver> <JDBC URL> <username> <password> ]");
        System.err.println("Where \'proxy instruction\' might look like:");
        System.err
                .println("'DIRECT' or 'PROXY server:port' or 'SOCKS server:port'");
        System.err.println("and the JDBC connection details might look like:");
        System.err
                .println("org.h2.Driver jdbc:h2:mem:webscarab3;DB_CLOSE_DELAY=-1 sa \"\"");
    }

    /**
     * Returns a proxy selector
     * 
     * @param proxy
     * @return
     */
    static ProxySelector getProxySelector(String proxy) {
        final java.net.Proxy upstream;
        if ("DIRECT".equals(proxy)) {
            upstream = java.net.Proxy.NO_PROXY;
        } else {
            java.net.Proxy.Type type = null;
            if (proxy.startsWith("PROXY ")) {
                type = java.net.Proxy.Type.HTTP;
            } else if (proxy.startsWith("SOCKS ")) {
                type = java.net.Proxy.Type.SOCKS;
            } else
                throw new IllegalArgumentException("Unknown Proxy type: "
                        + proxy);
            proxy = proxy.substring(6); // "SOCKS " or "PROXY "
            int c = proxy.indexOf(':');
            if (c == -1)
                throw new IllegalArgumentException("Illegal proxy address: "
                        + proxy);
            InetSocketAddress addr = new InetSocketAddress(proxy
                    .substring(0, c), Integer.parseInt(proxy.substring(c + 1)));
            upstream = new java.net.Proxy(type, addr);
        }
        ProxySelector ps = new ProxySelector() {

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                logger.info("Proxy connection to " + uri + " via " + sa
                        + " failed! " + ioe.getLocalizedMessage());
            }

            @Override
            public List<java.net.Proxy> select(URI uri) {
                return Arrays.asList(upstream);
            }
        };
        return ps;
    }

    static SSLContextSelector getSSLContextSelector()
            throws GeneralSecurityException, IOException {
        File ks = new File("auto_generated_ca.p12");
        String type = "PKCS12";
        char[] password = "password".toCharArray();
        String alias = "CA";
        if (ks.exists()) {
            try {
                return new AutoGeneratingContextSelector(ks, type, password,
                        password, alias);
            } catch (GeneralSecurityException e) {
                System.err.println("Error loading CA keys from keystore: "
                        + e.getLocalizedMessage());
            } catch (IOException e) {
                System.err.println("Error loading CA keys from keystore: "
                        + e.getLocalizedMessage());
            }
        }
        System.err.println("Generating a new CA");
        X500Principal ca = new X500Principal("cn=OWASP Custom CA for Tank,ou=Tank Custom CA,o=Tank,l=Tank,st=Tank,c=Tank");
        AutoGeneratingContextSelector ssl = new AutoGeneratingContextSelector(
                ca);
        try {
            ssl.save(ks, type, password, password, alias);
        } catch (GeneralSecurityException e) {
            System.err.println("Error saving CA keys to keystore: "
                    + e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println("Error saving CA keys to keystore: "
                    + e.getLocalizedMessage());
        }
        FileWriter pem = null;
        try {
            pem = new FileWriter("auto_generated_ca.pem");
            pem.write(ssl.getCACert());
        } catch (IOException e) {
            System.err.println("Error writing CA cert : "
                    + e.getLocalizedMessage());
        } finally {
            if (pem != null)
                pem.close();
        }
        return ssl;
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(new Option("config", true, "The name of the recording configuration file."));
        options.addOption(new Option("apiPort", true, "The port on which the api server should listen to."));
        options.addOption(new Option("help", false, "Print usage."));
        String configFile = null;
        int apiPort = 8008;
        CommandLineParser clp = new PosixParser();
        try {
            CommandLine cl = clp.parse(options, args);

            if (cl.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("CLITest <arguments>", options);
                System.exit(0);
            }

            if (cl.hasOption("config")) {
                configFile = cl.getOptionValue("config");
            } else {
                logger.info("Using default Configuration");
            }

            if (cl.hasOption("apiPort")) {
                apiPort = Integer.parseInt(cl.getOptionValue("apiPort"));
            } else {
                logger.info("Using " + apiPort + " as the default apiPort.");

            }
        } catch (ParseException e) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("CLITest <arguments>", options);
            System.exit(0);
        }
        ProxyConfiguration config = new CommonsProxyConfiguration(configFile);
        new Main(config, apiPort);

    }

    /**
     * @{inheritDoc
     */
    public void transactionProcessed(Transaction t, boolean filtered) {
        // don't know if we need to do something here
    }
}
