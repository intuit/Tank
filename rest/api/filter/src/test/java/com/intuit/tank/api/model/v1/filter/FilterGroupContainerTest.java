package com.intuit.tank.api.model.v1.filter;

/*
 * #%L
 * Filter Rest Api
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

import com.intuit.tank.api.model.v1.filter.FilterGroupContainer;
import com.intuit.tank.api.model.v1.filter.FilterGroupTO;

import static org.junit.Assert.*;

/**
 * The class <code>FilterGroupContainerTest</code> contains tests for the class <code>{@link FilterGroupContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:06 PM
 */
public class FilterGroupContainerTest {
    /**
     * Run the FilterGroupContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testFilterGroupContainer_1()
        throws Exception {

        FilterGroupContainer result = new FilterGroupContainer();

        assertNotNull(result);
    }

    /**
     * Run the FilterGroupContainer(List<FilterGroupTO>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testFilterGroupContainer_2()
        throws Exception {
        List<FilterGroupTO> filterGroups = new LinkedList();

        FilterGroupContainer result = new FilterGroupContainer(filterGroups);

        assertNotNull(result);
    }

    /**
     * Run the List<FilterGroupTO> getFilterGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testGetFilterGroups_1()
        throws Exception {
        FilterGroupContainer fixture = new FilterGroupContainer(new LinkedList());

        List<FilterGroupTO> result = fixture.getFilterGroups();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}