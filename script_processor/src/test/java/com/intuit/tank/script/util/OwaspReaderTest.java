package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
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
import java.io.FileReader;
import java.io.PipedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.conversation.Session;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.util.OwaspReader;
import com.intuit.tank.vm.exception.WatsParseException;

/**
 * The class <code>OwaspReaderTest</code> contains tests for the class <code>{@link OwaspReader}</code>.
 *
 * @generatedBy CodePro at 12/16/14 4:48 PM
 */
public class OwaspReaderTest {
    /**
     * Run the OwaspReader() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testOwaspReader_1()
        throws Exception {

        OwaspReader result = new OwaspReader();

        assertNotNull(result);
    }

    /**
     * Run the Set<RequestData> rawJsonToSet(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test()
    public void testRawJsonToSet_1()
        throws Exception {
        String response = "";

        assertThrows(org.json.JSONException.class, () -> OwaspReader.rawJsonToSet(response));
    }

    /**
     * Run the Set<RequestData> rawJsonToSet(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test()
    public void testRawJsonToSet_2()
        throws Exception {
        String response = "";

        assertThrows(org.json.JSONException.class, () -> OwaspReader.rawJsonToSet(response));
    }

    /**
     * Run the Set<RequestData> rawJsonToSet(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test()
    public void testRawJsonToSet_3()
        throws Exception {
        String response = "";

        assertThrows(org.json.JSONException.class, () ->  OwaspReader.rawJsonToSet(response));
    }

 

    /**
     * Run the List<ScriptStep> read(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test()
    public void testRead_4()
        throws Exception {
        OwaspReader fixture = new OwaspReader();
        String xml = "";

        assertThrows(com.intuit.tank.vm.exception.WatsParseException.class, () -> fixture.read(xml));
    }

    /**
     * Run the List<ScriptStep> transactionsToRequest(Collection<Transaction>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testTransactionsToRequest_1()
        throws Exception {
        OwaspReader fixture = new OwaspReader();
        Collection<Transaction> entries = new LinkedList();

        List<ScriptStep> result = fixture.transactionsToRequest(entries);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ScriptStep> transactionsToRequest(Collection<Transaction>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testTransactionsToRequest_2()
        throws Exception {
        OwaspReader fixture = new OwaspReader();
        Collection<Transaction> entries = new LinkedList();

        List<ScriptStep> result = fixture.transactionsToRequest(entries);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("read() should produce WS ScriptSteps when Session contains WebSocketTransactions")
    public void testReadWithWebSocketTransactions() throws Exception {
        // Build a Session with one WS transaction containing a client message
        Session session = new Session();
        WebSocketTransaction wsTx = new WebSocketTransaction("ws://example.com/ws");
        wsTx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hello".getBytes(StandardCharsets.UTF_8), 1000L));
        wsTx.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT,
                "world".getBytes(StandardCharsets.UTF_8), 2000L));
        session.getWebSocketTransactions().add(wsTx);

        // Marshal to XML, then parse via OwaspReader
        JAXBContext ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
        Marshaller m = ctx.createMarshaller();
        StringWriter sw = new StringWriter();
        m.marshal(session, sw);
        String xml = sw.toString();

        OwaspReader reader = new OwaspReader();
        List<ScriptStep> steps = reader.read(new StringReader(xml));

        // Should have CONNECT + SEND (client msg only) + DISCONNECT = 3 steps
        assertEquals(3, steps.size(), "Expected 3 WS steps: CONNECT, SEND, DISCONNECT");

        // Verify step types
        for (ScriptStep step : steps) {
            assertEquals("websocket", step.getType());
        }

        // Verify actions
        assertEquals("CONNECT", getRequestDataValue(steps.get(0), "ws-action"));
        assertEquals("SEND", getRequestDataValue(steps.get(1), "ws-action"));
        assertEquals("DISCONNECT", getRequestDataValue(steps.get(2), "ws-action"));

        // Verify payload on SEND step
        assertEquals("hello", getRequestDataValue(steps.get(1), "ws-payload"));

        // Verify URL
        assertEquals("ws://example.com/ws", getRequestDataValue(steps.get(0), "ws-url"));
    }

    private String getRequestDataValue(ScriptStep step, String key) {
        Set<RequestData> data = step.getData();
        if (data == null) return null;
        for (RequestData rd : data) {
            if (key.equals(rd.getKey())) {
                return rd.getValue();
            }
        }
        return null;
    }
}