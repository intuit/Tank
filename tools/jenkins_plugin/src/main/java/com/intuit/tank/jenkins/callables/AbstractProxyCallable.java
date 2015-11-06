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

import hudson.remoting.Callable;

import java.io.Serializable;

import org.jenkinsci.remoting.RoleChecker;

/**
 * All abstract proxy callables inherit from this class - this class will store
 * the ProxyRequest that we're making and pass it to the remote machine.
 */
abstract class AbstractProxyCallable implements
		Callable<Object, RuntimeException>, Serializable {
	private static final long serialVersionUID = 1L;

	protected final ProxyRequest request;

	protected AbstractProxyCallable(ProxyRequest request) {
		this.request = request;
	}

	protected AbstractProxyCallable() {
		this.request = null;
	}

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
	}

	abstract public Object call();
}
