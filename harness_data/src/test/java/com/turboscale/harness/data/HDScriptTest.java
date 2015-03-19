package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;

/**
 * The class <code>HDScriptTest</code> contains tests for the class <code>{@link HDScript}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDScriptTest {
    /**
     * Run the HDScript() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDScript_1()
            throws Exception {
        HDScript result = new HDScript();
        assertNotNull(result);
    }

    /**
     * Run the int getLoop() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetLoop_1()
            throws Exception {
        HDScript fixture = new HDScript();
        fixture.setName("");
        fixture.setParent(new HDScriptGroup());
        fixture.setLoop(1);

        int result = fixture.getLoop();

        assertEquals(1, result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        HDScript fixture = new HDScript();
        fixture.setName("");
        fixture.setParent(new HDScriptGroup());
        fixture.setLoop(1);

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the HDScriptGroup getParent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetParent_1()
            throws Exception {
        HDScript fixture = new HDScript();
        fixture.setName("");
        fixture.setParent(new HDScriptGroup());
        fixture.setLoop(1);

        HDScriptGroup result = fixture.getParent();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(null, result.getParent());
        assertEquals(null, result.getDescription());
        assertEquals(null, result.getVariable());
        assertEquals(0, result.getLoop());
    }

    /**
     * Run the List<HDScriptUseCase> getUseCase() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetUseCase_1()
            throws Exception {
        HDScript fixture = new HDScript();
        fixture.setName("");
        fixture.setParent(new HDScriptGroup());
        fixture.setLoop(1);

        List<HDScriptUseCase> result = fixture.getUseCase();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setLoop(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetLoop_1()
            throws Exception {
        HDScript fixture = new HDScript();
        fixture.setName("");
        fixture.setParent(new HDScriptGroup());
        fixture.setLoop(1);
        int loop = 1;

        fixture.setLoop(loop);

    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        HDScript fixture = new HDScript();
        fixture.setName("");
        fixture.setParent(new HDScriptGroup());
        fixture.setLoop(1);
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setParent(HDScriptGroup) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetParent_1()
            throws Exception {
        HDScript fixture = new HDScript();
        fixture.setName("");
        fixture.setParent(new HDScriptGroup());
        fixture.setLoop(1);
        HDScriptGroup parent = new HDScriptGroup();

        fixture.setParent(parent);

    }
}