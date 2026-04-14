package com.intuit.tank.conversation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "session", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class Session {

    @XmlAttribute(name = "collapseRedirects")
    private boolean collapseRedirects = true;

    @XmlElement(name = "transaction", namespace = Namespace.NAMESPACE_V1)
    private List<Transaction> transactions = new ArrayList<Transaction>();

    @XmlElement(name = "webSocketTransaction", namespace = Namespace.NAMESPACE_V1)
    private List<WebSocketTransaction> webSocketTransactions = new ArrayList<>();

    /**
     *
     */
    public Session() {
        super();
    }

    /**
     * @param transactions
     */
    public Session(List<Transaction> transactions, boolean collapseRedirects) {
        super();
        this.collapseRedirects = collapseRedirects;
        this.transactions = transactions;
    }

    /**
     * @param transactions HTTP transactions
     * @param webSocketTransactions WebSocket transactions
     * @param collapseRedirects whether to collapse redirects
     */
    public Session(List<Transaction> transactions, List<WebSocketTransaction> webSocketTransactions,
                   boolean collapseRedirects) {
        super();
        this.collapseRedirects = collapseRedirects;
        this.transactions = transactions;
        this.webSocketTransactions = webSocketTransactions;
    }

    /**
     * @return the transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @return the webSocketTransactions
     */
    public List<WebSocketTransaction> getWebSocketTransactions() {
        return webSocketTransactions;
    }

    /**
     * @return the collapseRedirects
     */
    public boolean isCollapseRedirects() {
        return collapseRedirects;
    }

    /**
     * Returns all HTTP and WebSocket entries in capture order.
     * <p>
     * If any entry has a valid sequence number (non-null and >= 0), the full list is merged
     * and sorted by seq. Entries with null or negative seq (unset, or written by the old
     * primitive-int implementation before the Integer migration) sort to the end.
     * <p>
     * Falls back to HTTP-first ordering for legacy recordings where no seq was ever assigned.
     */
    public List<Object> getOrderedEntries() {
        boolean hasSeq = transactions.stream().anyMatch(t -> isValidSeq(t.getSequenceNumber()))
                      || webSocketTransactions.stream().anyMatch(w -> isValidSeq(w.getSequenceNumber()));
        if (!hasSeq) {
            List<Object> result = new ArrayList<>(transactions);
            result.addAll(webSocketTransactions);
            return result;
        }
        List<Object> all = new ArrayList<>();
        all.addAll(transactions);
        all.addAll(webSocketTransactions);
        all.sort(Comparator.comparingInt(e -> {
            if (e instanceof Transaction) {
                return seqOrMax(((Transaction) e).getSequenceNumber());
            }
            if (e instanceof WebSocketTransaction) {
                return seqOrMax(((WebSocketTransaction) e).getSequenceNumber());
            }
            return Integer.MAX_VALUE;
        }));
        return all;
    }

    private static boolean isValidSeq(Integer seq) {
        return seq != null && seq >= 0;
    }

    private static int seqOrMax(Integer seq) {
        return isValidSeq(seq) ? seq : Integer.MAX_VALUE;
    }

}
