package com.intuit.tank.report;

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

import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.report.JobReport;
import com.intuit.tank.report.JobReportData;
import com.intuit.tank.report.JobReportOptions;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * The class <code>JobReportTest</code> contains tests for the class <code>{@link JobReport}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class JobReportTest {
    /**
     * Run the JobReport() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testJobReport_1()
        throws Exception {
        JobReport result = new JobReport();
        assertNotNull(result);
    }

  

    /**
     * Run the List<JobReportData> getEntityList(ViewFilterType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetEntityList_1()
        throws Exception {
        JobReport fixture = new JobReport();
        fixture.setSelectedResult(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<JobReportData> result = fixture.getEntityList(viewFilter);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.report.JobReport.<init>(JobReport.java:46)
        assertNotNull(result);
    }

    /**
     * Run the JobReportOptions getJobReportOptions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetJobReportOptions_1()
        throws Exception {
        JobReport fixture = new JobReport();
        fixture.setSelectedResult(new SelectableWrapper((Object) null));

        JobReportOptions result = fixture.getJobReportOptions();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.report.JobReport.<init>(JobReport.java:46)
        assertNotNull(result);
    }

    /**
     * Run the SelectableWrapper<JobReportData> getSelectedResult() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectedResult_1()
        throws Exception {
        JobReport fixture = new JobReport();
        fixture.setSelectedResult(new SelectableWrapper((Object) null));

        SelectableWrapper<JobReportData> result = fixture.getSelectedResult();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.report.JobReport.<init>(JobReport.java:46)
        assertNotNull(result);
    }

    /**
     * Run the boolean isCurrent() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsCurrent_1()
        throws Exception {
        JobReport fixture = new JobReport();
        fixture.setSelectedResult(new SelectableWrapper((Object) null));

        boolean result = fixture.isCurrent();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.report.JobReport.<init>(JobReport.java:46)
        assertTrue(result);
    }

    /**
     * Run the void main(String[]) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testMain_1()
        throws Exception {
        String[] args = new String[] {};

        JobReport.main(args);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.report.JobReport.main(JobReport.java:266)
    }

    /**
     * Run the void runReport() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRunReport_1()
        throws Exception {
        JobReport fixture = new JobReport();
        fixture.setSelectedResult(new SelectableWrapper((Object) null));

        fixture.runReport();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.report.JobReport.<init>(JobReport.java:46)
    }

    /**
     * Run the void runReport() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRunReport_2()
        throws Exception {
        JobReport fixture = new JobReport();
        fixture.setSelectedResult(new SelectableWrapper((Object) null));

        fixture.runReport();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.report.JobReport.<init>(JobReport.java:46)
    }

    /**
     * Run the void setSelectedResult(SelectableWrapper<JobReportData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelectedResult_1()
        throws Exception {
        JobReport fixture = new JobReport();
        fixture.setSelectedResult(new SelectableWrapper((Object) null));
        SelectableWrapper<JobReportData> selectedResult = new SelectableWrapper((Object) null);

        fixture.setSelectedResult(selectedResult);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.report.JobReport.<init>(JobReport.java:46)
    }
}