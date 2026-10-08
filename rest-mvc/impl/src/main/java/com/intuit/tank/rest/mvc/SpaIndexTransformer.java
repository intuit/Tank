/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Points the React app's {@code <base href="/app/">} at this deployment's context path, e.g.
 * {@code /tank/app/}. The build references its assets relative to the base, so one build works
 * whatever context path the WAR is deployed under.
 */
public class SpaIndexTransformer implements ResourceTransformer {

    static final String BASE_PLACEHOLDER = "<base href=\"/app/\"";

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain chain)
            throws IOException {
        resource = chain.transform(request, resource);
        String contextPath = request.getContextPath();
        if (!SpaResourceResolver.INDEX.equals(resource.getFilename()) || contextPath == null || contextPath.isEmpty()) {
            return resource;
        }
        String html = resource.getContentAsString(StandardCharsets.UTF_8);
        String base = "<base href=\"" + HtmlUtils.htmlEscape(contextPath + "/app/") + "\"";
        return new TransformedResource(resource, html.replace(BASE_PLACEHOLDER, base).getBytes(StandardCharsets.UTF_8));
    }
}
