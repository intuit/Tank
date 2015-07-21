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

import hudson.remoting.Channel;

import com.intuit.tank.jenkins.printer.LogPrinter;
import com.intuit.tank.jenkins.proxy.ProxyWrapperFactory;

/**
 * This callable will send an instruction to our remote machine to start a
 * proxy.
 * 
 * @author bfiola
 *
 */
public class CreateProxyCallable extends AbstractProxyCallable {
	private static final long serialVersionUID = 1L;

	protected CreateProxyCallable() {
		super();
	}

	public CreateProxyCallable(ProxyRequest request) {
		super(request);
	}

	@Override
	public Object call() {
		LogPrinter.print("Starting proxy", request.getLogger());
		ProxyWrapperFactory.startProxy(request);
		return null;
	}
}
