/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

/**
 * WhereClauseContainer
 * 
 * @author dangleton
 * 
 */
public class NamedParameter {

    private String name;
    private String field;
    private Object value;

    /**
     * @param name
     * @param field
     * @param value
     */
    public NamedParameter(String field, String name, Object value) {
        super();
        this.name = name;
        this.field = field;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

}
