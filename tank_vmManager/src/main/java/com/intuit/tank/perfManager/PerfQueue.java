package com.intuit.tank.perfManager;

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

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author ahernandez
 * 
 */
@ApplicationScoped
public class PerfQueue implements Serializable {

    private static final long serialVersionUID = 1L;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

}