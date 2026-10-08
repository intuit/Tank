/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Serves the React app's files, and its {@code index.html} for any other path so that client-side
 * routes such as {@code /app/projects/12} load the app. A missing path that looks like a file
 * (its last segment has a dot) stays a 404, so a stale script reference fails visibly.
 */
public class SpaResourceResolver extends PathResourceResolver {

    static final String INDEX = "index.html";

    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        if (resourcePath.isEmpty() || resourcePath.endsWith("/")) {
            return super.getResource(INDEX, location);
        }
        Resource resource = super.getResource(resourcePath, location);
        if (resource != null || looksLikeFile(resourcePath)) {
            return resource;
        }
        return super.getResource(INDEX, location);
    }

    static boolean looksLikeFile(String resourcePath) {
        return resourcePath.substring(resourcePath.lastIndexOf('/') + 1).contains(".");
    }
}
