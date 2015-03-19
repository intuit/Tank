package com.intuit.tank.script;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

public enum FailureTypes {
    abortScript("abort", "Abort script, goto next script"),
    skipTestGroup("skipgroup", "Abort script group  goto next script group"),
    skipRemainingRequest("skip", "Skip remaining requests in group"),
    continueNextRequest("continue", "Continue to next request"),
    gotoGroupRequest("goto", "Goto specified  group"),
    terminateUser("kill", "Terminate user"),
    restartUser("restart", "Restart user");

    private String value;
    private String displayName;

    /**
     * @param name
     * @param costModel
     */
    private FailureTypes(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
