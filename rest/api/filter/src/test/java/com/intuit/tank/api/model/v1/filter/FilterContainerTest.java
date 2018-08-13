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

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.filter.FilterContainer;
import com.intuit.tank.api.model.v1.filter.FilterTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>FilterContainerTest</code> contains tests for the class <code>{@link FilterContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:06 PM
 */
public class FilterContainerTest {
    /**
     * Run the FilterContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testFilterContainer_1()
        throws Exception {

        FilterContainer result = new FilterContainer();

        assertNotNull(result);
    }

    /**
     * Run the FilterContainer(List<FilterTO>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testFilterContainer_2()
        throws Exception {
        List<FilterTO> filters = new LinkedList();

        FilterContainer result = new FilterContainer(filters);

        assertNotNull(result);
    }

    /**
     * Run the List<FilterTO> getFilters() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testGetFilters_1()
        throws Exception {
        FilterContainer fixture = new FilterContainer(new LinkedList());

        List<FilterTO> result = fixture.getFilters();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}