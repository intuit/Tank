/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.client.v1.automation;

/*
 * #%L
 * Automation Rest Client
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.stream.Stream;

import com.intuit.tank.api.client.v1.automation.AutomationServiceClient;
import com.intuit.tank.api.model.v1.automation.AutomationJobRegion;
import com.intuit.tank.api.model.v1.automation.AutomationRequest;
import com.intuit.tank.api.model.v1.automation.AutomationRequest.AutomationRequestBuilder;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AutomationServiceClientTest
 * 
 * @author dangleton
 * 
 */
public class AutomationServiceClientTest {

    private AutomationServiceClient client;

    private int[] filterGroupIds = new int[] { 3 };
    private int[] exterScriptIds = new int[] { 3, 6, 7 };

    static Stream<Arguments> updload_files() {
        return Stream.of(
                Arguments.of(new File("src/test/resources/small.xml") )
        // , Arguments.of( new File("src/test/resources/medium.xml") )
        );
    }

    @BeforeEach
    public void setup() {
        client = new AutomationServiceClient("");
    }

    @Test
    @Tag(TestGroups.MANUAL)
    @Disabled
    public void testPing() {
        String result = client.ping();
        assertEquals("PONG " + "AutomationServiceV1", result);
    }

    // @ParameterizedTest
    // @Tag(TestGroups.MANUAL)
    // @MethodSource("upload_files")
    public void testRunJob(File xmlFile) {
        AutomationRequest request = AutomationRequest.builder().withProductName("Test Product").withRampTime("60s")
                .withSimulationTime("120s").withUserIntervalIncrement(1)
                .withAddedFilterId(1).withAddedJobRegion(new AutomationJobRegion(VMRegion.US_EAST, "5")).build();
        String result = client.runAutomationJob(request, xmlFile);
        assertNotNull(result);
    }

    @Test
    @Tag(TestGroups.MANUAL)
    public void testRunJob() {
        AutomationRequest request = AutomationRequest.builder().withProductName("Test Product").withRampTime("60s")
                .withSimulationTime("120s").withUserIntervalIncrement(1).withName("Google Home")
                .withAddedJobRegion(new AutomationJobRegion(VMRegion.US_EAST, "5")).build();
        String result = client.runAutomationJob(request, (File) null);
        assertNotNull(result);
    }

     @Test
     @Tag(TestGroups.MANUAL)
     @Disabled
    public void testRunFredsJob() {
        AutomationRequestBuilder builder = AutomationRequest.builder()
                .withProductName("Test Product")
                .withRampTime("1920s")
                .withSimulationTime("7200s")
                .withUserIntervalIncrement(1);
        for (int gId : filterGroupIds) {
            builder.withAddedFilterGroupId(gId);
        }
        for (int eId : exterScriptIds) {
            builder.withAddedExternalScriptId(eId);
        }
        builder.withAddedJobRegion(new AutomationJobRegion(VMRegion.US_EAST, "3000"));

        AutomationRequest request = builder.build();
        // String result = client.runAutomationJob(request, new File(resource));
        // Assert.assertNotNull(result);
    }

     @ParameterizedTest
     @Tag(TestGroups.MANUAL)
     @MethodSource("upload_files")
     @Disabled
    public void testRunJobStream(File xmlFile) throws FileNotFoundException {
        AutomationRequest request = AutomationRequest.builder().withProductName("Test Product").withRampTime("60s")
                .withSimulationTime("120s").withUserIntervalIncrement(1)
                .withAddedFilterId(1).withAddedJobRegion(new AutomationJobRegion(VMRegion.US_EAST, "5"))
                .withAddedExternalScriptId(1).withAddedFilterGroupId(1)
                .withAddedFilterGroupId(2).build();
        String result = client.runAutomationJob(request, new FileInputStream(xmlFile));
        assertNotNull(result);
    }

}
