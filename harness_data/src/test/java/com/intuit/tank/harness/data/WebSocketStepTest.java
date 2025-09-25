package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

/**
 * Tests for {@link WebSocketStep} JAXB serialization and basic accessors.
 */
public class WebSocketStepTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshalUnmarshalConnectStep() throws Exception {
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect WS");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("conn-1");

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("wss://echo.example/ws");
        request.setTimeoutMs(5000);
        step.setRequest(request);

        String xml = JaxbUtil.marshall(step);
        assertNotNull(xml);

        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);
        assertEquals("Connect WS", roundTrip.getName());
        assertEquals(WebSocketAction.CONNECT, roundTrip.getAction());
        assertEquals("conn-1", roundTrip.getConnectionId());
        assertEquals("conn-1", roundTrip.getComments());
        assertNotNull(roundTrip.getRequest());
        assertEquals("wss://echo.example/ws", roundTrip.getRequest().getUrl());
        assertEquals(Integer.valueOf(5000), roundTrip.getRequest().getTimeoutMs());
        assertNull(roundTrip.getResponse());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshalUnmarshalDisconnectStep() throws Exception {
        WebSocketStep step = new WebSocketStep();
        step.setName("Disconnect WS");
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("conn-1");

        String xml = JaxbUtil.marshall(step);
        assertNotNull(xml);

        WebSocketStep roundTrip = JaxbUtil.unmarshall(xml, WebSocketStep.class);
        assertEquals(WebSocketAction.DISCONNECT, roundTrip.getAction());
        assertEquals("conn-1", roundTrip.getConnectionId());
        assertEquals("conn-1", roundTrip.getComments());
        assertNull(roundTrip.getRequest());
        assertNull(roundTrip.getResponse());
    }
}
