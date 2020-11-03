package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The class <code>ProjectDTOTest</code> contains tests for the class <code>{@link ProjectDTO}</code>.
 */
public class ProjectDTOTest {

    @Test
    public void testProjectDTO_1() {
        ProjectDTO result = new ProjectDTO();
        result.setId(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void testProjectDTO_2() {
        ProjectDTO result = new ProjectDTO();
        Date date = new Date();
        result.setCreated(date);
        result.setModified(date);
        assertEquals(date, result.getCreated());
        assertEquals(date, result.getModified());
    }

    @Test
    public void testProjectDTO_3() {
        ProjectDTO result = new ProjectDTO();
        result.setCreator("creator");
        result.setName("name");
        assertEquals("creator", result.getCreator());
        assertEquals("name", result.getName());
        assertNotNull(result.toString());
    }

    @Test
    public void testProjectDTO_4() {
        Date date = new Date();
        ProjectDTO result = new ProjectDTO(1, date, date, "creator", "name", null, "pname", "comment");
        result.setCreator("creator");
        result.setName("name");
        assertEquals("creator", result.getCreator());
        assertEquals("name", result.getName());
        assertEquals(1, result.getId());
        assertEquals(date, result.getCreated());
        assertEquals(date, result.getModified());
        assertNotNull(result.toString());
    }
}