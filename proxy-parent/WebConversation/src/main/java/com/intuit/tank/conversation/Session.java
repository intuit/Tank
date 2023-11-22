package com.intuit.tank.conversation;

import java.util.ArrayList;
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
     * @return the transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @return the collapseRedirects
     */
    public boolean isCollapseRedirects() {
        return collapseRedirects;
    }

}
