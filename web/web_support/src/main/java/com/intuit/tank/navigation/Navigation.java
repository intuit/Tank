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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * PageMap
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
@Named
public class Navigation implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<String, String> pageTitleMap = new HashMap<String, String>();
    private Map<String, SiteSection> pageSectionMap = new HashMap<String, SiteSection>();

    /**
     * Gets the page Title for the given View ID
     * 
     * @param viewId
     * @return The page title
     */
    @Nonnull
    public String getPageTitle(@Nonnull String viewId) {
        String ret = pageTitleMap.get(viewId);
        if (ret == null) {
            ret = findPageTitle(viewId);
            pageTitleMap.put(viewId, ret);
        }
        return ret;
    }

    /**
     * Get the section name for the given view ID
     * 
     * @param viewId
     * @return
     */
    @Nonnull
    public SiteSection getSiteSection(@Nonnull String viewId) {
        SiteSection ret = pageSectionMap.get(viewId);
        if (ret == null) {
            ret = findPageSection(viewId);
            pageSectionMap.put(viewId, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    @Nonnull
    public String getSection() {
        return getSiteSection(FacesContext.getCurrentInstance().getViewRoot().getViewId()).name();
    }

    /**
     * @param viewId
     * @return
     */
    private String findPageTitle(String viewId) {
        // TODO Auto-generated method stub
        return "";
    }

    private SiteSection findPageSection(String viewId) {
        return Arrays.stream(SiteSection.values()).filter(section -> section.isMatch(viewId)).findFirst().orElse(SiteSection.unknown);
    }
}
