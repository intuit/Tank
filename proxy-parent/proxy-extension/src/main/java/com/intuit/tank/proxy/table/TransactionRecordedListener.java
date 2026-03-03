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

import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.handler.WebSocketSession;

/**
 * TransactinRecordedListener
 * 
 * @author dangleton
 * 
 */
public interface TransactionRecordedListener {

    public void transactionProcessed(Transaction t, boolean filtered);

    /**
     * Called when a new WebSocket session is started.
     */
    default void webSocketSessionStarted(WebSocketSession session) {}

    /**
     * Called when a WebSocket session receives a new message.
     */
    default void webSocketMessageReceived(WebSocketSession session) {}

    /**
     * Called when a WebSocket session is closed.
     */
    default void webSocketSessionClosed(WebSocketSession session) {}
}
