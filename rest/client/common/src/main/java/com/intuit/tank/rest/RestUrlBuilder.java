/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.rest;

/*
 * #%L
 * Rest Client Common
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
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

/**
 * RestUrlBuilder
 * 
 * @author dangleton
 * 
 */
public class RestUrlBuilder {

    private String baseUrl;

    /**
     * @param baseUrl
     */
    public RestUrlBuilder(@Nonnull String baseUrl) {
        this.baseUrl = StringUtils.removeEnd(baseUrl, "/");
    }

    /**
     * Builds the url with the specified methodName.
     * 
     * @param methodName
     *            the methodName
     * @return the url.
     */
    @Nonnull
    public String buildUrl(@Nonnull String methodName) {
        return buildUrl(methodName, (Object[]) null);
    }

    /**
     * Builds the url with the specified methodName and parameter.
     * 
     * @param methodName
     *            the methodName
     * @param parameter
     *            the parameter value
     * @return the url.
     */
    @Nonnull
    public String buildUrl(@Nullable String methodName, @Nullable Object... parameters) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (methodName != null) {
            if (!methodName.startsWith("/")) {
                sb.append('/');
            }
            sb.append(StringUtils.removeEnd(methodName, "/"));

        }
        if (parameters != null) {
            for (Object parameter : parameters) {
                if (parameter != null) {
                    String s = parameter.toString();
                    if (!s.startsWith("/")) {
                        sb.append('/');
                    }
                    sb.append(s);
                }
            }
        }
        return sb.toString();
    }

}
