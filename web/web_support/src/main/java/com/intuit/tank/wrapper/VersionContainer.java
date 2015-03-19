/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.wrapper;

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
import java.util.List;

/**
 * VersionContainer
 * 
 * @author dangleton
 * 
 */
public class VersionContainer<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> entities;
    private int version;

    /**
     * @param entities
     * @param verison
     */
    public VersionContainer(List<T> entities, int version) {
        super();
        this.entities = entities;
        this.version = version;
    }

    /**
     * @return the entities
     */
    public List<T> getEntities() {
        return entities;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

}
