/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

/**
 * WorkloadType
 * 
 * @author dangleton
 * 
 */
public enum IncrementStrategy {
    increasing("Linear"),
    standard("Nonlinear");

    private String display;

    /**
     * @param display
     */
    private IncrementStrategy(String display) {
        this.display = display;
    }

    /**
     * @return the displayName
     */
    public String getDisplay() {
        return display;
    }

}
