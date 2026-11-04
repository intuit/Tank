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
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    /** Where the tank-web-react jar puts the React build */
    static final String SPA_LOCATION = "classpath:/META-INF/resources/app/";
    static final String SPA_FOLDER = "/app/";

    @Autowired
    private ServletContext context;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        List<String> views = new ArrayList<String>(context.getResourcePaths("/"));
        registry.addViewController("/").setViewName("forward:/login.jsf");
        for (String view : views){
            if (SPA_FOLDER.equals(view)) {
                continue; // registered below, whether or not the container lists it
            }
            String defaultView = view.replaceAll("\\/","");
            registry.addViewController("/" + defaultView).setViewName("redirect:/" + defaultView + "/");
            registry.addViewController(view).setViewName("forward:" + view + "index.html");
        }
        // the resource handler can't serve an empty path, so the app's root forwards to its index.html
        registry.addViewController("/app").setViewName("redirect:" + SPA_FOLDER);
        registry.addViewController(SPA_FOLDER).setViewName("forward:" + SPA_FOLDER + "index.html");
    }

    /**
     * The React app under {@code /app}. Asset names carry a content hash, so they are cached for a year;
     * {@code index.html} is revalidated on every load so a new deployment is picked up.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/app/assets/**")
                .addResourceLocations(SPA_LOCATION + "assets/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable());
        registry.addResourceHandler("/app/**")
                .addResourceLocations(SPA_LOCATION)
                .setCacheControl(CacheControl.noCache())
                .resourceChain(false)
                .addResolver(new SpaResourceResolver())
                .addTransformer(new SpaIndexTransformer());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
        registry.addInterceptor(new Utf8Interceptor()).addPathPatterns("/app", "/app/**");
    }

    /**
     * Sends the React app as UTF-8. Otherwise rendering the forward to index.html sets the response
     * locale, Tomcat derives ISO-8859-1 from it, and that header charset overrides the page's
     * {@code <meta charset>}; the stylesheets inherit it and the icon font's glyphs come out garbled.
     */
    static class Utf8Interceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            return true;
        }
    }
}
