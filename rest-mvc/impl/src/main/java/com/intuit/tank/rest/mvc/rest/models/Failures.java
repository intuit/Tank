/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import com.intuit.tank.vm.vmManager.models.ValidationStatus;

/**
 * Validation failure counts reported by agents.
 */
public record Failures(int total, int aborts, int gotos, int kills, int skips, int skipGroups, int restarts) {

    public static final Failures NONE = new Failures(0, 0, 0, 0, 0, 0, 0);

    public static Failures of(ValidationStatus status) {
        if (status == null) {
            return NONE;
        }
        return new Failures(status.getTotal(), status.getValidationAborts(), status.getValidationGotos(),
                status.getValidationKills(), status.getValidationSkips(), status.getValidationSkipGroups(),
                status.getValidationRestarts());
    }

    public Failures plus(Failures other) {
        return new Failures(total + other.total, aborts + other.aborts, gotos + other.gotos, kills + other.kills,
                skips + other.skips, skipGroups + other.skipGroups, restarts + other.restarts);
    }
}
