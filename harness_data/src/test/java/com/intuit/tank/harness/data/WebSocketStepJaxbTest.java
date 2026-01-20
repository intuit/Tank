package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

import com.intuit.tank.harness.data.AssertionBlock;
import com.intuit.tank.harness.data.FailOnPattern;
import com.intuit.tank.harness.data.SaveOccurrence;
import com.intuit.tank.harness.data.WebSocketAssertion;

/**
 * Tests for {@link WebSocketStep} JAXB marshalling and unmarshalling.
 * Verifies that WebSocket steps can be serialized to/from XML correctly.
 */
public class WebSocketStepJaxbTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallConnectStep() throws Exception {
        // Given: A WebSocket CONNECT step
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect to WebSocket");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("conn-1");
        step.setOnFail("abort");
        step.setStepIndex(1);

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws/events");
        request.setTimeoutMs(5000);
        step.setRequest(request);

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);
        
        // Then: XML should be generated
        assertNotNull(xml);
        assertTrue(xml.contains("webSocketStep"));
        assertTrue(xml.contains("connect"));
        assertTrue(xml.contains("conn-1"));
        assertTrue(xml.contains("ws://localhost:8080/ws/events"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: All fields should be preserved
        assertEquals("Connect to WebSocket", roundTrip.getName());
        assertEquals(WebSocketAction.CONNECT, roundTrip.getAction());
        assertEquals("conn-1", roundTrip.getConnectionId());
        assertEquals("abort", roundTrip.getOnFail());
        assertEquals(1, roundTrip.getStepIndex());
        
        assertNotNull(roundTrip.getRequest());
        assertEquals("ws://localhost:8080/ws/events", roundTrip.getRequest().getUrl());
        assertEquals(Integer.valueOf(5000), roundTrip.getRequest().getTimeoutMs());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallSendStep() throws Exception {
        // Given: A WebSocket SEND step with payload
        WebSocketStep step = new WebSocketStep();
        step.setName("Send Message");
        step.setAction(WebSocketAction.SEND);
        step.setConnectionId("conn-1");
        step.setOnFail("abort");
        step.setStepIndex(2);

        WebSocketRequest request = new WebSocketRequest();
        request.setPayload("{\"operation\":\"subscribe\",\"topic\":\"updates\"}");
        request.setTimeoutMs(2000);
        step.setRequest(request);

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);
        
        // Then: XML should contain payload
        assertNotNull(xml);
        assertTrue(xml.contains("send"));
        assertTrue(xml.contains("subscribe"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: All fields should be preserved
        assertEquals("Send Message", roundTrip.getName());
        assertEquals(WebSocketAction.SEND, roundTrip.getAction());
        assertEquals("conn-1", roundTrip.getConnectionId());
        
        assertNotNull(roundTrip.getRequest());
        assertEquals("{\"operation\":\"subscribe\",\"topic\":\"updates\"}", roundTrip.getRequest().getPayload());
        assertEquals(Integer.valueOf(2000), roundTrip.getRequest().getTimeoutMs());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallDisconnectStep() throws Exception {
        // Given: A WebSocket DISCONNECT step
        WebSocketStep step = new WebSocketStep();
        step.setName("Disconnect WebSocket");
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("conn-1");
        step.setOnFail("continue");
        step.setStepIndex(4);

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);
        
        // Then: XML should be minimal (no request/response needed)
        assertNotNull(xml);
        assertTrue(xml.contains("disconnect"));
        assertTrue(xml.contains("conn-1"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: All fields should be preserved
        assertEquals("Disconnect WebSocket", roundTrip.getName());
        assertEquals(WebSocketAction.DISCONNECT, roundTrip.getAction());
        assertEquals("conn-1", roundTrip.getConnectionId());
        assertEquals("continue", roundTrip.getOnFail());
        assertEquals(4, roundTrip.getStepIndex());
        
        assertNull(roundTrip.getRequest());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallWithVariables() throws Exception {
        // Given: A WebSocket step with variable substitution
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect with Variables");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("#{connectionId}");
        step.setStepIndex(1);

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws?id=#{userId}&token=#{authToken}");
        request.setTimeoutMs(5000);
        step.setRequest(request);

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);
        
        // Then: Variables should be preserved in XML
        assertNotNull(xml);
        assertTrue(xml.contains("#{connectionId}"));
        assertTrue(xml.contains("#{userId}"));
        assertTrue(xml.contains("#{authToken}"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: Variables should be preserved
        assertEquals("#{connectionId}", roundTrip.getConnectionId());
        assertEquals("ws://localhost:8080/ws?id=#{userId}&token=#{authToken}", 
                     roundTrip.getRequest().getUrl());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallWithComments() throws Exception {
        // Given: A WebSocket step with comments
        WebSocketStep step = new WebSocketStep();
        step.setName("Test Step");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("conn-1");
        step.setComments("This is a test connection for demo purposes");
        step.setStepIndex(1);

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws");
        step.setRequest(request);

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);
        
        // Then: Comments should be in XML
        assertNotNull(xml);
        assertTrue(xml.contains("demo purposes"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: Comments should be preserved
        assertEquals("This is a test connection for demo purposes", roundTrip.getComments());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetInfo() throws Exception {
        // Given: A WebSocket step
        WebSocketStep step = new WebSocketStep();
        step.setName("Test Connection");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("test-conn");

        // When: We call getInfo()
        String info = step.getInfo();

        // Then: Info should contain action and connection details
        assertNotNull(info);
        assertTrue(info.contains("CONNECT"));
        assertTrue(info.contains("Test Connection"));
        assertTrue(info.contains("test-conn"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallWithFailOnPatterns() throws Exception {
        // Given: A WebSocket step with fail-on patterns
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect with Fail-On");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("conn-1");
        step.setStepIndex(1);

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws");
        step.setRequest(request);

        step.getFailOnPatterns().add(new FailOnPattern("error", false));
        step.getFailOnPatterns().add(new FailOnPattern("\"status\":5\\d\\d", true));

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);

        // Then: XML should contain fail-on patterns
        assertNotNull(xml);
        assertTrue(xml.contains("fail-on"));
        assertTrue(xml.contains("error"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: Fail-on patterns should be preserved
        assertEquals(2, roundTrip.getFailOnPatterns().size());
        assertEquals("error", roundTrip.getFailOnPatterns().get(0).getPattern());
        assertFalse(roundTrip.getFailOnPatterns().get(0).isRegex());
        assertTrue(roundTrip.getFailOnPatterns().get(1).isRegex());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallWithAssertions() throws Exception {
        // Given: A WebSocket DISCONNECT step with assertions
        WebSocketStep step = new WebSocketStep();
        step.setName("Disconnect with Assertions");
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("conn-1");
        step.setStepIndex(5);

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("Echo:")
            .minCount(2)
            .build());
        assertions.getSaves().add(WebSocketAssertion.builder()
            .pattern("\"id\":(\\d+)")
            .regex(true)
            .variable("lastId")
            .occurrence(SaveOccurrence.LAST)
            .build());
        step.setAssertions(assertions);

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);

        // Then: XML should contain assertions
        assertNotNull(xml);
        assertTrue(xml.contains("assertions"));
        assertTrue(xml.contains("expect"));
        assertTrue(xml.contains("save"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: Assertions should be preserved
        assertNotNull(roundTrip.getAssertions());
        assertEquals(1, roundTrip.getAssertions().getExpects().size());
        assertEquals(1, roundTrip.getAssertions().getSaves().size());
        assertEquals("Echo:", roundTrip.getAssertions().getExpects().get(0).getPattern());
        assertEquals("lastId", roundTrip.getAssertions().getSaves().get(0).getVariable());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshallUnmarshallAssertAction() throws Exception {
        // Given: A WebSocket ASSERT step
        WebSocketStep step = new WebSocketStep();
        step.setName("Mid-Session Assert");
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");
        step.setStepIndex(3);

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("authenticated")
            .build());
        step.setAssertions(assertions);

        // When: We marshall to XML
        String xml = JaxbUtil.marshall(step);

        // Then: XML should contain assert action
        assertNotNull(xml);
        assertTrue(xml.contains("assert"));

        // When: We unmarshall back to object
        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);

        // Then: ASSERT action should be preserved
        assertEquals(WebSocketAction.ASSERT, roundTrip.getAction());
    }
}
