package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

/**
 * Integration tests for HDWorkload with WebSocket steps.
 * Verifies that workloads containing both HTTP and WebSocket steps parse correctly.
 */
public class HDWorkloadWebSocketTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testWorkloadWithMixedSteps() throws Exception {
        // Given: A workload XML with both HTTP and WebSocket steps
        String workloadXml = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <ns2:workload xmlns:ns2="urn:com/intuit/tank/harness/data/v1"
                          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          name="Mixed Test"
                          description="HTTP and WebSocket test">
              
              <client-class>com.intuit.tank.httpclientjdk.TankHttpClientJDK</client-class>
              
              <plan testPlanName="Main" userPercentage="100">
                <testSuite name="Suite-1" description="Mixed steps" loop="1">
                  <testGroup name="Group-1" description="Test group" loop="1">
                    <useCase>
                      
                      <!-- HTTP Step -->
                      <testStep xsi:type="ns2:testStep" name="HTTP Login" onFail="abort">
                        <stepIndex>0</stepIndex>
                        <request name="POST" format="json">
                          <label>http://localhost:8080/api/login</label>
                          <path>/api/login</path>
                          <protocol>http</protocol>
                          <host>localhost</host>
                          <port>8080</port>
                        </request>
                      </testStep>
                      
                      <!-- WebSocket Connect -->
                      <testStep xsi:type="ns2:webSocketStep"
                                name="WebSocket Connect"
                                action="connect"
                                connectionId="conn-1"
                                onFail="abort">
                        <stepIndex>1</stepIndex>
                        <request>
                          <url>ws://localhost:8080/ws</url>
                          <timeoutMs>5000</timeoutMs>
                        </request>
                      </testStep>
                      
                      <!-- WebSocket Send -->
                      <testStep xsi:type="ns2:webSocketStep"
                                name="Send Message"
                                action="send"
                                connectionId="conn-1"
                                onFail="abort">
                        <stepIndex>2</stepIndex>
                        <request>
                          <payload>{"action":"subscribe"}</payload>
                          <timeoutMs>2000</timeoutMs>
                        </request>
                      </testStep>
                      
                      <!-- WebSocket Expect -->
                      <testStep xsi:type="ns2:webSocketStep"
                                name="Expect Response"
                                action="expect"
                                connectionId="conn-1"
                                onFail="abort">
                        <stepIndex>3</stepIndex>
                        <response>
                          <timeoutMs>3000</timeoutMs>
                          <expectedContent>"subscribed"</expectedContent>
                        </response>
                      </testStep>
                      
                      <!-- Another HTTP Step -->
                      <testStep xsi:type="ns2:testStep" name="HTTP Request" onFail="abort">
                        <stepIndex>4</stepIndex>
                        <request name="GET" format="json">
                          <label>http://localhost:8080/api/data</label>
                          <path>/api/data</path>
                          <protocol>http</protocol>
                          <host>localhost</host>
                          <port>8080</port>
                        </request>
                      </testStep>
                      
                      <!-- WebSocket Disconnect -->
                      <testStep xsi:type="ns2:webSocketStep"
                                name="WebSocket Disconnect"
                                action="disconnect"
                                connectionId="conn-1"
                                onFail="continue">
                        <stepIndex>5</stepIndex>
                      </testStep>
                      
