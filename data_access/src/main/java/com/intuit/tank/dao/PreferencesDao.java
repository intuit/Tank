/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import javax.annotation.Nonnull;

import com.intuit.tank.project.Preferences;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class PreferencesDao extends OwnableDao<Preferences> {

    /**
     * @param entityClass
     */
    public PreferencesDao() {
        super();
    }

    public Preferences getForOwner(@Nonnull String owner) {
        Preferences ret = null;
        List<Preferences> listForOwner = listForOwner(owner);
        if (listForOwner.size() > 0) {
            ret = listForOwner.get(0);
        }
        return ret;
    }

}
