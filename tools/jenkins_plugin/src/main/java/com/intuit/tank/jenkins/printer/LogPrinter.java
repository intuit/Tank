package com.intuit.tank.jenkins.printer;

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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Since Jenkins only gives us a PrintStream to write to the job console during
 * a build, this class gives us an ability to conveniently log information to
 * this job console.
 * 
 * @author bfiola
 *
 */
public class LogPrinter {

	/**
	 * This method will take the typical output of printStackTrace and write it
	 * to the job console
	 * 
	 * @param e
	 * 		Exception being thrown.
	 * @param logger
	 * 		The PrintStream that points to the job console.
	 */
	public static void print(Exception e, PrintStream logger) {
		if(logger != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.println(sw.toString());
		}
	}

	/**
	 * This method will take a String that we intend to log and format it as the following:
	 * 
	 * <Class.getSimpleName()> : <Method Name> (<line number>) : <message to print>
	 * 
	 * @param s
	 * 		The message that will be printed to the log.
	 * @param logger
	 * 		The PrintStream that points to the job console.
	 */
	public static void print(String s, PrintStream logger) {
		if(logger != null) {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			StackTraceElement parentMethod = stack[2];
			String className = parentMethod.getClassName();
			int lastPeriod = className.lastIndexOf(".");
			String truncatedClassName = className.substring(lastPeriod + 1,
					className.length());
			logger.println(truncatedClassName + " : "
					+ parentMethod.getMethodName() + "("
					+ parentMethod.getLineNumber() + ") : " + s);
		}
	}
}
