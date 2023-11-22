/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.converter;

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

import jakarta.annotation.Nonnull;

/**
 * ScriptStepContainer
 * 
 * @author dangleton
 * 
 */
public class ScriptStepHolder implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DELIMETER = ":";

    private String uuid;
    private String label;
    private int index;

    /**
     * @param uuid
     * @param label
     * @param index
     */
    public ScriptStepHolder(int index, String uuid, String label) {
        this.uuid = uuid;
        this.label = label;
        this.index = index;
    }

    public static ScriptStepHolder fromCannonicalForm(@Nonnull String s) {
        String[] split = s.split(DELIMETER);
        if (split.length < 3) {
            throw new IllegalArgumentException("String must be in cannonical form");
        }
        String label = split[2];
        if (split.length > 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < split.length; i++) {
                if (i > 2) {
                    sb.append(':');
                }
                sb.append(split[i]);
            }
            if (s.endsWith(":")) {
                sb.append(':');
            }
            label = sb.toString();
        }

        return new ScriptStepHolder(Integer.parseInt(split[0]), split[1], label);
    }

    public String toCanonicalForm() {
        return index + ScriptStepHolder.DELIMETER + uuid + ScriptStepHolder.DELIMETER + label;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

}
