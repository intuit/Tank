package com.intuit.tank.admin;

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

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import com.intuit.tank.admin.SearchIndexer;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>SearchIndexerTest</code> contains tests for the class <code>{@link SearchIndexer}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
@EnableAutoWeld
public class SearchIndexerTest {

    @Inject
    private SearchIndexer searchIndexer;

    /**
     * Run the SearchIndexer() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSearchIndexer_1()
            throws Exception {
        assertNotNull(searchIndexer);
    }

}