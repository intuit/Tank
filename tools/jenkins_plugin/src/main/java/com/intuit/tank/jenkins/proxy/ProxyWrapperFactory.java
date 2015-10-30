package com.intuit.tank.jenkins.proxy;

/*
 * #%L
 * Intuit Tank Jenkins Plugin
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import hudson.FilePath;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.intuit.tank.jenkins.callables.ProxyRequest;
import com.intuit.tank.jenkins.exceptions.TankJenkinsPluginException;
import com.intuit.tank.jenkins.printer.LogPrinter;
import com.intuit.tank.jenkins.publisher.ProxyPlugin.ProxyPluginDescriptor;

/**
 * Jobs are sent to remote machines which have their own classloader. Generally
 * because our proxy uses Streams and anonymous inner classes to function, we
 * can't serialize the proxy and send it by wire to our remote machines. This
 * singleton factory resides on each slave and exists solely to instantiate
 * proxies, and ensure uniqueness by mapping a proxy to the port that it's being
 * assigned to.
 * 
 * However, on the same machine, we need to also keep track of which build 'owns' which proxy
 * 
 * @author bfiola
 *
 */
public class ProxyWrapperFactory implements Serializable {
	private static final long serialVersionUID = 1L;

	private static ProxyWrapperFactory instance;
	private final Map<Integer, ProxyWrapper> proxies;

	protected ProxyWrapperFactory() {
		proxies = new HashMap<Integer, ProxyWrapper>();
	}

	private static ProxyWrapperFactory getInstance() {
		if (instance == null) {
			instance = new ProxyWrapperFactory();
		}
		return instance;
	}

	private void _startProxy(ProxyRequest request) {
		Integer port = request.getProxyPort();
		ProxyPluginDescriptor descriptor = request.getDescriptor();
		PrintStream logger = request.getLogger();
		String buildId = request.getBuildId();
		
		// we use the plugin descriptor to validate the port number when
		// it's initially configured - we can reuse this here to make sure
		// that the port is still valid. if not, it will throw an exception.
		try {
			descriptor.testPort(port);
		} catch(TankJenkinsPluginException e) {
			throw new RuntimeException(e);
		}

		// if a proxy doesn't already exist at this port, create it.
		// otherwise, throw an exception.
		if (proxies.get(port) == null) {
			LogPrinter.print("Proxy at port " + port
					+ " wasn't found, creating a new one for build " + buildId + ".", logger);
			ProxyWrapper proxy = new ProxyWrapper(request);
			proxies.put(port, proxy);
			proxy.startProxy();
		} else {
			ProxyWrapper proxy = proxies.get(port);
			throw new RuntimeException("Port " + port + " already in use by build " + proxy.getOwnerId());
		}
	}

	private void _stopProxy(ProxyRequest request) {
		Integer port = request.getProxyPort();
		PrintStream logger = request.getLogger();
		String buildId = request.getBuildId();
		
		if(proxies.get(port) != null) {
			ProxyWrapper proxy = proxies.get(port);
			if(buildId.equals(proxy.getOwnerId())) {
				LogPrinter.print("Found proxy at port " + port + ", belonging to build " + buildId + " - stopping it.",
						logger);
				proxy.stopProxy();
				proxies.remove(port);
			} else {
				throw new RuntimeException("Proxy exists at port " + port + " but belongs to build " + proxy.getOwnerId() + ", not build " + buildId + ". ");
			}
		} else {
			throw new RuntimeException("No proxy at " + port + ".");
		}
	}

	public static void startProxy(ProxyRequest request) {
		getInstance()._startProxy(request);
	}

	public static void stopProxy(ProxyRequest request) {
		getInstance()._stopProxy(request);
	}
}
