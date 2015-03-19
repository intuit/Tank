package com.intuit.tank.script.replace;

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

import com.intuit.tank.search.script.Section;

public class ReplaceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String after;
    private Section section;

    private String key;
    private String value;

    /**
     * @return the section
     */
    public Section getSection() {
        return section;
    }

    /**
     * @param section
     *            the section to set
     */
    public void setSection(Section section) {
        this.section = section;
    }

    /**
     * @return the before
     */
    public String getValue() {
        return value;
    }

    /**
     * @param before
     *            the before to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the after
     */
    public String getAfter() {
        return after;
    }

    /**
     * @param after
     *            the after to set
     */
    public void setAfter(String after) {
        this.after = after;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * helper method to display field that is up for replacement
     * 
     * @param replaceMode
     * @return the proper field (key or value)
     */
    public String getReplaced(ReplaceMode replaceMode) {
        if (replaceMode == ReplaceMode.KEY) {
            return this.key;
        } else {
            return this.value;
        }
    }
}
