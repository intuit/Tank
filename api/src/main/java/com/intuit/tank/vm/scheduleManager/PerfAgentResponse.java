package com.intuit.tank.vm.scheduleManager;

/*
 * #%L
 * Intuit Tank Api
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

public class PerfAgentResponse implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -531439215041421731L;

    private String hostName;
    private String queueName;
    private int maxUsers;

    public PerfAgentResponse(String host, String qName, int users) {
        hostName = host;
        queueName = qName;
        maxUsers = users;
    }

    public int getNumberUsers() {
        return maxUsers;
    }

    public String getHost() {
        return hostName;
    }

    public String getQueue() {
        return queueName;
    }

}
