/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.rest;

/*
 * #%L
 * Rest Client Common
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
 * ServiceException
 * 
 * @author dangleton
 * 
 */
public class RestServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int statusCode;

    /**
     * @param arg0
     */
    public RestServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

}
