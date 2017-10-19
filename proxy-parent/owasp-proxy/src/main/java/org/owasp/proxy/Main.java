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

package org.owasp.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;
import javax.sql.DataSource;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.owasp.proxy.daemon.LoopAvoidingTargetedConnectionHandler;
import org.owasp.proxy.daemon.Proxy;
import org.owasp.proxy.daemon.ServerGroup;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.client.HttpClient;
import org.owasp.proxy.http.dao.JdbcMessageDAO;
import org.owasp.proxy.http.server.BufferedMessageInterceptor;
import org.owasp.proxy.http.server.BufferingHttpRequestHandler;
import org.owasp.proxy.http.server.ConversationServiceHttpRequestHandler;
import org.owasp.proxy.http.server.DefaultHttpRequestHandler;
import org.owasp.proxy.http.server.HttpProxyConnectionHandler;
import org.owasp.proxy.http.server.HttpRequestHandler;
import org.owasp.proxy.http.server.LoggingHttpRequestHandler;
import org.owasp.proxy.http.server.RecordingHttpRequestHandler;
import org.owasp.proxy.socks.SocksConnectionHandler;
import org.owasp.proxy.ssl.AutoGeneratingContextSelector;
import org.owasp.proxy.ssl.SSLConnectionHandler;
import org.owasp.proxy.ssl.SSLContextSelector;
import org.owasp.proxy.util.TextFormatter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Main {

    private static Logger logger = Logger.getLogger("org.owasp.proxy");

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
    private static ProxySelector getProxySelector(String proxy) {
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

    private static DataSource createDataSource(String driver, String url,
            String username, String password) throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private static SSLContextSelector getSSLContextSelector()
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
        Security.addProvider(new BouncyCastleProvider()); // add
        logger.setUseParentHandlers(false);
        Handler ch = new ConsoleHandler();
        ch.setFormatter(new TextFormatter());
        logger.addHandler(ch);

        if (args == null
                || (args.length != 1 && args.length != 2 && args.length != 5 && args.length != 6)) {
            usage();
            return;
        }
        InetSocketAddress listen;
        try {
            int port = Integer.parseInt(args[0]);
            listen = new InetSocketAddress("localhost", port);
        } catch (NumberFormatException nfe) {
            usage();
            return;
        }
        String proxy = "DIRECT";
        if (args.length == 2 || args.length == 6) {
            proxy = args[1];
        }
        DataSource dataSource = null;
        if (args.length == 5) {
            dataSource = createDataSource(args[1], args[2], args[3], args[4]);
        } else if (args.length == 6) {
            dataSource = createDataSource(args[2], args[3], args[4], args[5]);
        }

        final ProxySelector ps = getProxySelector(proxy);

        DefaultHttpRequestHandler drh = new DefaultHttpRequestHandler() {
            @Override
            protected HttpClient createClient() {
                HttpClient client = super.createClient();
                client.setProxySelector(ps);
                client.setSoTimeout(0);
                return client;
            }
        };
        ServerGroup sg = new ServerGroup();
        sg.addServer(listen);
        drh.setServerGroup(sg);
        HttpRequestHandler rh = drh;
        rh = new LoggingHttpRequestHandler(rh);

        if (dataSource != null) {
            JdbcMessageDAO dao = new JdbcMessageDAO();
            dao.setDataSource(dataSource);
            dao.createTables();
            rh = new RecordingHttpRequestHandler(dao, rh, 4 * 1024 * 1024);
            rh = new ConversationServiceHttpRequestHandler("127.0.0.2", dao, rh);
        }
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
        rh = new BufferingHttpRequestHandler(rh, bmi, 4 * 1024 * 1024);

        HttpProxyConnectionHandler hpch = new HttpProxyConnectionHandler(rh);
        SSLContextSelector cp = getSSLContextSelector();
        TargetedConnectionHandler tch = new SSLConnectionHandler(cp, true, hpch);
        tch = new LoopAvoidingTargetedConnectionHandler(sg, tch);
        hpch.setConnectHandler(tch);
        TargetedConnectionHandler socks = new SocksConnectionHandler(tch, true);
        Proxy p = new Proxy(listen, socks, null);
        p.setSocketTimeout(0);
        p.start();

        System.out.println("Listener started on " + listen);
        System.out.println("Press Enter to terminate");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        p.stop();
        System.out.println("Terminated");
        System.exit(0);
    }
}
