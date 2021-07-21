/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.settings;

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

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * SelectableItem represents a selectable item with a value and a display name.
 * 
 * @author dangleton
 * 
 */
public class SelectableItem implements Comparable<SelectableItem>, Serializable {

    private static final long serialVersionUID = 1L;
    private String displayName;
    private String value;

    /**
     * @param displayName
     * @param value
     */
    public SelectableItem(@Nonnull String displayName, @Nonnull String value) {
        this.displayName = displayName;
        this.value = value;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(SelectableItem o) {
        return displayName.compareTo(o.displayName);
    }

}
