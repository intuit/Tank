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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private ServletContext context;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        List<String> views = new ArrayList<String>(context.getResourcePaths("/"));
        registry.addViewController("/").setViewName("forward:/login.jsf");
        for (String view : views){
            String defaultView = view.replaceAll("\\/","");
            registry.addViewController("/" + defaultView).setViewName("redirect:/" + defaultView + "/");
            registry.addViewController(view).setViewName("forward:" + view + "index.html");
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
    }
}
