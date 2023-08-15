/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients.util;


import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
     * @param parameters
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
