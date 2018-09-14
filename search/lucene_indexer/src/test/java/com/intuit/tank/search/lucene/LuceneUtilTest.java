package com.intuit.tank.search.lucene;

/*
 * #%L
 * Lucene Indexer
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.junit.jupiter.api.*;

import com.intuit.tank.search.lucene.LuceneUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>LuceneUtilTest</code> contains tests for the class <code>{@link LuceneUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:36 PM
 */
public class LuceneUtilTest {
    /**
     * Run the void clearAllIndices() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testClearAllIndices_1()
        throws Exception {
        LuceneUtil fixture = new LuceneUtil();

        fixture.clearAllIndices();

    }

    /**
     * Run the void indexDocument(Document) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testIndexDocument_1()
        throws Exception {
        LuceneUtil fixture = new LuceneUtil();
        Document doc = new Document();

        fixture.indexDocument(doc);

    }

    /**
     * Run the void removeIndex(Query) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testRemoveIndex_1()
        throws Exception {
        LuceneUtil fixture = new LuceneUtil();
        Query query = null;

        fixture.removeIndex(query);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.apache.lucene.search.Query.mergeBooleanQueries(Query.java:182)
    }

    /**
     * Run the void search(Query) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testSearch_1()
        throws Exception {
        LuceneUtil fixture = new LuceneUtil();

        fixture.search(null);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.apache.lucene.search.Query.mergeBooleanQueries(Query.java:182)
    }
}