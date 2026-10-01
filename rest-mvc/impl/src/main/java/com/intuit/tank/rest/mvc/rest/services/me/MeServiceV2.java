/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.me;

import com.intuit.tank.rest.mvc.rest.models.AccountUpdate;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.ColumnPreferenceUpdate;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.TablePreferences;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;

import java.util.List;

/**
 * The signed-in user's own account. Every method requires a user caller (not anonymous, not the agent token).
 */
public interface MeServiceV2 {

    CurrentUser getCurrentUser();

    /**
     * Describes the given caller, who need not be the current request's caller (used right after login).
     */
    CurrentUser describe(TankPrincipal principal);

    CurrentUser updateAccount(AccountUpdate update);

    /**
     * Generates an API token, replacing any existing one. The token is only ever returned here.
     */
    ApiTokenResponse createApiToken();

    void deleteApiToken();

    TablePreferences getPreferences();

    TablePreferences updateTablePreferences(String table, List<ColumnPreferenceUpdate> updates);

    /**
     * Deletes the user's table preferences so the defaults apply again.
     */
    void resetPreferences();
}
