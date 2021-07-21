/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.exception;

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
 * WatsParseException
 * 
 * @author dangleton
 * 
 */
public class WatsParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private int lineNumber;
    private int column;

    /**
     * @param lineNumber
     * @param column
     */
    public WatsParseException(Throwable t, String msg, int lineNumber, int column) {
        super(msg, t);
        this.lineNumber = lineNumber;
        this.column = column;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Error at line:column " + lineNumber + ":" + column + ": " + super.getMessage();
    }

}
