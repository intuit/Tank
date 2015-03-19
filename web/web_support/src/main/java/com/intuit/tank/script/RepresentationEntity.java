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

public class RepresentationEntity {

    private String representation;
    private String value;

    public RepresentationEntity(String representation, String value) {
        this.representation = representation;
        this.value = value;
    }

    /**
     * @return the representation
     */
    public String getRepresentation() {
        return representation;
    }

    /**
     * @param representation
     *            the representation to set
     */
    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
