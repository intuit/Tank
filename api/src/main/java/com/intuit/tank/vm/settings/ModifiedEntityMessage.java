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

/**
 * ModifiedEntityMessage
 * 
 * @author dangleton
 * 
 */
public class ModifiedEntityMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private Class<?> entityClass;
    private int id;
    private ModificationType type;

    /**
     * @param entityClass
     * @param id
     * @param type
     */
    public ModifiedEntityMessage(Class<?> entityClass, int id, ModificationType type) {
        super();
        this.entityClass = entityClass;
        this.id = id;
        this.type = type;
    }

    /**
     * @return the entityClass
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the type
     */
    public ModificationType getType() {
        return type;
    }

}
