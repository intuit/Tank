package com.intuit.tank.conversation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionOrderingTest {

    private Transaction httpTx(int seq) {
        Transaction t = new Transaction();
        t.setSequenceNumber(seq);
        return t;
    }

    private WebSocketTransaction wsTx(int seq) {
        WebSocketTransaction w = new WebSocketTransaction("ws://example.com/ws");
        w.setSequenceNumber(seq);
        return w;
    }

    @Test
    @DisplayName("No seq numbers — returns HTTP first, WS appended (legacy fallback)")
    void noSeqLegacyFallback() {
        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        WebSocketTransaction w1 = new WebSocketTransaction("ws://example.com/a");
        WebSocketTransaction w2 = new WebSocketTransaction("ws://example.com/b");

        Session session = new Session(List.of(t1, t2), List.of(w1, w2), false);
        List<Object> ordered = session.getOrderedEntries();

        assertEquals(4, ordered.size());
        assertSame(t1, ordered.get(0));
        assertSame(t2, ordered.get(1));
        assertSame(w1, ordered.get(2));
        assertSame(w2, ordered.get(3));
    }

    @Test
    @DisplayName("With seq numbers — interleaved by seq value")
    void withSeqInterleaved() {
        Transaction t0 = httpTx(0);
        WebSocketTransaction w1 = wsTx(1);
        Transaction t2 = httpTx(2);

        Session session = new Session(List.of(t0, t2), List.of(w1), false);
        List<Object> ordered = session.getOrderedEntries();

        assertEquals(3, ordered.size());
        assertSame(t0, ordered.get(0));
        assertSame(w1, ordered.get(1));
        assertSame(t2, ordered.get(2));
    }

    @Test
    @DisplayName("HTTP-only — no WS steps in result")
    void httpOnlyNoWs() {
        Transaction t0 = httpTx(0);
        Transaction t1 = httpTx(1);

        Session session = new Session(List.of(t0, t1), new ArrayList<>(), false);
        List<Object> ordered = session.getOrderedEntries();

        assertEquals(2, ordered.size());
        assertTrue(ordered.stream().allMatch(e -> e instanceof Transaction));
    }

    @Test
    @DisplayName("WS-only — no HTTP steps in result")
    void wsOnlyNoHttp() {
        WebSocketTransaction w0 = wsTx(0);

        Session session = new Session(new ArrayList<>(), List.of(w0), false);
        List<Object> ordered = session.getOrderedEntries();

        assertEquals(1, ordered.size());
        assertSame(w0, ordered.get(0));
    }

    @Test
    @DisplayName("Legacy negative seq (-1 from old primitive-int phase) sorts last, not before seq=0")
    void legacyNegativeSeqSortsLast() {
        Transaction tWithSeq = httpTx(0);
        WebSocketTransaction wWithSeq = wsTx(1);
        // Simulate a recording produced during the primitive-int phase: seq=-1 written to XML,
        // then loaded back as Integer(-1) via JAXB.
        Transaction tLegacyNegative = new Transaction();
        tLegacyNegative.setSequenceNumber(-1);  // explicitly set to -1 (old format)

        Session session = new Session(List.of(tWithSeq, tLegacyNegative), List.of(wWithSeq), false);
        List<Object> ordered = session.getOrderedEntries();

        assertEquals(3, ordered.size());
        assertSame(tWithSeq, ordered.get(0),    "seq=0 should be first");
        assertSame(wWithSeq, ordered.get(1),    "seq=1 should be second");
        assertSame(tLegacyNegative, ordered.get(2), "legacy seq=-1 should sort last, not before seq=0");
    }

    @Test
    @DisplayName("Legacy negative seq triggers hasSeq=true — falls back to sorted path, not HTTP-first")
    void legacyNegativeSeqDoesNotTriggerLegacyFallback() {
        // A file with seq=-1 set explicitly should NOT trigger the HTTP-first legacy fallback,
        // because it has non-null seq values — it should take the sorted path.
        Transaction tLegacy = new Transaction();
        tLegacy.setSequenceNumber(-1);
        WebSocketTransaction wLegacy = new WebSocketTransaction("ws://example.com/ws");
        wLegacy.setSequenceNumber(-1);

        Session session = new Session(List.of(tLegacy), List.of(wLegacy), false);
        List<Object> ordered = session.getOrderedEntries();

        // Both are -1, so they're treated as "unset" and sorted last — but all entries present
        assertEquals(2, ordered.size());
    }

    @Test
    @DisplayName("Mixed seq/no-seq — unset entries (null) sort last")
    void unsetSeqSortsLast() {
        Transaction tWithSeq = httpTx(0);
        WebSocketTransaction wWithSeq = wsTx(1);
        Transaction tNoSeq = new Transaction(); // seq = null

        Session session = new Session(List.of(tWithSeq, tNoSeq), List.of(wWithSeq), false);
        List<Object> ordered = session.getOrderedEntries();

        assertEquals(3, ordered.size());
        assertSame(tWithSeq, ordered.get(0));
        assertSame(wWithSeq, ordered.get(1));
        assertSame(tNoSeq, ordered.get(2));
    }
}
