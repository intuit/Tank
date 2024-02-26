/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.navigation;

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

import jakarta.annotation.Nonnull;

/**
 * SiteSection. Represents a site section. suitable for navigation
 * 
 * @author dangleton
 * 
 */
public enum SiteSection {

    projects("\\/projects.*"),
    scripts("\\/scripts.*"),
    datafiles("\\/datafiles.*"),
    filters("\\/filters.*"),
    reports("\\/report.*"),
    tools("\\/tools.*"),
    agents("\\/agents.*"),
    admin("\\/admin.*"),
    preferences("\\/preferences.*"),
    unknown(".*");

    private String match;

    /**
     * @param match
     */
    private SiteSection(String match) {
        this.match = match;
    }

    /**
     * tests whether the viewId matches the Section
     * 
     * @param viewId
     *            the view Id to test
     * @return true if is a match
     */
    public boolean isMatch(@Nonnull String viewId) {
        return viewId.matches(match);
    }

}
