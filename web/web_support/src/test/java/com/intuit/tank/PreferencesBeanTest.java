package com.intuit.tank;

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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang3.time.FastDateFormat;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.Preferences;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;

/**
 * The class <code>PreferencesBeanTest</code> contains tests for the class <code>{@link PreferencesBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
@EnableAutoWeld
@ActivateScopes(SessionScoped.class)
public class PreferencesBeanTest {
    
    @Inject
    private PreferencesBean preferencesBean;
    
    /**
     * Run the PreferencesBean() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testPreferencesBean_1()
        throws Exception {

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(preferencesBean);
    }

    /**
     * Run the String formatDate(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testFormatDate_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        Date date = new Date();

        String result = preferencesBean.formatDate(date);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(result);
    }

    /**
     * Run the String getCollectionFilterString(Collection<? extends Object>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetCollectionFilterString_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        Collection<? extends Object> c = new LinkedList<>();

        String result = preferencesBean.getCollectionFilterString(c);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(result);
    }

    /**
     * Run the String getCollectionFilterString(Collection<? extends Object>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetCollectionFilterString_2()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        Collection<? extends Object> c = new LinkedList();

        String result = preferencesBean.getCollectionFilterString(c);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(result);
    }

    /**
     * Run the String getCollectionFilterString(Collection<? extends Object>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetCollectionFilterString_3()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        Collection<? extends Object> c = new LinkedList<>();

        String result = preferencesBean.getCollectionFilterString(c);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(result);
    }

    /**
     * Run the String getCollectionFilterString(Collection<? extends Object>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetCollectionFilterString_4()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        Collection<? extends Object> c = null;

        String result = preferencesBean.getCollectionFilterString(c);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(result);
    }

    /**
     * Run the FastDateFormat getDateTimeFotmat() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetDateTimeFotmat_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        FastDateFormat result = preferencesBean.getDateTimeFormat();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(result);
    }

    /**
     * Run the Preferences getPreferences() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetPreferences_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        Preferences result = preferencesBean.getPreferences();
    }

    /**
     * Run the int getScreenHeight() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetScreenHeight_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        int result = preferencesBean.getScreenHeight();
    }

    /**
     * Run the int getScreenWidth() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetScreenWidth_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        int result = preferencesBean.getScreenWidth();
    }

    /**
     * Run the FastDateFormat getTimestampFormat() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTimestampFormat_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        FastDateFormat result = preferencesBean.getTimestampFormat();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
        assertNotNull(result);
    }



    /**
     * Run the void prefsChanged() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testPrefsChanged_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        preferencesBean.prefsChanged();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void prefsChanged() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testPrefsChanged_2()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        preferencesBean.prefsChanged();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void prefsChanged() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testPrefsChanged_3()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());

        preferencesBean.prefsChanged();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setDateTimeFotmat(FastDateFormat) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetDateTimeFotmat_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        FastDateFormat dateTimeFotmat = FastDateFormat.getInstance();

        preferencesBean.setDateTimeFotmat(dateTimeFotmat);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setScreenHeight(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetScreenHeight_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        int screenHeight = 1;

        preferencesBean.setScreenHeight(screenHeight);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setScreenSizes(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetScreenSizes_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        String width = "";
        String height = "";

        preferencesBean.setScreenSizes(width, height);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setScreenSizes(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetScreenSizes_2()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        String width = "";
        String height = "";

        preferencesBean.setScreenSizes(width, height);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setScreenSizes(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetScreenSizes_3()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        String width = "";
        String height = "";

        preferencesBean.setScreenSizes(width, height);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setScreenSizes(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetScreenSizes_4()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        String width = "";
        String height = "";

        preferencesBean.setScreenSizes(width, height);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setScreenWidth(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetScreenWidth_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        int screenWidth = 1;

        preferencesBean.setScreenWidth(screenWidth);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }

    /**
     * Run the void setTimestampFormat(FastDateFormat) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetTimestampFormat_1()
        throws Exception {
        preferencesBean.setDateTimeFotmat(FastDateFormat.getInstance());
        preferencesBean.setScreenWidth(1);
        preferencesBean.setScreenHeight(1);
        preferencesBean.setTimestampFormat(FastDateFormat.getInstance());
        FastDateFormat timestampFormat = FastDateFormat.getInstance();

        preferencesBean.setTimestampFormat(timestampFormat);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PreferencesBean.<init>(PreferencesBean.java:179)
    }
}