package com.intuit.tank.runner.method;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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

import java.io.StringReader;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.data.WebSocketAction;
import com.intuit.tank.harness.data.WebSocketStep;

/**
 * Test WebSocket script parsing from XML
 */
public class WebSocketScriptParsingTest {

    @Test
    public void testParseWebSocketScript() throws Exception {
        String xmlScript = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <ns2:workload xmlns:ns2="urn:com/intuit/tank/harness/data/v1" name="WebSocket Test Script">
                <plan testPlanName="WebSocket-Test" userPercentage="100">
                    <testSuite name="WebSocket-Suite" description="WebSocket Testing" loop="1">
                        <testGroup name="Echo-Test" loop="1">
                            <useCase>
                                <testStep xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                                          xsi:type="ns2:webSocketStep" 
                                          name="Connect to Echo Server" 
                                          action="connect"
                                          connectionId="echo_conn"
                                          onFail="abort">
                                    <stepIndex>0</stepIndex>
                                    <request>
                                        <url>ws://localhost:8080/echo</url>
                                        <timeoutMs>5000</timeoutMs>
                                    </request>
                                </testStep>
                            </useCase>
                        </testGroup>
                    </testSuite>
                </plan>
            </ns2:workload>
            """;

        // Parse the XML with WebSocket classes
        JAXBContext jaxbContext = JAXBContext.newInstance("com.intuit.tank.harness.data");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        HDWorkload workload = (HDWorkload) unmarshaller.unmarshal(new StringReader(xmlScript));

        // Verify the workload was parsed
        assertNotNull(workload);
        assertEquals("WebSocket Test Script", workload.getName());
        
        // Navigate to the WebSocket step
        assertNotNull(workload.getPlans());
        assertFalse(workload.getPlans().isEmpty());
        
        var plan = workload.getPlans().get(0);
        assertNotNull(plan.getGroup());
        assertFalse(plan.getGroup().isEmpty());
        
        var testSuite = plan.getGroup().get(0);
        assertNotNull(testSuite.getGroupSteps());
        assertFalse(testSuite.getGroupSteps().isEmpty());
        
        var testGroup = testSuite.getGroupSteps().get(0);
        assertNotNull(testGroup.getUseCase());
        assertFalse(testGroup.getUseCase().isEmpty());
        
        var useCase = testGroup.getUseCase().get(0);
        assertNotNull(useCase.getScriptSteps());
        assertFalse(useCase.getScriptSteps().isEmpty());
        
        // Verify the WebSocket step
        var step = useCase.getScriptSteps().get(0);
        assertTrue(step instanceof WebSocketStep);
        
        WebSocketStep wsStep = (WebSocketStep) step;
        assertEquals("Connect to Echo Server", wsStep.getName());
        assertEquals(WebSocketAction.CONNECT, wsStep.getAction());
        assertEquals("echo_conn", wsStep.getConnectionId());
        assertEquals("abort", wsStep.getOnFail());
        assertEquals(0, wsStep.getStepIndex());
        
        // Verify the request
        assertNotNull(wsStep.getRequest());
        assertEquals("ws://localhost:8080/echo", wsStep.getRequest().getUrl());
        assertEquals(Integer.valueOf(5000), wsStep.getRequest().getTimeoutMs());
    }

    @Test
    public void testWebSocketStepCreation() {
        // Test programmatic creation of WebSocket steps
        WebSocketStep step = new WebSocketStep();
        step.setName("Test WebSocket Step");
        step.setAction(WebSocketAction.SEND);
        step.setConnectionId("test_connection");
        step.setStepIndex(1);
        
        assertEquals("Test WebSocket Step", step.getName());
        assertEquals(WebSocketAction.SEND, step.getAction());
        assertEquals("test_connection", step.getConnectionId());
        assertEquals(1, step.getStepIndex());
        
        // Test getInfo() method
        String info = step.getInfo();
        assertTrue(info.contains("SEND"));
        assertTrue(info.contains("Test WebSocket Step"));
        assertTrue(info.contains("test_connection"));
    }
}
