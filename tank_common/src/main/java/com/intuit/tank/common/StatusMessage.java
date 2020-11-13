package com.intuit.tank.common;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.vm.api.enumerated.AgentCommand;

public class StatusMessage {
    String replyQueue = null;
    AgentCommand status = AgentCommand.run;
    String body = null;

    public StatusMessage(String replyQueue) {
        this.replyQueue = replyQueue;
    }

    public String getReplyQueue() {
        return replyQueue;
    }

    public void setReplyQueue(String replyQueue) {
        this.replyQueue = replyQueue;
    }

    public AgentCommand getStatus() {
        return status;
    }

    public void setStatus(AgentCommand status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
