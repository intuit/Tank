/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * Which inputs the filter action editor shows for one action type and scope, following the JSF
 * {@code ScriptFilterActionBean}.
 *
 * @param actionType the action type name ({@code add}, {@code remove} or {@code replace})
 * @param scope      the scope value, as listed in the matching {@code *ActionScopes} filter option
 * @param key        whether the action takes a key
 * @param value      whether the action takes a value
 * @param onFail     whether the value is picked from the {@code onFailOptions} filter option instead
 * @param prefix     what goes in front of the value when it is saved
 */
public record FilterActionField(String actionType, String scope, boolean key, boolean value, boolean onFail,
                                ValuePrefix prefix) {

    public enum ValuePrefix {
        /** The value is saved as typed. */
        NONE,
        /** The value is saved after {@code =}. */
        ASSIGNMENT,
        /** The value is saved after a validation type picked from the {@code validationTypes} filter option. */
        VALIDATION
    }
}
