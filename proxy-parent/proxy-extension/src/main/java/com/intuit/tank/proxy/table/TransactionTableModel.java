/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.table;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.table.DefaultTableModel;

import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.conversation.WebSocketTransaction;
import com.intuit.tank.entity.Application;
import com.intuit.tank.handler.WebSocketSession;
import com.intuit.tank.util.HeaderParser;

/**
 * TransactionTableModel
 * 
 * @author dangleton
 * 
 */
public class TransactionTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    private static final int STATUS_INDEX = 5;
    private static final int FILTERED_INDEX = 6;
    private static final int REDIRECTED_INDEX = 7;

    private List<Transaction> dataList = new ArrayList<Transaction>();
    private static Transaction NULL_TRANSACTION = new Transaction();
    private Set<String> hostSet = new TreeSet<String>();

    // WebSocket session tracking - maps row index to session
    private Map<Integer, WebSocketSession> webSocketSessions = new HashMap<>();

    private static final String[] COLUMN_NAMES = new String[] {
            "#",
            "Method",
            "Path",
            "Url",
            "Content-Type",
            "Status Code",
            "Filtered",
            "wasRedirect"
    };

    public TransactionTableModel() {
        this.setColumnCount(COLUMN_NAMES.length);
        this.setColumnIdentifiers(COLUMN_NAMES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == STATUS_INDEX) {
            return Integer.class;
        } else if (columnIndex == FILTERED_INDEX || columnIndex == REDIRECTED_INDEX) {
            return Boolean.class;
        }
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    public void addTransaction(Transaction t, boolean filtered) {
        HeaderParser req = new HeaderParser(t.getRequest());
        HeaderParser res = new HeaderParser(t.getResponse());
        Vector<Object> rowData = new Vector<Object>(COLUMN_NAMES.length + 1);
        rowData.add(dataList.size() + 1);
        rowData.add(req.getMethod());
        rowData.add(req.getPath());
        rowData.add(req.getUrl(t.getRequest().getProtocol()));
        rowData.add(res.getContentType());
        rowData.add(res.getStatusCode());
        rowData.add(filtered ? Boolean.TRUE : Boolean.FALSE);
        rowData.add(t.getRequest().getHeaders().contains(Application.REDIRECT_MARKER));
        dataList.add(t);
        hostSet.add(req.getHost());
        this.addRow(rowData);
    }

    /**
     * @return the hostSet
     */
    public Set<String> getHostSet() {
        return hostSet;
    }

    /**
     * @param hostSet
     *            the hostSet to set
     */
    public void setHostSet(Set<String> hostSet) {
        this.hostSet = hostSet;
    }

    public void setTransactions(List<Transaction> transactions) {
        clear();
        for (Transaction t : transactions) {
            addTransaction(t, false);
        }
    }

    public Transaction getTransactionForIndex(int index) {
        Transaction ret = null;
        if (index >= 0 && index < dataList.size()) {
            ret = dataList.get(index);
        }
        return ret;
    }

    public void removeTransaction(Transaction t, int rowIndex) {
        if (dataList.remove(t)) {
            dataList.add(rowIndex, NULL_TRANSACTION);
            for (int i = getRowCount(); --i >= 0;) {
                if ((Integer) getValueAt(i, 0) - 1 == rowIndex) {
                    this.removeRow(i);
                    break;
                }
            }
        }
    }

    /**
     * 
     */
    public void clear() {
        dataList.clear();
        hostSet.clear();
        webSocketSessions.clear();
        this.getDataVector().clear();
        this.fireTableDataChanged();
    }

    /**
     * Add a WebSocket session to the table.
     */
    public void addWebSocketSession(WebSocketSession session) {
        int rowIndex = dataList.size();
        Vector<Object> rowData = new Vector<Object>(COLUMN_NAMES.length + 1);
        rowData.add(rowIndex + 1);
        rowData.add("WS");  // Method column
        rowData.add(extractPath(session.getUrl()));  // Path
        rowData.add(session.getUrl());  // URL
        rowData.add("websocket");  // Content-Type
        rowData.add(session.getMessageCount());  // Status = message count
        rowData.add(Boolean.FALSE);  // Not filtered
        rowData.add(Boolean.FALSE);  // Not redirected
        
        dataList.add(NULL_TRANSACTION);  // Placeholder in transaction list
        webSocketSessions.put(rowIndex, session);
        this.addRow(rowData);
    }

    /**
     * Update an existing WebSocket session row (message count changed).
     */
    public void updateWebSocketSession(WebSocketSession session) {
        for (Map.Entry<Integer, WebSocketSession> entry : webSocketSessions.entrySet()) {
            if (entry.getValue() == session) {
                int rowIndex = entry.getKey();
                // Find the actual table row for this data index
                for (int i = 0; i < getRowCount(); i++) {
                    if ((Integer) getValueAt(i, 0) - 1 == rowIndex) {
                        setValueAt(session.getMessageCount(), i, STATUS_INDEX);
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     * Get WebSocket session for a given data index.
     */
    public WebSocketSession getWebSocketSessionForIndex(int index) {
        return webSocketSessions.get(index);
    }

    /**
     * Check if a row is a WebSocket session.
     */
    public boolean isWebSocketSession(int index) {
        return webSocketSessions.containsKey(index);
    }

    private String extractPath(String url) {
        if (url == null) return "/";
        // ws://host:port/path -> /path
        int schemeEnd = url.indexOf("://");
        if (schemeEnd > 0) {
            int pathStart = url.indexOf('/', schemeEnd + 3);
            if (pathStart > 0) {
                return url.substring(pathStart);
            }
        }
        return "/";
    }

    /**
     * Get WebSocket transactions from all tracked sessions.
     */
    public List<WebSocketTransaction> getWebSocketTransactions() {
        List<WebSocketTransaction> result = new ArrayList<>();
        for (WebSocketSession session : webSocketSessions.values()) {
            result.add(session.toTransaction());
        }
        return result;
    }

    /**
     * Load WebSocket transactions into the model (used when opening a saved recording).
     */
    public void addWebSocketTransactions(List<WebSocketTransaction> wsTxns) {
        for (WebSocketTransaction wsTx : wsTxns) {
            WebSocketSession session = new WebSocketSession(wsTx.getUrl(), null);
            addWebSocketSession(session);
        }
    }

    /**
     * @return
     */
    public List<Transaction> getTransactions() {
        List<Transaction> ret = new ArrayList<Transaction>(dataList.size());
        int index = 0;
        for (Transaction t : dataList) {
            if (t != NULL_TRANSACTION && !isRowFiltered(index++)) {
                ret.add(t);
            }
        }
        return ret;
    }

    /**
     * @return
     */
    public List<Transaction> getAllTransactions() {
        return dataList.stream().filter(t -> t != NULL_TRANSACTION).collect(Collectors.toCollection(() -> new ArrayList<>(dataList.size())));
    }

    /**
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean isRowFiltered(int row) {
        Vector v = (Vector) getDataVector().get(row);
        return (Boolean) v.get(TransactionTableModel.FILTERED_INDEX);
    }

    /**
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean isRedirected(int row) {
        Vector v = (Vector) getDataVector().get(row);
        return (Boolean) v.get(TransactionTableModel.REDIRECTED_INDEX);
    }

    /**
     * @return
     */
    @SuppressWarnings("rawtypes")
    public int getStatus(int row) {
        Vector v = (Vector) getDataVector().get(row);
        return (Integer) v.get(TransactionTableModel.STATUS_INDEX);
    }

}
