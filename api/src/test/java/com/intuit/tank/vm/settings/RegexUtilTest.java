/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.settings;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.intuit.tank.vm.common.util.RegexUtil;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * AbstractReplacementTest
 * 
 * @author dangleton
 * 
 */
public class RegexUtilTest {

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("query*", "queryOtherStuff", true),
                Arguments.of("query*", "query*", true),
                Arguments.of("query*", "query", true),
                Arguments.of("y\\*", "y*", true),
                Arguments.of("y\\*", "y*Stuff", false),
                Arguments.of("*$100*", "I have $100 Dollars", true),
                Arguments.of("$100*", "I have $100 Dollars", false),
                Arguments.of("y\\\\*", "y\\*", true),
                Arguments.of("y\\\\*", "y\\anything else", true),
                Arguments.of("*", "hello There", true),
                Arguments.of("*", "", true)
        );
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void testPatternConvert(String query, String matchValue, boolean isMatch) throws Exception {
        String pattern = RegexUtil.wildcardToRegexp(query);
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(matchValue);
        assertEquals(m.matches(), isMatch);
    }
}
