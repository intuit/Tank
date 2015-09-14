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

import org.junit.*;

import com.intuit.tank.vm.exception.WatsParseException;

import static org.junit.Assert.*;

/**
 * The class <code>WatsParseExceptionCpTest</code> contains tests for the class <code>{@link WatsParseException}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class WatsParseExceptionCpTest {
    /**
     * Run the WatsParseException(Throwable,String,int,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testWatsParseException_1()
            throws Exception {
        Throwable t = new Throwable();
        String msg = "";
        int lineNumber = 1;
        int column = 1;

        WatsParseException result = new WatsParseException(t, msg, lineNumber, column);

        assertNotNull(result);
        assertEquals(1, result.getColumn());
        assertEquals("Error at line:column 1:1: ", result.getMessage());
        assertEquals(1, result.getLineNumber());
        assertEquals("com.intuit.tank.vm.exception.WatsParseException: Error at line:column 1:1: ",
                result.toString());
        assertEquals("Error at line:column 1:1: ", result.getLocalizedMessage());
    }

    /**
     * Run the int getColumn() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetColumn_1()
            throws Exception {
        WatsParseException fixture = new WatsParseException(new Throwable(), "", 1, 1);
        fixture.addSuppressed(new Throwable());

        int result = fixture.getColumn();

        assertEquals(1, result);
    }

    /**
     * Run the int getLineNumber() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetLineNumber_1()
            throws Exception {
        WatsParseException fixture = new WatsParseException(new Throwable(), "", 1, 1);
        fixture.addSuppressed(new Throwable());

        int result = fixture.getLineNumber();

        assertEquals(1, result);
    }

    /**
     * Run the String getMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetMessage_1()
            throws Exception {
        WatsParseException fixture = new WatsParseException(new Throwable(), "", 1, 1);
        fixture.addSuppressed(new Throwable());

        String result = fixture.getMessage();

        assertEquals("Error at line:column 1:1: ", result);
    }
}