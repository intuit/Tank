package com.intuit.tank.script;

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

import java.io.InputStream;
import java.io.PipedInputStream;

import javax.faces.component.UIComponent;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.extensions.component.dynaform.DynaForm;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFile;

import com.intuit.tank.script.TankXmlUploadBean;

/**
 * The class <code>TankXmlUploadBeanTest</code> contains tests for the class
 * <code>{@link TankXmlUploadBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class TankXmlUploadBeanTest {
    /**
     * Run the TankXmlUploadBean() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testTankXmlUploadBean_1()
            throws Exception {

        TankXmlUploadBean result = new TankXmlUploadBean();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.TankXmlUploadBean.<init>(TankXmlUploadBean.java:55)
        assertNotNull(result);
    }

    /**
     * Run the boolean isUseFlash() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsUseFlash_1()
            throws Exception {
        TankXmlUploadBean fixture = new TankXmlUploadBean();
        fixture.setUseFlash(true);

        boolean result = fixture.isUseFlash();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.TankXmlUploadBean.<init>(TankXmlUploadBean.java:55)
        assertTrue(result);
    }

    /**
     * Run the boolean isUseFlash() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsUseFlash_2()
            throws Exception {
        TankXmlUploadBean fixture = new TankXmlUploadBean();
        fixture.setUseFlash(false);

        boolean result = fixture.isUseFlash();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.script.TankXmlUploadBean.<init>(TankXmlUploadBean.java:55)
        assertTrue(!result);
    }

}