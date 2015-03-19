/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.lang.StringUtils;

/**
 * ComponentUtil
 * 
 * @author dangleton
 * 
 */
public class ComponentUtil {

    /**
     * extracs the string after the last occurence of the seperator character ':'
     * 
     * @param clientId
     * @return
     */
    public static String extractId(String clientId) {
        if (StringUtils.contains(clientId, ':')) {
            clientId = StringUtils.substringAfterLast(clientId, ":");
        }
        return clientId;
    }
}
