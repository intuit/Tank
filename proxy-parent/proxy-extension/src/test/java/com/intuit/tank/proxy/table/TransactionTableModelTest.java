package com.intuit.tank.proxy.table;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;
import com.intuit.tank.handler.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTableModelTest {

    @Test
    @DisplayName("getWebSocketTransactions returns transactions from added sessions")
    public void testGetWebSocketTransactions() {
        TransactionTableModel model = new TransactionTableModel();

        WebSocketSession session1 = new WebSocketSession("ws://example.com/chat", null);
        WebSocketSession session2 = new WebSocketSession("ws://example.com/feed", null);

        model.addWebSocketSession(session1);
        model.addWebSocketSession(session2);

        List<WebSocketTransaction> wsTxns = model.getWebSocketTransactions();
        assertEquals(2, wsTxns.size());
        assertEquals("ws://example.com/chat", wsTxns.get(0).getUrl());
        assertEquals("ws://example.com/feed", wsTxns.get(1).getUrl());
    }

    @Test
    @DisplayName("addWebSocketTransactions loads WS transactions into model")
    public void testAddWebSocketTransactions() {
        TransactionTableModel model = new TransactionTableModel();

        List<WebSocketTransaction> wsTxns = new ArrayList<>();
        WebSocketTransaction wsTx = new WebSocketTransaction("ws://example.com/chat");
        wsTx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hello".getBytes(StandardCharsets.UTF_8), 1000L));
        wsTxns.add(wsTx);

        model.addWebSocketTransactions(wsTxns);

        List<WebSocketTransaction> retrieved = model.getWebSocketTransactions();
        assertEquals(1, retrieved.size());
        assertEquals("ws://example.com/chat", retrieved.get(0).getUrl());
    }

    @Test
    @DisplayName("clear removes WebSocket sessions")
    public void testClearRemovesWebSocketSessions() {
        TransactionTableModel model = new TransactionTableModel();
        model.addWebSocketSession(new WebSocketSession("ws://example.com/chat", null));

        model.clear();

        List<WebSocketTransaction> wsTxns = model.getWebSocketTransactions();
        assertEquals(0, wsTxns.size());
    }
}
