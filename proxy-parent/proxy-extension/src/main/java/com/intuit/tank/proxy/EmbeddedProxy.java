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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import org.owasp.proxy.daemon.LoopAvoidingTargetedConnectionHandler;
import org.owasp.proxy.daemon.Proxy;
import org.owasp.proxy.daemon.ServerGroup;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
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
import com.intuit.tank.handler.ProxyDefaultHttpRequestHandler;
import com.intuit.tank.interceptor.ProxyBufferedMessageInterceptor;
import com.intuit.tank.proxy.config.ProxyConfiguration;
import com.intuit.tank.proxy.table.TransactionRecordedListener;
import com.intuit.tank.selector.TankProxySelector;


public class EmbeddedProxy implements TransactionRecordedListener {

	private static Logger logger = Logger.getLogger("org.owasp.proxy");

	private static char[] CA_PASSWORD = "password".toCharArray();
	private static String CA_TYPE = "PKCS12";
	private static String CA_ALIAS = "CA";

	private ProxyConfiguration config;
	private Proxy p;
	private Application application;

	static {
		System.setProperty("https.protocols", "TLSv1.2");
	}

	/**
     * 
     */

	public EmbeddedProxy(ProxyConfiguration config) {
		this.application = new Application(config);
		this.config = config;
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
				listen = new InetSocketAddress("localhost", config.getPort());
			} catch (NumberFormatException nfe) {
				return;
			}
			DefaultHttpRequestHandler drh = new ProxyDefaultHttpRequestHandler(
					getProxySelector());
			ServerGroup sg = new ServerGroup();
			sg.addServer(listen);
			drh.setServerGroup(sg);

			BufferedMessageInterceptor bmi = new ProxyBufferedMessageInterceptor();
			HttpRequestHandler rh = new BufferingHttpRequestHandler(
					new LoggingHttpRequestHandler(drh), bmi, 4 * 1024 * 1024,
					application);

			HttpProxyConnectionHandler hpch = new HttpProxyConnectionHandler(rh);
			
			TargetedConnectionHandler tch = new LoopAvoidingTargetedConnectionHandler(
					sg, new SSLConnectionHandler(getSSLContextSelector(), true,
							hpch));
			hpch.setConnectHandler(tch);
			
			TargetedConnectionHandler socks = new SocksConnectionHandler(tch,
					true);
			p = new Proxy(listen, socks, null);
			p.setSocketTimeout(0);
			p.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void stop() {
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

	/**
	 * Returns a proxy selector
	 * 
	 * @param proxy
	 * @return
	 */
	private ProxySelector getProxySelector() {
		ProxySelector ps = new TankProxySelector(java.net.Proxy.NO_PROXY);
		return ps;
	}

	/**
	 * Uses an AutoGeneratingContextSelector to generate a certificate authority
	 * but does not return it only because we're going to load it with the
	 * 'useExistingKeystore' method instead.
	 * 
	 * @param ks
	 *            File representing the certificate authority file we're
	 *            generating.
	 */
	public static void generateKeystore(File ks)
			throws GeneralSecurityException, CertificateEncodingException,
			IOException {
		X500Principal ca = new X500Principal(
				"cn=OWASP Custom CA for Tank,ou=Tank Custom CA,o=Tank,l=Tank,st=Tank,c=Tank");
		AutoGeneratingContextSelector ssl = null;
		try {
			ssl = new AutoGeneratingContextSelector(ca);
			ssl.save(ks, CA_TYPE, CA_PASSWORD, CA_PASSWORD, CA_ALIAS);
		} catch (GeneralSecurityException e) {
			System.err.println("Error saving CA keys to keystore: "
					+ e.getLocalizedMessage());
			throw e;
		} catch (IOException e) {
			System.err.println("Error saving CA keys to keystore: "
					+ e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * Attempts to load an existing certificate authority file, throws Exception
	 * on failure.
	 * 
	 * @param ks
	 * @return
	 */
	private AutoGeneratingContextSelector useExistingKeystore(File ks)
			throws GeneralSecurityException, IOException {
		try {
			return new AutoGeneratingContextSelector(ks, CA_TYPE, CA_PASSWORD,
					CA_PASSWORD, CA_ALIAS);
		} catch (GeneralSecurityException e) {
			System.err.println("Error loading CA keys from keystore: "
					+ e.getLocalizedMessage());
			throw e;
		} catch (IOException e) {
			System.err.println("Error loading CA keys from keystore: "
					+ e.getLocalizedMessage());
			throw e;
		}
	}

	private SSLContextSelector getSSLContextSelector()
			throws GeneralSecurityException, IOException {
		File ks = new File(config.getCertificateAuthorityPath());
		if (!ks.exists()) {
			generateKeystore(ks);
		}
		return useExistingKeystore(ks);
	}

	/**
	 * @{inheritDoc
	 */
	public void transactionProcessed(Transaction t, boolean filtered) {
		// don't know if we need to do something here
	}
}
