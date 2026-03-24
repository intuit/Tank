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

import com.intuit.tank.util.Messages;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    // ===== Mockito-based tests for branch coverage =====

    @InjectMocks
    private ProjectVariableEditor editor;

    @Mock
    private Messages messages;

    @Mock
    private com.intuit.tank.ProjectBean projectBean;

    private AutoCloseable closeable;

    @BeforeEach
    void setUpMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDownMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testAddEntry_WhenValueEmpty_ShowsWarning() {
        editor.setVariables(new ArrayList<>());
        editor.setCurrentEntry(new VariableEntry("key", ""));
        editor.addEntry();
        verify(messages).warn(contains("Value cannot be empty"));
    }

    @Test
    public void testAddEntry_WhenKeyEmpty_ShowsWarning() {
        editor.setVariables(new ArrayList<>());
        editor.setCurrentEntry(new VariableEntry("", "value"));
        editor.addEntry();
        verify(messages).warn(contains("Key cannot be empty"));
    }

    @Test
    public void testAddEntry_WhenDuplicateKey_ShowsWarning() {
        List<VariableEntry> vars = new ArrayList<>();
        vars.add(new VariableEntry("existingKey", "val1"));
        editor.setVariables(vars);
        editor.setCurrentEntry(new VariableEntry("existingkey", "val2")); // equalsIgnoreCase match
        editor.addEntry();
        verify(messages).warn(contains("Duplicate key"));
    }

    @Test
    public void testAddEntry_WhenValid_AddsEntry() {
        List<VariableEntry> vars = new ArrayList<>();
        editor.setVariables(vars);
        editor.setCurrentEntry(new VariableEntry("newKey", "newValue"));
        when(messages.isEmpty()).thenReturn(true);
        editor.addEntry();
        assertEquals(1, vars.size());
        assertEquals("newKey", vars.get(0).getKey());
    }

    @Test
    public void testDelete_WhenKeyExists_RemovesEntry() {
        List<VariableEntry> vars = new ArrayList<>();
        vars.add(new VariableEntry("removeMe", "val"));
        editor.setVariables(vars);
        editor.delete("removeMe");
        assertTrue(vars.isEmpty());
    }

    @Test
    public void testDelete_WhenKeyNotExists_NoChange() {
        List<VariableEntry> vars = new ArrayList<>();
        vars.add(new VariableEntry("keepMe", "val"));
        editor.setVariables(vars);
        editor.delete("notPresent");
        assertEquals(1, vars.size());
    }

    @Test
    public void testDelete_WhenNullKey_DoesNothing() {
        List<VariableEntry> vars = new ArrayList<>();
        vars.add(new VariableEntry("keepMe", "val"));
        editor.setVariables(vars);
        editor.delete(null);
        assertEquals(1, vars.size());
    }

    @Test
    public void testSavedVariable_WhenDuplicateKey_RemovesOldEntry() {
        VariableEntry original = new VariableEntry("dup", "old");
        VariableEntry updated = new VariableEntry("dup", "new");
        List<VariableEntry> vars = new ArrayList<>();
        vars.add(original);
        vars.add(updated);
        editor.setVariables(vars);
        editor.savedVariable(updated); // should remove original (different instance, same key)
        assertEquals(1, vars.size());
        assertEquals("new", vars.get(0).getValue());
    }

    @Test
    public void testNewEntry_SetsCurrentEntry() {
        editor.newEntry();
        assertNotNull(editor.getCurrentEntry());
    }

    @Test
    public void testCopyTo_CopiesVariablesToWorkload() {
        List<VariableEntry> vars = new ArrayList<>();
        vars.add(new VariableEntry("k1", "v1"));
        vars.add(new VariableEntry("k2", "v2"));
        editor.setVariables(vars);

        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        editor.copyTo(workload);
        assertEquals("v1", workload.getJobConfiguration().getVariables().get("k1"));
        assertEquals("v2", workload.getJobConfiguration().getVariables().get("k2"));
    }

    @Test
    public void testInit_LoadsVariablesFromProjectBean() {
        JobConfiguration jc = new JobConfiguration();
        jc.getVariables().put("key1", "val1");
        jc.getVariables().put("key2", "val2");
        when(projectBean.getJobConfiguration()).thenReturn(jc);

        editor.init();

        assertEquals(2, editor.getVariables().size());
    }

    @Test
    public void testInit_WithEmptyVariables() {
        JobConfiguration jc = new JobConfiguration();
        when(projectBean.getJobConfiguration()).thenReturn(jc);

        editor.init();

        assertNotNull(editor.getVariables());
        assertTrue(editor.getVariables().isEmpty());
    }

    @Test
    public void testSave_PersistsVariablesToJobConfiguration() {
        JobConfiguration jc = new JobConfiguration();
        Workload workload = Workload.builder().name("test").build();
        workload.setJobConfiguration(jc);
        when(projectBean.getWorkload()).thenReturn(workload);

        List<VariableEntry> vars = new ArrayList<>();
        vars.add(new VariableEntry("k1", "v1"));
        vars.add(new VariableEntry("k2", "v2"));
        editor.setVariables(vars);
        editor.save();

        assertEquals("v1", jc.getVariables().get("k1"));
        assertEquals("v2", jc.getVariables().get("k2"));
    }

    @Test
    public void testGetVariables_WhenNull_LazyInitFromProjectBean() {
        JobConfiguration jc = new JobConfiguration();
        jc.getVariables().put("lazyKey", "lazyVal");
        Workload workload = Workload.builder().name("test").build();
        workload.setJobConfiguration(jc);
        when(projectBean.getWorkload()).thenReturn(workload);

        List<VariableEntry> result = editor.getVariables();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("lazyKey", result.get(0).getKey());
    }
}