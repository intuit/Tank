package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.harness.HostInfo;
import com.intuit.tank.test.TestGroups;

/**
 * The class <code>HostInfoCpTest</code> contains tests for the class <code>{@link HostInfo}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class HostInfoCpTest extends Arquillian {

//    @Inject
//    private HostInfo hostInfo;
//
//    @Deployment
//    public static Archive<?> createDeployment() {
//        return ShrinkWrap.create(WebArchive.class, "test.war")
//                .addPackage(HostInfo.class.getPackage())
//                .addAsManifestResource("container-context.xml",
//                        "context.xml").setWebXML("container-web.xml")
//                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
//    }
//
//    @BeforeClass
//    public void configure() {
//    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//    	Configuration config = ctx.getConfiguration();
//    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
//    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
//    }
//
//    /**
//     * Run the HostInfo() constructor test.
//     *
//     * @throws Exception
//     *
//     * @generatedBy CodePro at 9/3/14 3:44 PM
//     */
//    @Test(groups = TestGroups.FUNCTIONAL)
//    public void testHostInfo_1()
//            throws Exception {
//        assertNotNull(hostInfo);
//        Assert.assertNotNull(hostInfo.getPublicHostname());
//        Assert.assertNotNull(hostInfo.getPublicIp());
//    }

}