package com.intuit.tank.vm.api.enumerated;

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

public enum AgentCommand {
    start("/start"),
    run("/start"),
    stop("/stop"),
    pause("/pause"),
    pause_ramp("/ramp_pause"),
    kill("/kill"),
    status("/staus"),
    request("/request"),
    resume_ramp("/resume");

    private String path;

    /**
     * @param path
     */
    private AgentCommand(String path) {
        this.path = path;
    }

    /**
     * @return the representation
     */
    public String getPath() {
        return path;
    }
}
