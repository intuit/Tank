package com.intuit.tank.project;

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
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.ProjectVariableEditor;
import com.intuit.tank.project.VariableEntry;
import com.intuit.tank.project.Workload;

/**
 * The class <code>ProjectVariableEditorTest</code> contains tests for the class <code>{@link ProjectVariableEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ProjectVariableEditorTest {
    /**
     * Run the ProjectVariableEditor() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testProjectVariableEditor_1()
        throws Exception {
        ProjectVariableEditor result = new ProjectVariableEditor();
        assertNotNull(result);
    }

    /**
     * Run the void addEntry() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testAddEntry_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setVariables(new ArrayList<VariableEntry>());
        fixture.setCurrentEntry(new VariableEntry("test", "value"));

        fixture.addEntry();
    }

   

    /**
     * Run the void copyTo(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testCopyTo_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());

        fixture.copyTo(workload);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<init>(IncrementStrategy.java:23)
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<clinit>(IncrementStrategy.java:13)
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
    }

    /**
     * Run the void copyTo(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testCopyTo_2()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());

        fixture.copyTo(workload);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
    }

    /**
     * Run the void delete(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        String key = "";

        fixture.delete(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void delete(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_2()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        String key = "";

        fixture.delete(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void delete(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_3()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        String key = "";

        fixture.delete(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void delete(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_4()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        String key = "";

        fixture.delete(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void delete(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_5()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        String key = "";

        fixture.delete(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void delete(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_6()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        String key = "";

        fixture.delete(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void delete(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDelete_7()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        String key = null;

        fixture.delete(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the VariableEntry getCurrentEntry() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetCurrentEntry_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());

        VariableEntry result = fixture.getCurrentEntry();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
        assertNotNull(result);
    }

    /**
     * Run the List<VariableEntry> getVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetVariables_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());

        List<VariableEntry> result = fixture.getVariables();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
        assertNotNull(result);
    }

  

    /**
     * Run the void newEntry() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testNewEntry_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());

        fixture.newEntry();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

 

    /**
     * Run the void savedVariable(VariableEntry) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSavedVariable_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        VariableEntry variable = new VariableEntry();

        fixture.savedVariable(variable);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void savedVariable(VariableEntry) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSavedVariable_2()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        VariableEntry variable = new VariableEntry("", "");

        fixture.savedVariable(variable);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void savedVariable(VariableEntry) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSavedVariable_3()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        VariableEntry variable = new VariableEntry();

        fixture.savedVariable(variable);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void savedVariable(VariableEntry) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSavedVariable_4()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        VariableEntry variable = new VariableEntry("", "");

        fixture.savedVariable(variable);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void setCurrentEntry(VariableEntry) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetCurrentEntry_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        VariableEntry currentEntry = new VariableEntry();

        fixture.setCurrentEntry(currentEntry);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }

    /**
     * Run the void setVariables(List<VariableEntry>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetVariables_1()
        throws Exception {
        ProjectVariableEditor fixture = new ProjectVariableEditor();
        fixture.setCurrentEntry(new VariableEntry());
        fixture.setVariables(new LinkedList());
        List<VariableEntry> variables = new LinkedList();

        fixture.setVariables(variables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
    }
}