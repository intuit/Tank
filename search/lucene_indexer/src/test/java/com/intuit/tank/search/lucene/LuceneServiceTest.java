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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.*;

import com.intuit.tank.search.lucene.LuceneService;

import static org.junit.Assert.*;

/**
 * The class <code>LuceneServiceTest</code> contains tests for the class <code>{@link LuceneService}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:36 PM
 */
public class LuceneServiceTest {
    /**
     * Run the LuceneService() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testLuceneService_1()
        throws Exception {

        LuceneService result = new LuceneService();

        assertNotNull(result);
    }

    /**
     * Run the LuceneService() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testLuceneService_2()
        throws Exception {

        LuceneService result = new LuceneService();

        assertNotNull(result);
    }

    /**
     * Run the LuceneService(Directory) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testLuceneService_3()
        throws Exception {
        Directory directory = new RAMDirectory();

        LuceneService result = new LuceneService(directory);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.IllegalArgumentException: Prefix string too short
        //       at java.io.File.createTempFile(File.java:2001)
        //       at java.io.File.createTempFile(File.java:2070)
        assertNotNull(result);
    }

    /**
     * Run the void clearIndex() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testClearIndex_1()
        throws Exception {
        LuceneService fixture = new LuceneService();

        fixture.clearIndex();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.RuntimeException: java.lang.SecurityException: Cannot write to files while generating test cases
        //       at com.intuit.tank.search.lucene.LuceneService.getWriter(LuceneService.java:219)
        //       at com.intuit.tank.search.lucene.LuceneService.clearIndex(LuceneService.java:160)
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
        LuceneService fixture = new LuceneService();
        Document doc = new Document();

        fixture.indexDocument(doc);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.RuntimeException: java.lang.SecurityException: Cannot write to files while generating test cases
        //       at com.intuit.tank.search.lucene.LuceneService.getWriter(LuceneService.java:219)
        //       at com.intuit.tank.search.lucene.LuceneService.indexDocuments(LuceneService.java:76)
        //       at com.intuit.tank.search.lucene.LuceneService.indexDocument(LuceneService.java:67)
    }

    /**
     * Run the void indexDocuments(List<Document>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testIndexDocuments_1()
        throws Exception {
        LuceneService fixture = new LuceneService();
        List<Document> docs = new LinkedList();

        fixture.indexDocuments(docs);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.RuntimeException: java.lang.SecurityException: Cannot write to files while generating test cases
        //       at com.intuit.tank.search.lucene.LuceneService.getWriter(LuceneService.java:219)
        //       at com.intuit.tank.search.lucene.LuceneService.indexDocuments(LuceneService.java:76)
    }

    /**
     * Run the void indexDocuments(List<Document>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testIndexDocuments_2()
        throws Exception {
        LuceneService fixture = new LuceneService();
        List<Document> docs = new LinkedList();

        fixture.indexDocuments(docs);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.RuntimeException: java.lang.SecurityException: Cannot write to files while generating test cases
        //       at com.intuit.tank.search.lucene.LuceneService.getWriter(LuceneService.java:219)
        //       at com.intuit.tank.search.lucene.LuceneService.indexDocuments(LuceneService.java:76)
    }

    /**
     * Run the void removeDocument(Query) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testRemoveDocument_1()
        throws Exception {
        LuceneService fixture = new LuceneService();
        Query query = new BooleanQuery(true);

        fixture.removeDocument(query);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.apache.lucene.search.Query.mergeBooleanQueries(Query.java:182)
    }

    /**
     * Run the void removeDocuments(List<Query>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testRemoveDocuments_1()
        throws Exception {
        LuceneService fixture = new LuceneService();
        List<Query> queries = new LinkedList();

        fixture.removeDocuments(queries);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.RuntimeException: java.lang.SecurityException: Cannot write to files while generating test cases
        //       at com.intuit.tank.search.lucene.LuceneService.getWriter(LuceneService.java:219)
        //       at com.intuit.tank.search.lucene.LuceneService.removeDocuments(LuceneService.java:120)
    }

    /**
     * Run the void removeDocuments(List<Query>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testRemoveDocuments_2()
        throws Exception {
        LuceneService fixture = new LuceneService();
        List<Query> queries = new LinkedList();

        fixture.removeDocuments(queries);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.RuntimeException: java.lang.SecurityException: Cannot write to files while generating test cases
        //       at com.intuit.tank.search.lucene.LuceneService.getWriter(LuceneService.java:219)
        //       at com.intuit.tank.search.lucene.LuceneService.removeDocuments(LuceneService.java:120)
    }

    /**
     * Run the List<Document> search(Query,boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testSearch_1()
        throws Exception {
        LuceneService fixture = new LuceneService();
        Query query = new BooleanQuery();
        boolean prefixWildCard = true;

        List<Document> result = fixture.search(query, prefixWildCard);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.apache.lucene.search.Query.mergeBooleanQueries(Query.java:182)
        assertNotNull(result);
    }

    /**
     * Run the List<Document> search(Query,boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:36 PM
     */
    @Test
    public void testSearch_2()
        throws Exception {
        LuceneService fixture = new LuceneService();
        Query query = new BooleanQuery();
        boolean prefixWildCard = true;

        List<Document> result = fixture.search(query, prefixWildCard);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.apache.lucene.search.Query.mergeBooleanQueries(Query.java:182)
        assertNotNull(result);
    }
}