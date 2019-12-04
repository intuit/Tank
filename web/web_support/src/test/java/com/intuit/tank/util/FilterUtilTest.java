package com.intuit.tank.util;

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

import java.util.Locale;
import java.util.stream.Stream;

import com.intuit.tank.util.FilterUtil;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterUtilTest {

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("One", "o", true ),
                Arguments.of( "One", "O", true ),
                Arguments.of( "One", "N", true ),
                Arguments.of( "One", "n", true ),
                Arguments.of( "One", "one", true ),
                Arguments.of( "One", "OnE", true ),
                Arguments.of( "One", "OnEx", false ),
                Arguments.of( "One", "f", false ),
                Arguments.of( null, "", true ),
                Arguments.of( null, "d", false ),
                Arguments.of( "Three", null, true )
        );
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void filterByName(Object value, String filterText, boolean expected) {
        boolean filtered = new FilterUtil().contains(value, filterText, Locale.US);
        assertEquals(filtered, expected);
    }
}
