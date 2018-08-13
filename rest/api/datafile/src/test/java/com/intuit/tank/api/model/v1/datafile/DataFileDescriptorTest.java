/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.datafile;

/*
 * #%L
 * Datafile Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.testng.Assert;
import org.junit.jupiter.api.Test;

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.test.JaxbUtil;

/**
 * DataFileDescriptorTest
 * 
 * @author dangleton
 * 
 */
public class DataFileDescriptorTest {

    @Test
    public void generateSample() throws Exception {
        DataFileDescriptor df = new DataFileDescriptor();
        df.setComments("Comments");
        df.setCreated(new Date());
        df.setCreator("Denis Angleton");
        df.setId(1);
        df.setModified(new Date());
        df.setName("users.csv");
        String marshall = JaxbUtil.marshall(df);
        DataFileDescriptor unmarshall = JaxbUtil.unmarshall(marshall, DataFileDescriptor.class);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(df, unmarshall));
    }

    /**
     * Run the DataFileDescriptor() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testDataFileDescriptor_1()
        throws Exception {

        DataFileDescriptor result = new DataFileDescriptor();

        assertNotNull(result);
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(null, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreator());
        assertEquals(null, result.getCreated());
        assertEquals(null, result.getDataUrl());
        assertEquals(null, result.getComments());
    }

    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the Date getCreated() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetCreated_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Date result = fixture.getCreated();

        assertNotNull(result);
    }

    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        String result = fixture.getCreator();

        assertEquals("", result);
    }

    /**
     * Run the String getDataUrl() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetDataUrl_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        String result = fixture.getDataUrl();

        assertEquals("", result);
    }

    /**
     * Run the Integer getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Integer result = fixture.getId();

        assertNotNull(result);
        assertEquals("1", result.toString());
        assertEquals((byte) 1, result.byteValue());
        assertEquals((short) 1, result.shortValue());
        assertEquals(1, result.intValue());
        assertEquals(1L, result.longValue());
        assertEquals(1.0f, result.floatValue(), 1.0f);
        assertEquals(1.0, result.doubleValue(), 1.0);
    }

    /**
     * Run the Date getModified() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Date result = fixture.getModified();

        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setCreated(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetCreated_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        Date created = new Date();

        fixture.setCreated(created);

    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        String creator = "";

        fixture.setCreator(creator);

    }

    /**
     * Run the void setDataUrl(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetDataUrl_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        String dataUrl = "";

        fixture.setDataUrl(dataUrl);

    }

    /**
     * Run the void setId(Integer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        Integer id = new Integer(1);

        fixture.setId(id);

    }

    /**
     * Run the void setModified(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetModified_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        Date modified = new Date();

        fixture.setModified(modified);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        DataFileDescriptor fixture = new DataFileDescriptor();
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setName("");
        fixture.setDataUrl("");
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        String result = fixture.toString();

        assertEquals("", result);
    }
}
