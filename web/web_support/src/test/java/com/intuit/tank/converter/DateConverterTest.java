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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.junit.jupiter.api.Test;
import org.primefaces.extensions.component.dynaform.DynaForm;

import com.intuit.tank.converter.DateConverter;

/**
 * The class <code>DateConverterTest</code> contains tests for the class <code>{@link DateConverter}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class DateConverterTest {
    /**
     * Run the DateConverter() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDateConverter_1()
            throws Exception {
        DateConverter result = new DateConverter();
        assertNotNull(result);
    }

    /**
     * Run the Object getAsObject(FacesContext,UIComponent,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAsObject_1()
            throws Exception {
        DateConverter fixture = new DateConverter();
        String value = fixture.getAsString(null, null, new Date());

        Object result = fixture.getAsObject(null, null, value);
    }

    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAsString_1()
            throws Exception {
        DateConverter fixture = new DateConverter();
        Object obj = new Date();

        String result = fixture.getAsString(null, null, obj);
        assertNotNull(result);
    }
}