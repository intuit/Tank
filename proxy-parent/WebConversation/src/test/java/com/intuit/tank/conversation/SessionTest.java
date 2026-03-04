package com.intuit.tank.conversation;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.junit.jupiter.api.*;

import com.intuit.tank.conversation.Session;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>SessionTest</code> contains tests for the class <code>{@link Session}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:08 AM
 */
public class SessionTest {
    /**
     * Run the Session() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSession_1()
            throws Exception {

        Session result = new Session();

        assertNotNull(result);
        assertEquals(true, result.isCollapseRedirects());
    }

    /**
     * Run the Session(List<Transaction>,boolean) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testSession_2()
            throws Exception {
        List<Transaction> transactions = new LinkedList();
        boolean collapseRedirects = true;

        Session result = new Session(transactions, collapseRedirects);

        assertNotNull(result);
        assertEquals(true, result.isCollapseRedirects());
    }

    /**
     * Run the List<Transaction> getTransactions() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testGetTransactions_1()
            throws Exception {
        Session fixture = new Session(new LinkedList(), true);

        List<Transaction> result = fixture.getTransactions();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the boolean isCollapseRedirects() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsCollapseRedirects_1()
            throws Exception {
        Session fixture = new Session(new LinkedList(), true);

        boolean result = fixture.isCollapseRedirects();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isCollapseRedirects() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:08 AM
     */
    @Test
    public void testIsCollapseRedirects_2()
            throws Exception {
        Session fixture = new Session(new LinkedList(), false);

        boolean result = fixture.isCollapseRedirects();

        assertEquals(false, result);
    }

    @Test
    @DisplayName("JAXB round-trip preserves both HTTP transactions and WebSocket transactions")
    public void testJaxbRoundTripWithWebSocketTransactions() throws Exception {
        List<Transaction> httpTxns = new ArrayList<>();
        Transaction httpTx = new Transaction();
        httpTxns.add(httpTx);

        List<WebSocketTransaction> wsTxns = new ArrayList<>();
        WebSocketTransaction wsTx = new WebSocketTransaction("ws://example.com/chat");
        wsTx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hello".getBytes(StandardCharsets.UTF_8), 1000L));
        wsTx.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT,
                "world".getBytes(StandardCharsets.UTF_8), 2000L));
        wsTxns.add(wsTx);

        Session session = new Session(httpTxns, wsTxns, false);

        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(session, sw);
        String xml = sw.toString();

        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Session restored = (Session) unmarshaller.unmarshal(new StringReader(xml));

        assertEquals(1, restored.getTransactions().size());
        assertEquals(1, restored.getWebSocketTransactions().size());

        WebSocketTransaction restoredWsTx = restored.getWebSocketTransactions().get(0);
        assertEquals("ws://example.com/chat", restoredWsTx.getUrl());
        assertEquals(2, restoredWsTx.getMessageCount());
        assertEquals("hello", restoredWsTx.getMessages().get(0).getContentAsString());
        assertEquals("world", restoredWsTx.getMessages().get(1).getContentAsString());
        assertTrue(restoredWsTx.getMessages().get(0).isFromClient());
        assertFalse(restoredWsTx.getMessages().get(1).isFromClient());
    }
}