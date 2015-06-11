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

public enum VMImageType {
    SUPPORT("Support"),
    CONTROLLER("Controller"),
    AGENT("Agent");

    private String configName;

    /**
     * @param configName
     */
    private VMImageType(String configName) {
        this.configName = configName;
    }

    /**
     * @return the configName
     */
    public String getConfigName() {
        return configName;
    }

}
