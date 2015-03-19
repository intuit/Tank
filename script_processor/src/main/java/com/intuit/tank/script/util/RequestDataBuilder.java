/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
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

import com.intuit.tank.project.RequestData;

/**
 * DataItemBuilder
 * 
 * @author dangleton
 * 
 */
public class RequestDataBuilder {

    private StringBuilder sb = new StringBuilder();
    private String value;
    private String type;

    /**
     * @param type
     */
    public RequestDataBuilder(String type) {
        this.type = type;
    }

    /**
     * @param sb
     *            the sb to set
     */
    public void addPathElement(@Nonnull String path) {
        appentPath(sb, path);
    }

    public RequestData build(String lastElement, String value) {
        StringBuilder s = new StringBuilder(sb.toString());
        appentPath(s, lastElement);
        return new RequestData(s.toString(), value, type);
    }

    private static final void appentPath(StringBuilder stringBuilder, String path) {
        if (!path.startsWith("/")) {
            stringBuilder.append("/");
        }
        stringBuilder.append(path);
    }

    public String getPath() {
        return sb.toString();
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

    public RequestDataBuilder copy() {
        RequestDataBuilder ret = new RequestDataBuilder(type);
        ret.value = value;
        ret.sb.append(sb.toString());
        return ret;
    }

}
