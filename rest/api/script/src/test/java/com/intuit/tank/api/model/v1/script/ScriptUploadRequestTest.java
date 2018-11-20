package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptUploadRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ScriptUploadRequestTest</code> contains tests for the class <code>{@link ScriptUploadRequest}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class ScriptUploadRequestTest {
    /**
     * Run the ScriptUploadRequest() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptUploadRequest_1()
        throws Exception {

        ScriptUploadRequest result = new ScriptUploadRequest();

        assertNotNull(result);
        assertEquals(null, result.getScript());
        assertEquals(null, result.getFilterIds());
    }

    /**
     * Run the ScriptUploadRequest(ScriptDescription,List<Integer>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptUploadRequest_2()
        throws Exception {
        ScriptDescription script = new ScriptDescription();
        List<Integer> filterIds = new LinkedList();

        ScriptUploadRequest result = new ScriptUploadRequest(script, filterIds);

        assertNotNull(result);
    }

    /**
     * Run the List<Integer> getFilterIds() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetFilterIds_1()
        throws Exception {
        ScriptUploadRequest fixture = new ScriptUploadRequest(new ScriptDescription(), new LinkedList());

        List<Integer> result = fixture.getFilterIds();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the ScriptDescription getScript() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetScript_1()
        throws Exception {
        ScriptUploadRequest fixture = new ScriptUploadRequest(new ScriptDescription(), new LinkedList());

        ScriptDescription result = fixture.getScript();

        assertNotNull(result);
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(0, result.getRuntime());
        assertEquals(null, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getProductName());
        assertEquals(null, result.getComments());
        assertEquals(null, result.getCreator());
        assertEquals(null, result.getCreated());
    }
}