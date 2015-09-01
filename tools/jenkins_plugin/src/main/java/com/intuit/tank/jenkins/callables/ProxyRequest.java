package com.intuit.tank.jenkins.callables;

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
import hudson.model.BuildListener;

import java.io.PrintStream;
import java.io.Serializable;

import com.intuit.tank.jenkins.publisher.ProxyPlugin.ProxyPluginDescriptor;

/**
 * This class serves as a bundle of the things we need to start a proxy on a
 * remote machine.
 */
public class ProxyRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private BuildListener listener;
	private Integer proxyPort;
	private String buildId;
	private FilePath workspacePath;
	private ProxyPluginDescriptor descriptor;

	public ProxyRequest(BuildListener listener, Integer proxyPort, String buildId,
			FilePath workspacePath, ProxyPluginDescriptor descriptor) {
		this.listener = listener;
		this.proxyPort = proxyPort;
		this.buildId = buildId;
		this.workspacePath = workspacePath;
		this.descriptor = descriptor;
	}

	public PrintStream getLogger() {
		return listener.getLogger();
	}

	public Integer getProxyPort() {
		return proxyPort;
	}
	
	public String getBuildId() {
		return buildId;
	}

	public FilePath getWorkspacePath() {
		return workspacePath;
	}

	public ProxyPluginDescriptor getDescriptor() {
		return descriptor;
	}
}
