/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common.util;

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

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TimingPageNameTest
 * 
 * @author dangleton
 * 
 */
public class TimingPageNameTest {

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("Page ID:||:Page Name:||:14", "Page ID", "Page Name", 14),
                Arguments.of("Page ID", "Page ID", "Page ID", null),
                Arguments.of(":||:Page Name", "Page Name", "Page Name", null),
                Arguments.of(":||:Page Name:||:15", "Page Name", "Page Name", 15),
                Arguments.of("Page ID:||:", "Page ID", "Page ID", null),
                Arguments.of("Page ID:||::||:16", "Page ID", "Page ID", 16),
                Arguments.of("Old Page", "Old Page", "Old Page", null)
        );
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void testParse(String pageIdString, String id, String name, Integer index) {

        TimingPageName timingPageName = new TimingPageName(pageIdString);
        assertEquals(timingPageName.getId(), id);
        assertEquals(timingPageName.getName(), name);
        assertEquals(timingPageName.getIndex(), index);
    }
}
