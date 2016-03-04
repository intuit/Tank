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

import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.api.model.v1.datafile.DataFileDescriptorContainer;

import static org.junit.Assert.*;

/**
 * The class <code>DataFileDescriptorContainerTest</code> contains tests for the class <code>{@link DataFileDescriptorContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:00 PM
 */
public class DataFileDescriptorContainerTest {
    /**
     * Run the DataFileDescriptorContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testDataFileDescriptorContainer_1()
        throws Exception {

        DataFileDescriptorContainer result = new DataFileDescriptorContainer();

        assertNotNull(result);
    }

    /**
     * Run the DataFileDescriptorContainer(List<DataFileDescriptor>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testDataFileDescriptorContainer_2()
        throws Exception {
        List<DataFileDescriptor> list = new LinkedList<DataFileDescriptor>();

        DataFileDescriptorContainer result = new DataFileDescriptorContainer(list);

        assertNotNull(result);
    }

    /**
     * Run the List<DataFileDescriptor> getDataFiles() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testGetDataFiles_1()
        throws Exception {
        DataFileDescriptorContainer fixture = new DataFileDescriptorContainer(new LinkedList<DataFileDescriptor>());

        List<DataFileDescriptor> result = fixture.getDataFiles();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setDataFiles(List<DataFileDescriptor>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:00 PM
     */
    @Test
    public void testSetDataFiles_1()
        throws Exception {
        DataFileDescriptorContainer fixture = new DataFileDescriptorContainer(new LinkedList<DataFileDescriptor>());
        List<DataFileDescriptor> dataFiles = new LinkedList<DataFileDescriptor>();

        fixture.setDataFiles(dataFiles);

    }
}