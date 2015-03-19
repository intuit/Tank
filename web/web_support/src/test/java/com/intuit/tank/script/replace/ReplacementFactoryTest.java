package com.intuit.tank.script.replace;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Collection;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.script.replace.AbstractReplacement;
import com.intuit.tank.script.replace.ReplacementFactory;
import com.intuit.tank.search.script.Section;

/**
 * The class <code>ReplacementFactoryTest</code> contains tests for the class <code>{@link ReplacementFactory}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:32 PM
 */
public class ReplacementFactoryTest {
    /**
     * Run the ReplacementFactory() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplacementFactory_1()
        throws Exception {
        ReplacementFactory result = new ReplacementFactory();
        assertNotNull(result);
    }

    /**
     * Run the AbstractReplacement getReplacementForSection(Section) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementForSection_1()
        throws Exception {
        Section section = null;

        AbstractReplacement result = ReplacementFactory.getReplacementForSection(section);

        assertEquals(null, result);
    }

    /**
     * Run the Collection<AbstractReplacement> getReplacementSections() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementSections_1()
        throws Exception {

        Collection<AbstractReplacement> result = ReplacementFactory.getReplacementSections();

        assertNotNull(result);
        assertEquals(29, result.size());
    }
}