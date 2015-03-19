/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.util;

/*
 * #%L
 * Rest Service Common Classes
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.ws.rs.core.CacheControl;

/**
 * ResponseUtil
 * 
 * @author dangleton
 * 
 */
public class ResponseUtil {

    private ResponseUtil() {
        // empty private constructor to implement util pattern
    }

    public static final CacheControl getNoStoreCacheControl() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setNoStore(true);
        cc.setMaxAge(0);
        cc.setSMaxAge(0);
        cc.setPrivate(true);
        return cc;
    }
}
