/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc;

import com.intuit.tank.rest.mvc.rest.models.common.cloud.VMTrackerV2Impl;
import com.intuit.tank.rest.mvc.rest.util.JobEventListener;
import com.intuit.tank.rest.mvc.rest.util.JobEventSender;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.FilterType;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication(proxyBeanMethods = false)
@ComponentScan(basePackages = "com.intuit.tank.rest.mvc", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JobEventSender.class, JobEventListener.class, VMTrackerV2Impl.class})
})
public class TankAPIApplication extends SpringBootServletInitializer {
    public static ConfigurableApplicationContext run(String[] args) {
        return SpringApplication.run(TankAPIApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(TankAPIApplication.class);
    }

    public static void main(String[] args) {
        run(args);  //NOSONAR
    }
}
