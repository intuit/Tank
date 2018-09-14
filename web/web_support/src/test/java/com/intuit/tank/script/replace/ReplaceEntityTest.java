package com.intuit.tank.script.replace;

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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.search.script.Section;

/**
 * The class <code>ReplaceEntityTest</code> contains tests for the class <code>{@link ReplaceEntity}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:32 PM
 */
public class ReplaceEntityTest {
    /**
     * Run the ReplaceEntity() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceEntity_1()
        throws Exception {
        ReplaceEntity result = new ReplaceEntity();
        assertNotNull(result);
    }

    /**
     * Run the String getAfter() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetAfter_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");

        String result = fixture.getAfter();

        assertEquals("", result);
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getReplaced(ReplaceMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplaced_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");
        ReplaceMode replaceMode = ReplaceMode.KEY;

        String result = fixture.getReplaced(replaceMode);

        assertEquals("", result);
    }

    /**
     * Run the String getReplaced(ReplaceMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplaced_2()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");
        ReplaceMode replaceMode = ReplaceMode.KEY;

        String result = fixture.getReplaced(replaceMode);

        assertEquals("", result);
    }

    /**
     * Run the Section getSection() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetSection_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");

        Section result = fixture.getSection();

        assertEquals(null, result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the void setAfter(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testSetAfter_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");
        String after = "";

        fixture.setAfter(after);

    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the void setSection(Section) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testSetSection_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");
        Section section = null;

        fixture.setSection(section);

    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        ReplaceEntity fixture = new ReplaceEntity();
        fixture.setAfter("");
        fixture.setSection((Section) null);
        fixture.setValue("");
        fixture.setKey("");
        String value = "";

        fixture.setValue(value);

    }
}