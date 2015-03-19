package com.intuit.tank.http.soap;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.httpclient.HttpClient;
import org.junit.*;

import com.intuit.tank.http.soap.SOAPRequest;

import static org.junit.Assert.*;

/**
 * The class <code>SOAPRequestTest</code> contains tests for the class <code>{@link SOAPRequest}</code>.
 *
 * @generatedBy CodePro at 12/16/14 4:29 PM
 */
public class SOAPRequestTest {
    /**
     * Run the SOAPRequest(HttpClient) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSOAPRequest_1()
        throws Exception {
        HttpClient client = new HttpClient();

        SOAPRequest result = new SOAPRequest(client);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        assertNotNull(result);
    }

    /**
     * Run the SOAPRequest(HttpClient) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSOAPRequest_2()
        throws Exception {
        HttpClient client = new HttpClient();

        SOAPRequest result = new SOAPRequest(client);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

    /**
     * Run the SOAPRequest(HttpClient) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSOAPRequest_3()
        throws Exception {
        HttpClient client = new HttpClient();

        SOAPRequest result = new SOAPRequest(client);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

    /**
     * Run the SOAPRequest(HttpClient) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSOAPRequest_4()
        throws Exception {
        HttpClient client = new HttpClient();

        SOAPRequest result = new SOAPRequest(client);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

    /**
     * Run the SOAPRequest(HttpClient) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSOAPRequest_5()
        throws Exception {
        HttpClient client = new HttpClient();

        SOAPRequest result = new SOAPRequest(client);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

    /**
     * Run the String getKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        SOAPRequest fixture = new SOAPRequest(new HttpClient());
        String key = "";

        String result = fixture.getKey(key);
    }

    /**
     * Run the void setKey(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        SOAPRequest fixture = new SOAPRequest(new HttpClient());
        String key = "";
        String value = "";

        fixture.setKey(key, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
    }

    /**
     * Run the void setNamespace(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSetNamespace_1()
        throws Exception {
        SOAPRequest fixture = new SOAPRequest(new HttpClient());
        String name = "";
        String value = "";

        fixture.setNamespace(name, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
    }
}