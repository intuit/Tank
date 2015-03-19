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

import java.util.ArrayList;
import java.util.LinkedList;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.junit.*;

import static org.junit.Assert.*;

import org.primefaces.extensions.component.dynaform.DynaForm;

import com.intuit.tank.converter.CollectionConverter;

/**
 * The class <code>CollectionConverterTest</code> contains tests for the class <code>{@link CollectionConverter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class CollectionConverterTest {

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
        CollectionConverter fixture = new CollectionConverter();
        Object obj = new ArrayList<String>();

        String result = fixture.getAsString(null, null, obj);
        assertNotNull(result);
    }

    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAsString_2()
        throws Exception {
        CollectionConverter fixture = new CollectionConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        Object obj = new LinkedList();

        String result = fixture.getAsString(facesContext, uiComponent, obj);
        assertNotNull(result);
    }

    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAsString_3()
        throws Exception {
        CollectionConverter fixture = new CollectionConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        Object obj = new LinkedList();

        String result = fixture.getAsString(facesContext, uiComponent, obj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.converter.CollectionConverter.getAsString(CollectionConverter.java:48)
        assertNotNull(result);
    }

    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAsString_4()
        throws Exception {
        CollectionConverter fixture = new CollectionConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        Object obj = new LinkedList();

        String result = fixture.getAsString(facesContext, uiComponent, obj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.converter.CollectionConverter.getAsString(CollectionConverter.java:48)
        assertNotNull(result);
    }
}