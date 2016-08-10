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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.intuit.tank.search.util.SearchConstants;

public class LuceneService {

    private static final Logger LOG = LogManager.getLogger(LuceneService.class);

    private Directory directory;

    /**
     * Initializes the attributes of the instance of the class. Right now it uses the searchDirectory as the default
     * search index location.
     * 
     * TODO: Fetch the search index location from the tank configuration file.
     * 
     * @throws IOException
     */
    public LuceneService() throws IOException {
        this(new SimpleFSDirectory(new File("./searchDirectory")));
    }

    /**
     * initializes the attributes of the instance.
     * 
     * @param directory
     */
    LuceneService(Directory directory) {
        initialize(directory);
    }

    /**
     * Initializes the directory and analyzer to be used for search service.
     * 
     * @param directory
     */
    private void initialize(Directory directory) {
        this.directory = directory;

    }

    /**
     * Indexes a document
     * 
     * @param doc
     */
    public void indexDocument(Document doc) {
        indexDocuments(Arrays.asList(new Document[] { doc }));
    }

    /**
     * Indexes a list of documents.
     * 
     * @param docs
     */
    public void indexDocuments(List<Document> docs) {
        IndexWriter writer = getWriter();
        for (Document document : docs) {
            try {
                writer.addDocument(document);
            } catch (Exception e) {
                e.printStackTrace();
                closeWriter(writer);
                throw new RuntimeException(e);
            }
        }
        closeWriter(writer);
    }

    /**
     * Closes the writer after optimizing the writer. One must call the closeWriter after one is done performing
     * indexing on documents.
     * 
     * @param writer
     */
    private void closeWriter(IndexWriter writer) {
        try {
            writer.optimize();
            writer.close();
        } catch (Exception e1) {
            e1.printStackTrace();
            // throw new RuntimeException(e1);
        }
    }

    /**
     * Removes the index of the document
     * 
     * @param doc
     */
    public void removeDocument(Query query) {
        removeDocuments(Arrays.asList(new Query[] { query }));
    }

    /**
     * Removes the indexes of the documents.
     * 
     * @param docs
     */
    public void removeDocuments(List<Query> queries) {
        IndexWriter writer = getWriter();
        for (Query query : queries) {
            try {
                writer.deleteDocuments(query);
            } catch (Exception e) {
                e.printStackTrace();
                closeWriter(writer);
                throw new RuntimeException(e);
            }
        }
        closeWriter(writer);
    }

    /**
     * Searches through the index for the specified query and returns the list of documents that finds a match for the
     * query.
     * 
     * @param query
     * @return
     */
    public List<Document> search(Query query, boolean prefixWildCard) {
        List<Document> documents = new ArrayList<Document>();
        try {
            IndexSearcher searcher = getSearcher();
            TopDocs search = searcher.search(query, 10000);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                documents.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return documents;
    }

    /**
     * Clears all the indices from the search index location;
     */
    public void clearIndex() {
        IndexWriter writer = getWriter();
        try {
            writer.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeWriter(writer);
        }
    }

    /**
     * Gets a searcher for the search index location
     * 
     * @return
     */
    private IndexSearcher getSearcher() {
        IndexSearcher searcher = null;
        try {
            IndexReader reader = getReader();
            if (!reader.isCurrent()) {
                reader.reopen();
            }
            searcher = new IndexSearcher(reader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return searcher;
    }

    /**
     * Gets a new reader for the search index location
     * 
     * @return
     */
    private IndexReader getReader() {
        try {
            return IndexReader.open(directory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a writer for the search index location
     * 
     * @return
     */
    private IndexWriter getWriter() {
        IndexWriter writer = null;
        try {

            IndexWriterConfig iwc = new IndexWriterConfig(SearchConstants.version, SearchConstants.analyzer);
            IndexWriter.unlock(directory);
            writer = new IndexWriter(directory, iwc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return writer;
    }
}
