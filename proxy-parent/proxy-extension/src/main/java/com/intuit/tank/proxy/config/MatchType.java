/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.config;

/*
 * #%L
 * proxy-extension
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
 * MatchType
 * 
 * @author dangleton
 * 
 */
public enum MatchType {
    contains,
    equals,
    startsWith,
    endsWith,
    notContains,
    notEquals,
    notStartsWith,
    notEndsWith,
}
