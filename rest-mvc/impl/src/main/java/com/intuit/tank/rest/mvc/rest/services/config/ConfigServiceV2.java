/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.config;

import com.intuit.tank.rest.mvc.rest.models.UiOptions;

import java.util.List;

/**
 * Reference data for the UI. Every method requires a user caller.
 */
public interface ConfigServiceV2 {

    UiOptions getOptions();

    /**
     * @return the names of all active users, sorted, for owner pickers
     */
    List<String> getUserNames();
}
