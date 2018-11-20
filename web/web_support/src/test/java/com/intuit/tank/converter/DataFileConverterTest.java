package com.intuit.tank.converter;

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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.primefaces.extensions.component.dynaform.DynaForm;

import com.intuit.tank.converter.DataFileConverter;
import com.intuit.tank.project.DataFile;

/**
 * The class <code>DataFileConverterTest</code> contains tests for the class <code>{@link DataFileConverter}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class DataFileConverterTest {
    /**
     * Run the DataFileConverter() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDataFileConverter_1()
            throws Exception {
        DataFileConverter result = new DataFileConverter();
        assertNotNull(result);
    }

    /**
     * Run the Object getAsObject(FacesContext,UIComponent,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetAsObject_1()
            throws Exception {
        DataFileConverter fixture = new DataFileConverter();
        String value = fixture.getAsString(null, null, new DataFile());
        Object result = fixture.getAsObject(null, null, value);
    }


    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetAsString_1()
            throws Exception {
        DataFileConverter fixture = new DataFileConverter();
        Object obj = new DataFile();

        String result = fixture.getAsString(null, null, obj);
        assertNotNull(result);
    }
}