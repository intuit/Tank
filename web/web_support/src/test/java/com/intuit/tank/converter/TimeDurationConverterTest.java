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

import org.junit.*;

import static org.junit.Assert.*;

import org.primefaces.extensions.component.dynaform.DynaForm;

import com.intuit.tank.converter.TimeDurationConverter;

/**
 * The class <code>TimeDurationConverterTest</code> contains tests for the class <code>{@link TimeDurationConverter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class TimeDurationConverterTest {
    /**
     * Run the TimeDurationConverter() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testTimeDurationConverter_1()
        throws Exception {
        TimeDurationConverter result = new TimeDurationConverter();
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
        TimeDurationConverter fixture = new TimeDurationConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        String value = "";

        Object result = fixture.getAsObject(facesContext, uiComponent, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.converter.TimeDurationConverter.getAsObject(TimeDurationConverter.java:44)
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
    public void testGetAsObject_2()
        throws Exception {
        TimeDurationConverter fixture = new TimeDurationConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        String value = "";

        Object result = fixture.getAsObject(facesContext, uiComponent, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.converter.TimeDurationConverter.getAsObject(TimeDurationConverter.java:44)
        assertNotNull(result);
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
        TimeDurationConverter fixture = new TimeDurationConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        Object obj = new Byte((byte) 1);

        String result = fixture.getAsString(facesContext, uiComponent, obj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.converter.TimeDurationConverter.getAsString(TimeDurationConverter.java:56)
        assertNotNull(result);
    }

    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetAsString_2()
        throws Exception {
        TimeDurationConverter fixture = new TimeDurationConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        Object obj = new Object();

        String result = fixture.getAsString(facesContext, uiComponent, obj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.converter.TimeDurationConverter.getAsString(TimeDurationConverter.java:56)
        assertNotNull(result);
    }
}