/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    /** Where the tank-web-react jar puts the React build */
    static final String SPA_LOCATION = "classpath:/META-INF/resources/app/";
    /** The JSF UI, which the React app replaced at the root */
    static final String CLASSIC_FOLDER = "/classic/";
    /** Folders of the war served as they are: the JSF UI's static files and the docs a release build adds */
    static final List<String> STATIC_FOLDERS = List.of(CLASSIC_FOLDER, "/docs/", "/apidocs/", "/restdocs/");

    @Autowired
    private ServletContext context;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // the resource handler can't serve an empty path, so the app's root forwards to its index.html
        registry.addViewController("/").setViewName("forward:/" + SpaResourceResolver.INDEX);
        registry.addViewController("/classic").setViewName("redirect:" + CLASSIC_FOLDER);
        registry.addViewController(CLASSIC_FOLDER).setViewName("forward:" + CLASSIC_FOLDER + "login.jsf");
        Set<String> views = context.getResourcePaths(CLASSIC_FOLDER);
        for (String view : views == null ? Set.<String>of() : views) {
            if (!view.endsWith("/")) {
                continue; // a page, not a section
            }
            registry.addViewController(view.substring(0, view.length() - 1)).setViewName("redirect:" + view);
            registry.addViewController(view).setViewName("forward:" + view + "index.html");
        }
    }

    /**
     * The React app at the root, for any path nothing else claims. Asset names carry a content hash, so
     * they are cached for a year; {@code index.html} is revalidated on every load so a new deployment is
     * picked up.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (String folder : STATIC_FOLDERS) {
            registry.addResourceHandler(folder + "**")
                    .addResourceLocations(folder)
                    .resourceChain(false)
                    .addResolver(new StaticResourceResolver());
        }
        registry.addResourceHandler("/assets/**")
                .addResourceLocations(SPA_LOCATION + "assets/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable());
        registry.addResourceHandler("/**")
                .addResourceLocations(SPA_LOCATION)
                .setCacheControl(CacheControl.noCache())
                .resourceChain(false)
                .addResolver(new SpaResourceResolver())
                .addTransformer(new SpaIndexTransformer());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
        registry.addInterceptor(new Utf8Interceptor());
    }

    /** Serves the war's static files, but not the JSF page sources, which are only reached as .jsf */
    static class StaticResourceResolver extends PathResourceResolver {
        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            if (resourcePath.endsWith(".xhtml") || resourcePath.endsWith(".jspf")) {
                return null;
            }
            return super.getResource(resourcePath, location);
        }
    }

    /**
     * Sends the React app as UTF-8. Otherwise rendering the forward to index.html sets the response
     * locale, Tomcat derives ISO-8859-1 from it, and that header charset overrides the page's
     * {@code <meta charset>}; the stylesheets inherit it and the icon font's glyphs come out garbled.
     * Only the static files and the forwards to them are touched, so the API's responses keep their own.
     */
    static class Utf8Interceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if (handler instanceof ResourceHttpRequestHandler || handler instanceof ParameterizableViewController) {
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            }
            return true;
        }
    }
}
