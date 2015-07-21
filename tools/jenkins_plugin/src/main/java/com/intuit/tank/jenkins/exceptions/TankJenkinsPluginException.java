package com.intuit.tank.jenkins.exceptions;

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

/**
 * When an exception gets thrown that's directly related to the function of this
 * plugin, it is a TankJenkinsPluginException
 * 
 * @author bfiola
 *
 */
public class TankJenkinsPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public TankJenkinsPluginException(String message) {
		super(message);
	}

	public TankJenkinsPluginException(Exception e) {
		super(e);
	}

	public TankJenkinsPluginException(String message, Exception e) {
		super(message, e);
	}

}
