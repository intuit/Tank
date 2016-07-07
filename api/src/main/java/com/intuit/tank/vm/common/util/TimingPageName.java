/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common.util;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 * TimingPageName
 * 
 * @author dangleton
 * 
 */
public class TimingPageName {
    private static final Logger LOG = Logger.getLogger(TimingPageName.class);

    private Integer index;
    private String name;
    private String id;

    /**
     * @param pageName
     */
    public TimingPageName(String pageName) {
        super();
        String[] split = StringUtils.splitByWholeSeparatorPreserveAllTokens(pageName, ReportUtil.PAGE_NAME_SEPERATOR);
        if (split.length > 0 && !StringUtils.isBlank(split[0])) {
            this.id = split[0].trim();
        }
        if (split.length > 1 && !StringUtils.isBlank(split[1])) {
            this.name = split[1].trim();
        }
        if (name == null && id == null) {
            id = pageName.trim();
            name = pageName.trim();
        }
        if (id == null) {
            id = name;
        }
        if (name == null) {
            name = id;
        }
        if (split.length > 2) {
            String indStr = split[2].trim();
            if (NumberUtils.isDigits(indStr)) {
                try {
                    index = Integer.parseInt(indStr);
                } catch (NumberFormatException e) {
                    LOG.warn("Error parsing index " + indStr + ": " + e);
                }
            }

        }
    }

    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

}
