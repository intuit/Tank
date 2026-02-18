/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.agent.models;

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
        if (value == null || value.isEmpty()) {
            return VMStatus.unknown;
        }
        if ("shutting-down".equals(value)) {
            return VMStatus.shutting_down;
        }
        try {
            return VMStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            // Gracefully handle unknown values (e.g., 'replaced' from controller which agent doesn't need)
            return VMStatus.unknown;
        }
    }

}
