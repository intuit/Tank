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

import org.jenkinsci.remoting.RoleChecker;

import com.intuit.tank.jenkins.printer.LogPrinter;
import com.intuit.tank.jenkins.proxy.ProxyWrapperFactory;

/**
 * This callable will send instructions to a remote machine to stop a proxy (if
 * it exists).
 * 
 * @author bfiola
 *
 */
public class StopProxyCallable extends AbstractProxyCallable {
	private static final long serialVersionUID = 1L;

	protected StopProxyCallable() {
		super();
	}

	public StopProxyCallable(ProxyRequest request) {
		super(request);
	}

	@Override
	public Object call() {
		LogPrinter.print("Stopping proxy", request.getLogger());
		ProxyWrapperFactory.stopProxy(request);
		return null;
	}

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
	}
}
