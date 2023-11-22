/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.io.Serializable;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;

/**
 * DirtyTracker
 * 
 * @author dangleton
 * 
 */
@Named
@ConversationScoped
public class ChangeTracker implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean dirty;

    /**
     * @return the dirty
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * @param dirty
     *            the dirty to set
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    /**
     * resets the dirtyFlag
     */
    public void reset() {
        this.dirty = false;
    }

}
