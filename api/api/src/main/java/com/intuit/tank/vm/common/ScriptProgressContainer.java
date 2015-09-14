/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common;

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

/**
 * ScriptProgressContainer
 * 
 * @author dangleton
 * 
 */
public class ScriptProgressContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    int percentComplete;
    String errorMessage;

    /**
     * @param percentComplete
     * @param script
     * @param errorMessage
     */
    public ScriptProgressContainer(int percentComplete, String errorMessage) {
        super();
        this.percentComplete = percentComplete;
        this.errorMessage = errorMessage;
    }

    /**
     * 
     */
    public ScriptProgressContainer() {
        super();
    }

    /**
     * @return the percentComplete
     */
    public int getPercentComplete() {
        return percentComplete;
    }

    /**
     * @param percentComplete
     *            the percentComplete to set
     */
    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
