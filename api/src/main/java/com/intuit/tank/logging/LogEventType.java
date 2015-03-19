package com.intuit.tank.logging;

/*
 * #%L
 * Intuit Tank Api
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
 * LogEventType enum represents the diferent categories that log events from agents can occur
 * 
 * @author dangleton
 * 
 */
public enum LogEventType {
    System,
    Validation,
    Script,
    Informational,
    Http,
    IO,
    Initialization,
    Other

}
