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
 * Operation
 * 
 * @author dangleton
 * 
 */
public enum Operation {

    EQUALS("=", ""),
    IN("in (", ")"),
    NOT("not", ""),
    NOT_NULL("IS NOT NULL", ""),
    NULL("IS NULL", ""),
    NOT_IN("not in (", ")"),
    GREATER_THAN(">", ""),
    GREATER_THAN_OR_EQUALS(">=", ""),
    LESS_THAN_OR_EQUALS("<=", ""),
    LESS_THAN("<", "");
    private String representation;
    private String ending;

    /**
     * @param representation
     */
    private Operation(String representation, String ending) {
        this.representation = representation;
        this.ending = ending;
    }

    /**
     * @return the representation
     */
    public String getRepresentation() {
        return representation;
    }

    /**
     * @return the ending
     */
    public String getEnding() {
        return ending;
    }

}
