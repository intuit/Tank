package com.intuit.tank.conversation;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WebSocketTransaction JAXB round-trip serialization.
 */
class WebSocketTransactionTest {

    @Test
    @DisplayName("P0 #8 — handshakeHeaders survive JAXB marshal/unmarshal round-trip with adapter")
    void handshakeHeadersRoundTrip() throws Exception {
        // Arrange
        WebSocketTransaction tx = new WebSocketTransaction("ws://example.com/chat");
        tx.addHandshakeHeader("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==");
        tx.addHandshakeHeader("Origin", "http://example.com");
        tx.addHandshakeHeader("Host", "example.com");

        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hello".getBytes(StandardCharsets.UTF_8), 1000L));

        // Marshal
        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(tx, sw);
        String xml = sw.toString();

        // Verify adapter produces clean XML with <header key="..." value="..."/> structure
        assertTrue(xml.contains("header"), "XML should contain header elements: " + xml);
        // Should NOT contain raw <entry><key>...</key><value>...</value></entry> from default Map handling
        assertFalse(xml.contains("<entry>"), "Should use adapter, not default Map serialization: " + xml);

        // Unmarshal
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        WebSocketTransaction result = (WebSocketTransaction) unmarshaller.unmarshal(new StringReader(xml));

        // Assert headers survived
        assertNotNull(result.getHandshakeHeaders(), "handshakeHeaders should not be null");
        assertEquals(3, result.getHandshakeHeaders().size(), "should have 3 headers");
        assertEquals("dGhlIHNhbXBsZSBub25jZQ==", result.getHandshakeHeaders().get("Sec-WebSocket-Key"));
        assertEquals("http://example.com", result.getHandshakeHeaders().get("Origin"));
        assertEquals("example.com", result.getHandshakeHeaders().get("Host"));

        // Assert messages survived
        assertEquals(1, result.getMessages().size());
        assertEquals("hello", result.getMessages().get(0).getContentAsString());
    }

    @Test
    @DisplayName("P0 #8 — empty handshakeHeaders round-trip produces empty map, not null")
    void emptyHandshakeHeadersRoundTrip() throws Exception {
        WebSocketTransaction tx = new WebSocketTransaction("ws://example.com/ws");
        // No headers added — map is empty

        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(tx, sw);

        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        WebSocketTransaction result = (WebSocketTransaction) unmarshaller.unmarshal(new StringReader(sw.toString()));

        assertNotNull(result.getHandshakeHeaders(), "handshakeHeaders should not be null even if empty");
        assertTrue(result.getHandshakeHeaders().isEmpty());
    }

    @Test
    @DisplayName("Session with WebSocketTransactions round-trips correctly including headers")
    void sessionWithWebSocketTransactionsRoundTrip() throws Exception {
        WebSocketTransaction wsTx = new WebSocketTransaction("wss://secure.example.com/ws");
        wsTx.addHandshakeHeader("Authorization", "Bearer token123");
        wsTx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "ping".getBytes(StandardCharsets.UTF_8), 2000L));
        wsTx.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT,
                "pong".getBytes(StandardCharsets.UTF_8), 2001L));

        Session session = new Session(new java.util.ArrayList<>(),
                java.util.List.of(wsTx), false);

        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(session, sw);
        String xml = sw.toString();

        // Verify adapter-style XML
        assertTrue(xml.contains("Authorization"), "XML should contain Authorization header: " + xml);
        assertTrue(xml.contains("Bearer token123"), "XML should contain token value: " + xml);

        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Session result = (Session) unmarshaller.unmarshal(new StringReader(sw.toString()));

        assertEquals(1, result.getWebSocketTransactions().size());
        WebSocketTransaction resultTx = result.getWebSocketTransactions().get(0);
        assertEquals("wss://secure.example.com/ws", resultTx.getUrl());
        assertEquals(Protocol.wss, resultTx.getProtocol());

        Map<String, String> headers = resultTx.getHandshakeHeaders();
        assertNotNull(headers);
        assertEquals(1, headers.size());
        assertEquals("Bearer token123", headers.get("Authorization"));

        assertEquals(2, resultTx.getMessages().size());
    }
}
