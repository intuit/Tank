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

import org.junit.*;

import com.intuit.tank.api.model.v1.script.ExternalScriptContainer;
import com.intuit.tank.api.model.v1.script.ExternalScriptTO;

import static org.junit.Assert.*;

/**
 * The class <code>ExternalScriptContainerTest</code> contains tests for the class <code>{@link ExternalScriptContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:10 PM
 */
public class ExternalScriptContainerTest {
    /**
     * Run the ExternalScriptContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:10 PM
     */
    @Test
    public void testExternalScriptContainer_1()
        throws Exception {

        ExternalScriptContainer result = new ExternalScriptContainer();

        assertNotNull(result);
    }

    /**
     * Run the List<ExternalScriptTO> getScripts() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:10 PM
     */
    @Test
    public void testGetScripts_1()
        throws Exception {
        ExternalScriptContainer fixture = new ExternalScriptContainer();
        fixture.setScripts(new LinkedList());

        List<ExternalScriptTO> result = fixture.getScripts();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setScripts(List<ExternalScriptTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:10 PM
     */
    @Test
    public void testSetScripts_1()
        throws Exception {
        ExternalScriptContainer fixture = new ExternalScriptContainer();
        fixture.setScripts(new LinkedList());
        List<ExternalScriptTO> scripts = new LinkedList();

        fixture.setScripts(scripts);

    }
}