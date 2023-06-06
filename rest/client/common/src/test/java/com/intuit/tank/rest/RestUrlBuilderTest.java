/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.rest;

/*
 * #%L
 * Rest Client Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * RestUrlBuilderTest
 * 
 * @author dangleton
 * 
 */
public class RestUrlBuilderTest {

    static Stream<Arguments> patterns() {
        return Stream.of(
                Arguments.of("http://base.url.com/api/v2", "method", "parameter", "http://base.url.com/api/v2/method/parameter"),
                Arguments.of("http://base.url.com/api/v2/", "method", "parameter", "http://base.url.com/api/v2/method/parameter"),
                Arguments.of("http://base.url.com/api/v2/", "/method", "parameter", "http://base.url.com/api/v2/method/parameter"),
                Arguments.of("http://base.url.com/api/v2/", "/method/", "parameter", "http://base.url.com/api/v2/method/parameter"),
                Arguments.of("http://base.url.com/api/v2/", "/method/", "/parameter", "http://base.url.com/api/v2/method/parameter"),
                Arguments.of("http://base.url.com/api/v2/", "/method/", "/parameter/", "http://base.url.com/api/v2/method/parameter/"),
                Arguments.of("http://base.url.com/api/v2", "method", null, "http://base.url.com/api/v2/method"),
                Arguments.of("http://base.url.com/api/v2", "/method", null, "http://base.url.com/api/v2/method"),
                Arguments.of("http://base.url.com/api/v2", "/method/", null, "http://base.url.com/api/v2/method"),
                Arguments.of("http://base.url.com/api/v2", null, null, "http://base.url.com/api/v2")
        );
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("patterns")
    public void testUrlPatterns(String baseUrl, String methodName, String parameter, String expectedResult) {
        RestUrlBuilder restUrlBuilder = new RestUrlBuilder(baseUrl);
        String result = restUrlBuilder.buildUrl(methodName, parameter);
        assertEquals(result, expectedResult);
    }
}
