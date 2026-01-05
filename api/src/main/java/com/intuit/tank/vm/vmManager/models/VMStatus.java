package com.intuit.tank.vm.vmManager.models;

/*
 * #%L
 * Cloud Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

public enum VMStatus implements Serializable {
    unknown,
    starting,
    pending,
    ready,
    rampPaused,
    rebooting,
    running,
    stopping,
    stopped,
    shutting_down,
    terminated,
    replaced;  // replaced by AgentWatchdog due to failure to report back

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
            // Gracefully handle unknown values (e.g., from older/newer clients with different enum versions)
            return VMStatus.unknown;
        }
    }

}
