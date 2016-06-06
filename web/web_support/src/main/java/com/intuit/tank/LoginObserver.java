package com.intuit.tank;

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

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.picketlink.idm.model.basic.User;

import com.intuit.tank.auth.Authenticated;

public class LoginObserver {

    @Inject
    private PreferencesBean prefs;

    /**
     * 
     * @param user
     */
    public void observeLogin(@Observes @Authenticated User user) {
        prefs.init(user.getId());
    }
}
