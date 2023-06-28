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

import com.intuit.tank.api.model.v1.script.ScriptStepContainer;
import com.intuit.tank.api.model.v1.script.ScriptStepTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ScriptStepContainerTest</code> contains tests for the class <code>{@link ScriptStepContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class ScriptStepContainerTest {
    /**
     * Run the ScriptStepContainer(List<ScriptStepTO>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptStepContainer_1()
        throws Exception {
        List<ScriptStepTO> steps = new LinkedList();

        ScriptStepContainer result = new ScriptStepContainer(steps);

        assertNotNull(result);
        assertEquals(0, result.getStartIndex());
        assertEquals(0, result.getNumRequsted());
        assertEquals(0, result.getNumRemaining());
        assertEquals(0, result.getNumReturned());
    }

    /**
     * Run the ScriptStepContainer.ScriptStepContainerBuilder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        ScriptStepContainer.ScriptStepContainerBuilder result = ScriptStepContainer.builder();

        assertNotNull(result);
    }

    /**
     * Run the int getNumRemaining() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetNumRemaining_1()
        throws Exception {
        ScriptStepContainer fixture = new ScriptStepContainer(new LinkedList());

        int result = fixture.getNumRemaining();

        assertEquals(0, result);
    }

    /**
     * Run the int getNumRequsted() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetNumRequsted_1()
        throws Exception {
        ScriptStepContainer fixture = new ScriptStepContainer(new LinkedList());

        int result = fixture.getNumRequsted();

        assertEquals(0, result);
    }

    /**
     * Run the int getNumReturned() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetNumReturned_1()
        throws Exception {
        ScriptStepContainer fixture = new ScriptStepContainer(new LinkedList());

        int result = fixture.getNumReturned();

        assertEquals(0, result);
    }

    /**
     * Run the int getStartIndex() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetStartIndex_1()
        throws Exception {
        ScriptStepContainer fixture = new ScriptStepContainer(new LinkedList());

        int result = fixture.getStartIndex();

        assertEquals(0, result);
    }

    /**
     * Run the List<ScriptStepTO> getSteps() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetSteps_1()
        throws Exception {
        ScriptStepContainer fixture = new ScriptStepContainer(new LinkedList());

        List<ScriptStepTO> result = fixture.getSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}