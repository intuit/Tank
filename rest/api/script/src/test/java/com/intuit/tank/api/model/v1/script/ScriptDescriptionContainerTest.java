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
import com.intuit.tank.api.model.v1.script.ScriptDescriptionContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ScriptDescriptionContainerTest</code> contains tests for the class <code>{@link ScriptDescriptionContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class ScriptDescriptionContainerTest {
    /**
     * Run the ScriptDescriptionContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptDescriptionContainer_1()
        throws Exception {

        ScriptDescriptionContainer result = new ScriptDescriptionContainer();

        assertNotNull(result);
        assertEquals(null, result.getScripts());
    }

    /**
     * Run the ScriptDescriptionContainer(List<ScriptDescription>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptDescriptionContainer_2()
        throws Exception {
        List<ScriptDescription> scripts = new LinkedList();

        ScriptDescriptionContainer result = new ScriptDescriptionContainer(scripts);

        assertNotNull(result);
    }

    /**
     * Run the List<ScriptDescription> getScripts() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetScripts_1()
        throws Exception {
        ScriptDescriptionContainer fixture = new ScriptDescriptionContainer(new LinkedList());

        List<ScriptDescription> result = fixture.getScripts();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}