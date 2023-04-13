/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.common;

import java.io.Serializable;

public enum VMStatus implements Serializable {
    unknown,
    starting,
    pending,
    rampPaused,
    rebooting,
    running,
    stopping,
    stopped,
    shutting_down,
    terminated;

    public static final VMStatus fromString(String value) {
        VMStatus ret = null;
        if ("shutting-down".equals(value)) {
            ret = VMStatus.shutting_down;
        } else {
            ret = VMStatus.valueOf(value);
        }
        return ret != null ? ret : VMStatus.unknown;
    }

}
