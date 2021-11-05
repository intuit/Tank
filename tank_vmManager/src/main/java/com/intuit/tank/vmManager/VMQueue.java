package com.intuit.tank.vmManager;

/*
 * #%L
 * VmManager
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

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VMQueue {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public VMQueue clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}