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
    @DisplayName("P1 — getLoadedWebSocketTransactionForIndex returns transaction at correct row index")
    void getLoadedWebSocketTransactionForIndexReturnsCorrectEntry() {
        TransactionTableModel model = new TransactionTableModel();

        WebSocketTransaction tx1 = new WebSocketTransaction("ws://example.com/a");
        tx1.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "msg-a".getBytes(StandardCharsets.UTF_8), 1000L));
        WebSocketTransaction tx2 = new WebSocketTransaction("ws://example.com/b");
        tx2.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT,
                "msg-b".getBytes(StandardCharsets.UTF_8), 1001L));

        model.addWebSocketTransactions(List.of(tx1, tx2));

        // First loaded WS row is at dataList index 0
        assertTrue(model.isLoadedWebSocketTransaction(0), "index 0 should be a loaded WS transaction");
        assertTrue(model.isLoadedWebSocketTransaction(1), "index 1 should be a loaded WS transaction");

        WebSocketTransaction result0 = model.getLoadedWebSocketTransactionForIndex(0);
        assertNotNull(result0);
        assertEquals("ws://example.com/a", result0.getUrl());
        assertEquals(1, result0.getMessageCount());

        WebSocketTransaction result1 = model.getLoadedWebSocketTransactionForIndex(1);
        assertNotNull(result1);
        assertEquals("ws://example.com/b", result1.getUrl());
    }

    @Test
    @DisplayName("P1 — isLoadedWebSocketTransaction returns false for HTTP rows and live WS sessions")
    void isLoadedWebSocketTransactionReturnsFalseForNonLoadedRows() {
        TransactionTableModel model = new TransactionTableModel();
        assertFalse(model.isLoadedWebSocketTransaction(0), "empty model has no loaded WS rows");
        assertFalse(model.isLoadedWebSocketTransaction(-1), "negative index is not a loaded WS row");
    }

    private com.intuit.tank.conversation.Transaction makeHttpTransaction() {
        com.intuit.tank.conversation.Request req = new com.intuit.tank.conversation.Request();
        req.setProtocol(com.intuit.tank.conversation.Protocol.http);
        req.setFirstLine("GET /path HTTP/1.1");
        req.addHeader(new com.intuit.tank.conversation.Header("Host", "example.com"));
        com.intuit.tank.conversation.Response resp = new com.intuit.tank.conversation.Response();
        resp.setFirstLine("HTTP/1.1 200 OK");
        resp.addHeader(new com.intuit.tank.conversation.Header("Content-Type", "text/plain"));
        com.intuit.tank.conversation.Transaction t = new com.intuit.tank.conversation.Transaction();
        t.setRequest(req);
        t.setResponse(resp);
        return t;
    }

    @Test
    @DisplayName("buildOrderedSession — HTTP, WS, HTTP in insertion order yields seq 0, 1, 2")
    void buildOrderedSessionInterleaved() {
        TransactionTableModel model = new TransactionTableModel();

        com.intuit.tank.conversation.Transaction httpTx1 = makeHttpTransaction();
        model.addTransaction(httpTx1, false);  // index 0

        WebSocketTransaction wsTx = new WebSocketTransaction("ws://example.com/ws");
        wsTx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hi".getBytes(java.nio.charset.StandardCharsets.UTF_8), 1000L));
        model.addWebSocketTransactions(List.of(wsTx));  // index 1

        com.intuit.tank.conversation.Transaction httpTx2 = makeHttpTransaction();
        model.addTransaction(httpTx2, false);  // index 2

        com.intuit.tank.conversation.Session session = model.buildOrderedSession(false);

        // Ordered entries should be: httpTx1(seq=0), wsTx(seq=1), httpTx2(seq=2)
        List<Object> ordered = session.getOrderedEntries();
        assertEquals(3, ordered.size());
        assertTrue(ordered.get(0) instanceof com.intuit.tank.conversation.Transaction);
        assertTrue(ordered.get(1) instanceof WebSocketTransaction);
        assertTrue(ordered.get(2) instanceof com.intuit.tank.conversation.Transaction);

        assertEquals(0, ((com.intuit.tank.conversation.Transaction) ordered.get(0)).getSequenceNumber());
        assertEquals(1, ((WebSocketTransaction) ordered.get(1)).getSequenceNumber());
        assertEquals(2, ((com.intuit.tank.conversation.Transaction) ordered.get(2)).getSequenceNumber());
    }

    @Test
    @DisplayName("buildOrderedSession — HTTP-only produces empty WS list in session")
    void buildOrderedSessionHttpOnly() {
        TransactionTableModel model = new TransactionTableModel();

        model.addTransaction(makeHttpTransaction(), false);

        com.intuit.tank.conversation.Session session = model.buildOrderedSession(true);

        assertEquals(1, session.getTransactions().size());
        assertTrue(session.getWebSocketTransactions().isEmpty());
    }

    @Test
    @DisplayName("buildOrderedSession — WS-only produces empty HTTP list in session")
    void buildOrderedSessionWsOnly() {
        TransactionTableModel model = new TransactionTableModel();

        WebSocketTransaction wsTx = new WebSocketTransaction("ws://example.com/ws");
        model.addWebSocketTransactions(List.of(wsTx));

        com.intuit.tank.conversation.Session session = model.buildOrderedSession(false);

        assertTrue(session.getTransactions().isEmpty());
        assertEquals(1, session.getWebSocketTransactions().size());
    }

    @Test
    @DisplayName("buildOrderedSession — filtered HTTP rows are excluded from the session")
    void buildOrderedSessionExcludesFilteredRows() {
        TransactionTableModel model = new TransactionTableModel();

        com.intuit.tank.conversation.Transaction included = makeHttpTransaction();
        com.intuit.tank.conversation.Transaction filtered = makeHttpTransaction();

        model.addTransaction(included, false);  // not filtered
        model.addTransaction(filtered, true);   // filtered — should be excluded

        com.intuit.tank.conversation.Session session = model.buildOrderedSession(false);

        assertEquals(1, session.getTransactions().size(),
                "Filtered transaction should be excluded from saved session");
        assertSame(included, session.getTransactions().get(0));
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
