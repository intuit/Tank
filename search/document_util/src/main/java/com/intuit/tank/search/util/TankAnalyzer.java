/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.search.util;

/*
 * #%L
 * DocumentUtil
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.g
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

/**
 * TutrboscaleAnalyzer. analyzer that combines a LowerCaseFilter filter and WhitespaceTokenizer.
 * 
 * @author dangleton
 * 
 */
public class TankAnalyzer extends Analyzer {

    private final Version matchVersion;

    /**
     * 
     * @param matchVersion
     */
    public TankAnalyzer(Version matchVersion) {
        this.matchVersion = matchVersion;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName,
            final Reader reader) {
        final Tokenizer src = new WhitespaceTokenizer(matchVersion, reader);
        TokenStream tok = new LowerCaseFilter(matchVersion, src);
        return new TokenStreamComponents(src, tok);
    }
}
