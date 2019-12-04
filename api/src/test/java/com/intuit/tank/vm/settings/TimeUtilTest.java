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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TimeUtilTest
 * 
 * @author dangleton
 * 
 */
public class TimeUtilTest {

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("1234", 1234L),
                Arguments.of("1s", 1000L),
                Arguments.of("30s", 1000L * 30),
                Arguments.of("1m", 1000L * 60),
                Arguments.of("1ms", 1L),
                Arguments.of("1h", 1000L * 60 * 60),
                Arguments.of("1m30s", 1000L * 60 + 1000L * 30),
                Arguments.of("1d 1h 30m 10s 50ms", 1000L * 60 * 60 * 24 + 1000L * 60 * 60 + 1000L * 60 * 30 + 1000L * 10 + 50),
                Arguments.of("1d", 1000L * 60 * 60 * 24)
        );
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void testJSONParse(String time, long expected) throws Exception {
        long result = TimeUtil.parseTimeString(time);
        System.out.println(result);
        assertEquals(expected, result);
    }
}
