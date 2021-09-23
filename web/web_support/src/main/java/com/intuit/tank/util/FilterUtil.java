package com.intuit.tank.util;

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

import java.util.Locale;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * FilterUtil. Provides non-case sensitive filtering for primefaces v5
 * 
 * @author dangleton
 * 
 */
@Named
public class FilterUtil {

    /**
     * Filters by contains
     * @param value
     * @param filter
     * @param locale
     * @return
     */
    public boolean contains(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (StringUtils.isBlank(filterText)) {
            return true;
        }

        if (value == null) {
            return false;
        }
        return StringUtils.containsIgnoreCase(value.toString(), filterText);
    }

    public boolean equals(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (StringUtils.isBlank(filterText)) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(value.toString(), filterText);
    }
}
