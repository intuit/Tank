package com.intuit.tank.vm.api.enumerated;

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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>WatsAgentCommandCpTest</code> contains tests for the class <code>{@link AgentCommand}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AgentCommandCpTest {
    /**
     * Run the String getPath() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetPath_1()
            throws Exception {
        AgentCommand fixture = AgentCommand.kill;

        String result = fixture.getPath();

        assertEquals("/kill", result);
    }
}