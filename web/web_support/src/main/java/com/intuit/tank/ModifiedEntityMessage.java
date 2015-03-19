/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import javax.annotation.Nonnull;

/**
 * ModifiedEntityMessage
 * 
 * @author dangleton
 * 
 */
public class ModifiedEntityMessage<T> {

    private T modified;
    private Object modifier;

    /**
     * @param modified
     *            the modified Object
     * @param modifier
     *            the object doing the modification
     */
    public ModifiedEntityMessage(@Nonnull T modified, @Nonnull Object modifier) {
        super();
        this.modified = modified;
        this.modifier = modifier;
    }

    /**
     * @return the modified
     */
    public T getModified() {
        return modified;
    }

    /**
     * @return the modifier
     */
    public Object getModifier() {
        return modifier;
    }

}
