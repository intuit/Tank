package com.intuit.tank.jenkins.constants;

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

public final class StringConstants {
	public static final class DisplayName {
		public static final String GLOBAL_PLUGIN_CONFIGURATION = "Tank Proxy Plugin Configuration";
		public static final String PERSIST_SCRIPT = "Tank Proxy Plug-in";
		public static final String PROXY_PORT = "Proxy Port";
		public static final String BUILD_CONFIGURATION = "Build";
		public static final String SYSTEM_CONFIGURATION = "Global";
	}
	
	public static final class ErrorMessages {
		public static final String UNIQUE_PORTS = "Ports must be unique.";
		public static final String MUST_BE_INTEGER = "Please enter an integer.";
		public static final String PORT_INTEGER_RANGE = "Please enter an integer within the range of 1-65535.";
		public static final String PORT_IN_USE = "Port is in use.";

		public static String missingConfigurationProperty(String property, String environment) {
			return "Missing " + property + " in " + environment + " configuration - value is not set or " + environment + " configuration has not been saved.";
		}
	}
	
}
