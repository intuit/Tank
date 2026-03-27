package com.intuit.tank.proxy.table;

import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTableModelTest {

    @Test
    @DisplayName("P1 #24 — loaded WebSocketTransactions are returned with messages intact")
    void loadedWebSocketTransactionsRetainMessages() {
        TransactionTableModel model = new TransactionTableModel();

        // Create a loaded transaction with messages (as if from XML deserialization)
        WebSocketTransaction tx = new WebSocketTransaction("ws://example.com/ws");
        tx.addHandshakeHeader("Host", "example.com");
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hello".getBytes(StandardCharsets.UTF_8), 1000L));
        tx.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT,
                "world".getBytes(StandardCharsets.UTF_8), 1001L));

        // Load transactions (simulates opening a saved recording)
        model.addWebSocketTransactions(List.of(tx));

        // getWebSocketTransactions should return the loaded data with messages intact
        List<WebSocketTransaction> result = model.getWebSocketTransactions();
        assertEquals(1, result.size(), "should have 1 transaction");

        WebSocketTransaction resultTx = result.get(0);
        assertEquals("ws://example.com/ws", resultTx.getUrl());
        assertEquals(2, resultTx.getMessageCount(),
                "loaded transaction should retain its 2 messages, not be an empty shell");
        assertEquals("hello", resultTx.getMessages().get(0).getContentAsString());
        assertEquals("world", resultTx.getMessages().get(1).getContentAsString());
    }

    @Test
    @DisplayName("P1 #24 — loaded and active transactions are both returned by getWebSocketTransactions")
    void mergesLoadedAndActiveTransactions() {
        TransactionTableModel model = new TransactionTableModel();

        // Load a transaction from file
        WebSocketTransaction loaded = new WebSocketTransaction("ws://example.com/loaded");
        loaded.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "from-file".getBytes(StandardCharsets.UTF_8), 1000L));
        model.addWebSocketTransactions(List.of(loaded));

        // getWebSocketTransactions should include the loaded one
        List<WebSocketTransaction> result = model.getWebSocketTransactions();
        assertTrue(result.size() >= 1, "should include loaded transaction");

        boolean foundLoaded = result.stream()
                .anyMatch(tx -> "ws://example.com/loaded".equals(tx.getUrl())
                        && tx.getMessageCount() == 1);
        assertTrue(foundLoaded, "loaded transaction should be in results with messages intact");
    }

    @Test
    @DisplayName("clear removes loaded WebSocket transactions")
    void clearRemovesLoadedTransactions() {
        TransactionTableModel model = new TransactionTableModel();

        WebSocketTransaction tx = new WebSocketTransaction("ws://example.com/ws");
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "test".getBytes(StandardCharsets.UTF_8), 1000L));
        model.addWebSocketTransactions(List.of(tx));

        model.clear();

        List<WebSocketTransaction> result = model.getWebSocketTransactions();
        assertEquals(0, result.size(), "clear should remove loaded transactions");
    }
}
