package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseQueue {

    private static DatabaseQueue instance = new DatabaseQueue();
    private ExecutorService executor;

    private DatabaseQueue() {
        executor = Executors.newFixedThreadPool(1000);
    }

    public static DatabaseQueue getInstance() {
        return instance;
    }

    public DatabaseQueue clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