                    </useCase>
                  </testGroup>
                </testSuite>
              </plan>
            </ns2:workload>
            """;

        // When: We unmarshall the workload
        HDWorkload workload = JaxbUtil.unmarshall(workloadXml, HDWorkload.class);

        // Then: Workload should be parsed
        assertNotNull(workload);
        assertEquals("Mixed Test", workload.getName());
        assertEquals("HTTP and WebSocket test", workload.getDescription());

        // And: Should have one test plan
        List<HDTestPlan> plans = workload.getPlans();
        assertNotNull(plans);
        assertEquals(1, plans.size());

        HDTestPlan plan = plans.get(0);
        assertEquals("Main", plan.getTestPlanName());
        assertEquals(100, plan.getUserPercentage());

        // And: Should have test groups with steps
        List<HDScriptGroup> groups = plan.getGroup();
        assertNotNull(groups);
        assertEquals(1, groups.size());

        HDScriptGroup group = groups.get(0);
        List<HDScript> scripts = group.getGroupSteps();
        assertNotNull(scripts);
        assertEquals(1, scripts.size());

        HDScript script = scripts.get(0);
        List<HDScriptUseCase> useCases = script.getUseCase();
        assertNotNull(useCases);
        assertEquals(1, useCases.size());

        HDScriptUseCase useCase = useCases.get(0);
        List<TestStep> steps = useCase.getScriptSteps();
        assertNotNull(steps);
        assertEquals(6, steps.size());

        // And: Steps should be in correct order and type
        // Step 0: HTTP Login
        assertTrue(steps.get(0) instanceof RequestStep);
        RequestStep httpStep1 = (RequestStep) steps.get(0);
        assertEquals("HTTP Login", httpStep1.getName());
        assertEquals(0, httpStep1.getStepIndex());

        // Step 1: WebSocket Connect
        assertTrue(steps.get(1) instanceof WebSocketStep);
        WebSocketStep wsConnect = (WebSocketStep) steps.get(1);
        assertEquals("WebSocket Connect", wsConnect.getName());
        assertEquals(WebSocketAction.CONNECT, wsConnect.getAction());
        assertEquals("conn-1", wsConnect.getConnectionId());
        assertEquals(1, wsConnect.getStepIndex());
        assertNotNull(wsConnect.getRequest());
        assertEquals("ws://localhost:8080/ws", wsConnect.getRequest().getUrl());

        // Step 2: WebSocket Send
        assertTrue(steps.get(2) instanceof WebSocketStep);
        WebSocketStep wsSend = (WebSocketStep) steps.get(2);
        assertEquals("Send Message", wsSend.getName());
        assertEquals(WebSocketAction.SEND, wsSend.getAction());
        assertEquals(2, wsSend.getStepIndex());
        assertNotNull(wsSend.getRequest());
        assertEquals("{\"action\":\"subscribe\"}", wsSend.getRequest().getPayload());

        // Step 3: WebSocket Expect
        assertTrue(steps.get(3) instanceof WebSocketStep);
        WebSocketStep wsExpect = (WebSocketStep) steps.get(3);
        assertEquals("Expect Response", wsExpect.getName());
        assertEquals(WebSocketAction.EXPECT, wsExpect.getAction());
        assertEquals(3, wsExpect.getStepIndex());
        assertNotNull(wsExpect.getResponse());
        assertEquals("\"subscribed\"", wsExpect.getResponse().getExpectedContent());

        // Step 4: HTTP Request
        assertTrue(steps.get(4) instanceof RequestStep);
        RequestStep httpStep2 = (RequestStep) steps.get(4);
        assertEquals("HTTP Request", httpStep2.getName());
        assertEquals(4, httpStep2.getStepIndex());

        // Step 5: WebSocket Disconnect
        assertTrue(steps.get(5) instanceof WebSocketStep);
        WebSocketStep wsDisconnect = (WebSocketStep) steps.get(5);
        assertEquals("WebSocket Disconnect", wsDisconnect.getName());
        assertEquals(WebSocketAction.DISCONNECT, wsDisconnect.getAction());
        assertEquals(5, wsDisconnect.getStepIndex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testWorkloadRoundTrip() throws Exception {
        // Given: A workload with WebSocket steps
        HDWorkload originalWorkload = new HDWorkload();
        originalWorkload.setName("Test Workload");
        originalWorkload.setDescription("WebSocket test");

        HDTestPlan plan = new HDTestPlan();
        plan.setTestPlanName("Main");
        plan.setUserPercentage(100);

        HDScriptGroup group = new HDScriptGroup();
        group.setName("Group-1");
        group.setDescription("Test group");

        HDScript script = new HDScript();
        script.setName("Script-1");

        HDScriptUseCase useCase = new HDScriptUseCase();

        // Add a WebSocket step
        WebSocketStep wsStep = new WebSocketStep();
        wsStep.setName("Connect");
        wsStep.setAction(WebSocketAction.CONNECT);
        wsStep.setConnectionId("conn-1");
        wsStep.setStepIndex(0);
        
        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws");
        wsStep.setRequest(request);

        useCase.getScriptSteps().add(wsStep);
        script.getUseCase().add(useCase);
        group.getGroupSteps().add(script);
        plan.getGroup().add(group);
        originalWorkload.getPlans().add(plan);

        // When: We marshall to XML and back
        String xml = JaxbUtil.marshall(originalWorkload);
        assertNotNull(xml);
        assertTrue(xml.contains("webSocketStep"));
        assertTrue(xml.contains("connect"));

        HDWorkload roundTrip = JaxbUtil.unmarshall(xml, HDWorkload.class);

        // Then: Workload should be preserved
        assertNotNull(roundTrip);
        assertEquals("Test Workload", roundTrip.getName());

        List<TestStep> steps = roundTrip.getPlans().get(0)
            .getGroup().get(0)
            .getGroupSteps().get(0)
            .getUseCase().get(0)
            .getScriptSteps();

        assertEquals(1, steps.size());
        assertTrue(steps.get(0) instanceof WebSocketStep);
        
        WebSocketStep roundTripStep = (WebSocketStep) steps.get(0);
        assertEquals("Connect", roundTripStep.getName());
        assertEquals(WebSocketAction.CONNECT, roundTripStep.getAction());
        assertEquals("conn-1", roundTripStep.getConnectionId());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testWorkloadWithVariableSubstitution() throws Exception {
        // Given: A workload with variable substitution in WebSocket steps
        String workloadXml = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <ns2:workload xmlns:ns2="urn:com/intuit/tank/harness/data/v1"
                          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          name="Variable Test">
              
              <client-class>com.intuit.tank.httpclientjdk.TankHttpClientJDK</client-class>
              
              <plan testPlanName="Main" userPercentage="100">
                <testSuite name="Suite-1" loop="1">
                  <testGroup name="Group-1" loop="1">
                    <useCase>
                      <testStep xsi:type="ns2:webSocketStep"
                                name="Connect with Variables"
                                action="connect"
                                connectionId="#{connectionId}"
                                onFail="abort">
                        <stepIndex>0</stepIndex>
                        <request>
                          <url>ws://#{host}:#{port}/ws?user=#{userId}</url>
                          <timeoutMs>5000</timeoutMs>
                        </request>
                      </testStep>
                    </useCase>
                  </testGroup>
                </testSuite>
              </plan>
            </ns2:workload>
            """;

        // When: We unmarshall the workload
        HDWorkload workload = JaxbUtil.unmarshall(workloadXml, HDWorkload.class);

        // Then: Variables should be preserved
        List<TestStep> steps = workload.getPlans().get(0)
            .getGroup().get(0)
            .getGroupSteps().get(0)
            .getUseCase().get(0)
            .getScriptSteps();

        WebSocketStep wsStep = (WebSocketStep) steps.get(0);
        assertEquals("#{connectionId}", wsStep.getConnectionId());
        assertEquals("ws://#{host}:#{port}/ws?user=#{userId}", wsStep.getRequest().getUrl());
    }
}
