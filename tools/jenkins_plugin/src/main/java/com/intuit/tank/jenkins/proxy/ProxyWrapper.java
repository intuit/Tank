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

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.intuit.tank.conversation.Request;
import com.intuit.tank.jenkins.callables.ProxyRequest;
import com.intuit.tank.jenkins.printer.LogPrinter;
import com.intuit.tank.proxy.EmbeddedProxy;
import com.intuit.tank.proxy.config.FixedProxyConfiguration;
import com.intuit.tank.proxy.config.ProxyConfiguration;

/**
 * This is a 'wrapper' class for the Tank Proxy Recorder. It allows us to more
 * simply interact with our proxy.
 * 
 * @author bfiola
 *
 */
public class ProxyWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private EmbeddedProxy proxy;
	private ProxyConfiguration config;
	
	private String ownerId;
	private Integer proxyPort;
	private FilePath workspacePath;
	private PrintStream logger;
	
	public ProxyWrapper(ProxyRequest request) {
		this.logger = request.getLogger();
		this.ownerId = request.getBuildId();
		this.proxyPort = request.getProxyPort();
		this.workspacePath = request.getWorkspacePath();
	}
	
	public String getOwnerId() {
		return this.ownerId;
	}

	private void initializeProxy() {
		Security.addProvider(new BouncyCastleProvider());

		File certAuthority = new File(workspacePath + "/auto_generated_ca.p12");

		if (!certAuthority.exists()) {
			generateCertificate(certAuthority);
		}
		
		File tankScript = new File(workspacePath + "/tank-script.xml");
		if(tankScript.exists()) {
			LogPrinter.print("Tank Script found in workspace, deleting", logger);
			boolean deleted = tankScript.delete();
			LogPrinter.print("Tank Script Deleted? " + deleted, logger);
		}
		
		FixedProxyConfiguration config = new FixedProxyConfiguration(proxyPort,
				tankScript.getAbsolutePath());
		config.setCertificateAuthorityPath(certAuthority.getAbsolutePath());
		this.config = config;
		proxy = new EmbeddedProxy(config);
	}

	public void startProxy() {
		try {
			initializeProxy();
			Thread.currentThread().setContextClassLoader(
					Request.class.getClassLoader());
			LogPrinter.print("Proxy received start request - starting.", logger);
			proxy.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stopProxy() {
		try {
			LogPrinter.print("Proxy received stop request - stopping.", logger);
			proxy.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public File getOutputFile() {
		return new File(config.getOutputFile());
	}

	public static boolean generateCertificate(File filePath) {
		try {
			EmbeddedProxy.generateKeystore(filePath);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
