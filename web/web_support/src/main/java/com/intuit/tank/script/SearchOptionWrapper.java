package com.intuit.tank.script;

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


public class SearchOptionWrapper {

    private boolean selected = false;
    private Section value;

    public SearchOptionWrapper(Section value) {
        this.value = value;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the value
     */
    public Section getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Section value) {
        this.value = value;
    }

    public String getLabel() {
        return value.getDisplay();
    }

}
