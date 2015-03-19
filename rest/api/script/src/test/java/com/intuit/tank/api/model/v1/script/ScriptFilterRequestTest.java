/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.*;

import static org.junit.Assert.*;

import org.testng.annotations.Test;

import com.intuit.tank.api.model.v1.script.ScriptFilterRequest;

/**
 * DataFileDescriptorTest
 * 
 * @author dangleton
 * 
 */
public class ScriptFilterRequestTest {

    @Test(groups = { "functional" })
    public void generateSample() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(ScriptFilterRequest.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        ScriptFilterRequest jaxbObject = new ScriptFilterRequest(1, Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 }));
        File parent = new File("target/jaxb-sample-xml");
        parent.mkdirs();
        Assert.assertTrue(parent.exists());
        File file = new File(parent, jaxbObject.getClass().getSimpleName() + ".xml");
        marshaller.marshal(jaxbObject, file);
        ScriptFilterRequest unmarshalled = (ScriptFilterRequest) ctx.createUnmarshaller().unmarshal(file);
        Assert.assertEquals(jaxbObject.getScriptId(), unmarshalled.getScriptId());
        Assert.assertEquals(jaxbObject.getFilterIds().size(), unmarshalled.getFilterIds().size());
    }

    /**
     * Run the ScriptFilterRequest() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptFilterRequest_1()
        throws Exception {

        ScriptFilterRequest result = new ScriptFilterRequest();

        assertNotNull(result);
        assertEquals(0, result.getScriptId());
    }

    /**
     * Run the ScriptFilterRequest(int,List<Integer>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptFilterRequest_2()
        throws Exception {
        int scriptId = 1;
        List<Integer> filterIds = new LinkedList();

        ScriptFilterRequest result = new ScriptFilterRequest(scriptId, filterIds);

        assertNotNull(result);
        assertEquals(1, result.getScriptId());
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
        ScriptFilterRequest fixture = new ScriptFilterRequest(1, new LinkedList());

        List<Integer> result = fixture.getFilterIds();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the int getScriptId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetScriptId_1()
        throws Exception {
        ScriptFilterRequest fixture = new ScriptFilterRequest(1, new LinkedList());

        int result = fixture.getScriptId();

        assertEquals(1, result);
    }
}
