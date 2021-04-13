package com.intuit.tank.vm.agent.messages;

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

import com.intuit.tank.vm.api.enumerated.AgentCommand;

public class AgentStopRequest implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -8919376272310847391L;
    int id;
    AgentCommand command;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AgentCommand getCommand() {
        return command;
    }

    public void setCommand(AgentCommand command) {
        this.command = command;
    }

}
