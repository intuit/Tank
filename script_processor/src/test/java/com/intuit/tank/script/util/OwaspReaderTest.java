package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.FileReader;
import java.io.PipedReader;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.util.OwaspReader;
import com.intuit.tank.vm.exception.WatsParseException;

/**
 * The class <code>OwaspReaderTest</code> contains tests for the class <code>{@link OwaspReader}</code>.
 *
 * @generatedBy CodePro at 12/16/14 4:48 PM
 */
public class OwaspReaderTest {
    /**
     * Run the OwaspReader() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testOwaspReader_1()
        throws Exception {

        OwaspReader result = new OwaspReader();

        assertNotNull(result);
    }

    /**
     * Run the Set<RequestData> rawJsonToSet(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test(expected = org.json.JSONException.class)
    public void testRawJsonToSet_1()
        throws Exception {
        String response = "";

        Set<RequestData> result = OwaspReader.rawJsonToSet(response);

        assertNotNull(result);
    }

    /**
     * Run the Set<RequestData> rawJsonToSet(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test(expected = org.json.JSONException.class)
    public void testRawJsonToSet_2()
        throws Exception {
        String response = "";

        Set<RequestData> result = OwaspReader.rawJsonToSet(response);

        assertNotNull(result);
    }

    /**
     * Run the Set<RequestData> rawJsonToSet(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test(expected = org.json.JSONException.class)
    public void testRawJsonToSet_3()
        throws Exception {
        String response = "";

        Set<RequestData> result = OwaspReader.rawJsonToSet(response);

        assertNotNull(result);
    }

 

    /**
     * Run the List<ScriptStep> read(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test(expected = com.intuit.tank.vm.exception.WatsParseException.class)
    public void testRead_4()
        throws Exception {
        OwaspReader fixture = new OwaspReader();
        String xml = "";

        List<ScriptStep> result = fixture.read(xml);

        assertNotNull(result);
    }

    /**
     * Run the List<ScriptStep> transactionsToRequest(Collection<Transaction>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testTransactionsToRequest_1()
        throws Exception {
        OwaspReader fixture = new OwaspReader();
        Collection<Transaction> entries = new LinkedList();

        List<ScriptStep> result = fixture.transactionsToRequest(entries);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ScriptStep> transactionsToRequest(Collection<Transaction>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testTransactionsToRequest_2()
        throws Exception {
        OwaspReader fixture = new OwaspReader();
        Collection<Transaction> entries = new LinkedList();

        List<ScriptStep> result = fixture.transactionsToRequest(entries);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}