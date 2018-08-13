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

import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2Impl;

import com.intuit.tank.vm.exception.ParseExceptionConverter;
import com.intuit.tank.vm.exception.WatsParseException;

/**
 * The class <code>ParseExceptionConverterCpTest</code> contains tests for the class
 * <code>{@link ParseExceptionConverter}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class ParseExceptionConverterCpTest {
    /**
     * Run the WatsParseException handleException(Throwable) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testHandleException_1()
            throws Exception {
        Throwable throwable = new Throwable();

        WatsParseException result = ParseExceptionConverter.handleException(throwable);

        assertNotNull(result);
        assertEquals(-1, result.getColumn());
        assertEquals("Error at line:column -1:-1: Dont know what to do about Exception java.lang.Throwable",
                result.getMessage());
        assertEquals(-1, result.getLineNumber());
        assertEquals(
                "com.intuit.tank.vm.exception.WatsParseException: Error at line:column -1:-1: Dont know what to do about Exception java.lang.Throwable",
                result.toString());
        assertEquals("Error at line:column -1:-1: Dont know what to do about Exception java.lang.Throwable",
                result.getLocalizedMessage());
    }

    /**
     * Run the WatsParseException handleException(Throwable) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testHandleException_3()
            throws Exception {
        Throwable throwable = new SAXParseException("", new Locator2Impl());

        WatsParseException result = ParseExceptionConverter.handleException(throwable);

        assertNotNull(result);
        assertEquals(0, result.getColumn());
        assertEquals("Error at line:column 0:0: ", result.getMessage());
        assertEquals(0, result.getLineNumber());
        assertEquals("com.intuit.tank.vm.exception.WatsParseException: Error at line:column 0:0: ",
                result.toString());
        assertEquals("Error at line:column 0:0: ", result.getLocalizedMessage());
    }

    /**
     * Run the WatsParseException handleException(Throwable) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test()
    public void testHandleException_4()
            throws Exception {
        Throwable throwable = assertThrows(com.intuit.tank.vm.exception.WatsParseException.class, () -> new WatsParseException(new Throwable(), "", 1, 1));

        WatsParseException result = ParseExceptionConverter.handleException(throwable);

        assertNotNull(result);
    }
}