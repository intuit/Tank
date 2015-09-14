package com.intuit.tank.harness;

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

public enum StopBehavior {

    END_OF_STEP("Step", "Stop after current step completes."),
    END_OF_SCRIPT("Script", "Stop after current script completes."),
    END_OF_SCRIPT_GROUP("Script Group", "Stop after current script group completes."),
    END_OF_TEST("Test", "Stop after entire test completes.");

    private String display;
    private String description;

    private StopBehavior(String display, String description) {
        this.display = display;
        this.description = description;
    }

    /**
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    public static StopBehavior fromString(String stopBehavior) {
        StopBehavior ret = StopBehavior.END_OF_SCRIPT_GROUP;
        try {
            ret = valueOf(stopBehavior);
        } catch (Exception e) {
            // bad name return default
        }
        return ret;
    }

}
