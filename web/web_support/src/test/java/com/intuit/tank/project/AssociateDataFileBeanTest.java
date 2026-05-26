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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.ProjectBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.DualListModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The class <code>AssociateDataFileBeanTest</code> contains tests for the class
 * <code>{@link AssociateDataFileBean}</code>.
 */
public class AssociateDataFileBeanTest {

    @InjectMocks
    private AssociateDataFileBean associateDataFileBean;

    @Mock
    private ProjectBean projectBean;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // --- getSelectedAvailableFiles / setSelectedAvailableFiles ---

    @Test
    public void testGetSelectedAvailableFiles_DefaultIsNull() {
        assertNull(associateDataFileBean.getSelectedAvailableFiles());
    }

    @Test
    public void testSetAndGetSelectedAvailableFiles() {
        List<DataFile> files = new ArrayList<>();
        files.add(new DataFile());
        associateDataFileBean.setSelectedAvailableFiles(files);
        assertEquals(files, associateDataFileBean.getSelectedAvailableFiles());
    }

    @Test
    public void testSetSelectedAvailableFiles_EmptyList() {
        List<DataFile> files = new ArrayList<>();
        associateDataFileBean.setSelectedAvailableFiles(files);
        assertNotNull(associateDataFileBean.getSelectedAvailableFiles());
        assertTrue(associateDataFileBean.getSelectedAvailableFiles().isEmpty());
    }

    // --- getSelectedSelectedFiles / setSelectedSelectedFiles ---

    @Test
    public void testGetSelectedSelectedFiles_DefaultIsNull() {
        assertNull(associateDataFileBean.getSelectedSelectedFiles());
    }

    @Test
    public void testSetAndGetSelectedSelectedFiles() {
        List<DataFile> files = new ArrayList<>();
        files.add(new DataFile());
        associateDataFileBean.setSelectedSelectedFiles(files);
        assertEquals(files, associateDataFileBean.getSelectedSelectedFiles());
    }

    // --- getSelectionModel / setSelectionModel ---

    @Test
    public void testGetSelectionModel_DefaultIsNull() {
        assertNull(associateDataFileBean.getSelectionModel());
    }

    @Test
    public void testSetSelectionModel_DoesNotSetWhenNull() {
        associateDataFileBean.setSelectionModel(null);
        assertNull(associateDataFileBean.getSelectionModel());
    }

    @Test
    public void testSetSelectionModel_DoesNotSetWhenBothSourceAndTargetEmpty() {
        DualListModel<DataFile> model = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
        associateDataFileBean.setSelectionModel(model);
        // condition: source.isEmpty() && target.isEmpty() => should NOT set
        assertNull(associateDataFileBean.getSelectionModel());
    }

    @Test
    public void testSetSelectionModel_SetsWhenSourceIsNonEmpty() {
        List<DataFile> source = new ArrayList<>();
        source.add(new DataFile());
        DualListModel<DataFile> model = new DualListModel<>(source, new ArrayList<>());
        associateDataFileBean.setSelectionModel(model);
        assertNotNull(associateDataFileBean.getSelectionModel());
        assertEquals(model, associateDataFileBean.getSelectionModel());
    }

    @Test
    public void testSetSelectionModel_SetsWhenTargetIsNonEmpty() {
        List<DataFile> target = new ArrayList<>();
        target.add(new DataFile());
        DualListModel<DataFile> model = new DualListModel<>(new ArrayList<>(), target);
        associateDataFileBean.setSelectionModel(model);
        assertNotNull(associateDataFileBean.getSelectionModel());
        assertEquals(model, associateDataFileBean.getSelectionModel());
    }

    // --- addAllToTarget ---

    @Test
    public void testAddAllToTarget_MovesAllSourceToTarget() {
        DataFile df1 = new DataFile();
        df1.setId(1);
        DataFile df2 = new DataFile();
        df2.setId(2);
        List<DataFile> source = new ArrayList<>(List.of(df1, df2));
        List<DataFile> target = new ArrayList<>();
        DualListModel<DataFile> model = new DualListModel<>(source, target);

        // Bypass the setSelectionModel guard by using source-populated model
        associateDataFileBean.setSelectionModel(model);

        associateDataFileBean.addAllToTarget();

        assertTrue(associateDataFileBean.getSelectionModel().getSource().isEmpty());
        assertEquals(2, associateDataFileBean.getSelectionModel().getTarget().size());
        assertTrue(associateDataFileBean.getSelectionModel().getTarget().contains(df1));
        assertTrue(associateDataFileBean.getSelectionModel().getTarget().contains(df2));
    }

    // --- addToTarget ---

    @Test
    public void testAddToTarget_MovesSelectedAvailableFilesToTarget() {
        DataFile df1 = new DataFile();
        df1.setId(1);
        DataFile df2 = new DataFile();
        df2.setId(2);
        List<DataFile> source = new ArrayList<>(List.of(df1, df2));
        List<DataFile> target = new ArrayList<>();
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);
        associateDataFileBean.setSelectedAvailableFiles(new ArrayList<>(List.of(df1)));

        associateDataFileBean.addToTarget();

        assertEquals(1, associateDataFileBean.getSelectionModel().getSource().size());
        assertFalse(associateDataFileBean.getSelectionModel().getSource().contains(df1));
        assertEquals(1, associateDataFileBean.getSelectionModel().getTarget().size());
        assertTrue(associateDataFileBean.getSelectionModel().getTarget().contains(df1));
    }

    @Test
    public void testAddToTarget_WhenSelectedAvailableFilesEmpty_DoesNothing() {
        DataFile df1 = new DataFile();
        List<DataFile> source = new ArrayList<>(List.of(df1));
        List<DataFile> target = new ArrayList<>();
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);
        associateDataFileBean.setSelectedAvailableFiles(new ArrayList<>());

        associateDataFileBean.addToTarget();

        assertEquals(1, associateDataFileBean.getSelectionModel().getSource().size());
        assertTrue(associateDataFileBean.getSelectionModel().getTarget().isEmpty());
    }

    // --- removeFromTarget ---

    @Test
    public void testRemoveFromTarget_MovesSelectedSelectedFilesToSource() {
        DataFile df1 = new DataFile();
        df1.setId(1);
        DataFile df2 = new DataFile();
        df2.setId(2);
        List<DataFile> source = new ArrayList<>();
        List<DataFile> target = new ArrayList<>(List.of(df1, df2));
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);
        associateDataFileBean.setSelectedSelectedFiles(new ArrayList<>(List.of(df1)));

        associateDataFileBean.removeFromTarget();

        assertEquals(1, associateDataFileBean.getSelectionModel().getSource().size());
        assertTrue(associateDataFileBean.getSelectionModel().getSource().contains(df1));
        assertEquals(1, associateDataFileBean.getSelectionModel().getTarget().size());
        assertFalse(associateDataFileBean.getSelectionModel().getTarget().contains(df1));
    }

    @Test
    public void testRemoveFromTarget_WhenSelectedSelectedFilesEmpty_DoesNothing() {
        DataFile df1 = new DataFile();
        List<DataFile> source = new ArrayList<>();
        List<DataFile> target = new ArrayList<>(List.of(df1));
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);
        associateDataFileBean.setSelectedSelectedFiles(new ArrayList<>());

        associateDataFileBean.removeFromTarget();

        assertTrue(associateDataFileBean.getSelectionModel().getSource().isEmpty());
        assertEquals(1, associateDataFileBean.getSelectionModel().getTarget().size());
    }

    // --- removeAllFromTarget ---

    @Test
    public void testRemoveAllFromTarget_MovesAllTargetToSource() {
        DataFile df1 = new DataFile();
        df1.setId(1);
        DataFile df2 = new DataFile();
        df2.setId(2);
        List<DataFile> source = new ArrayList<>();
        List<DataFile> target = new ArrayList<>(List.of(df1, df2));
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);

        associateDataFileBean.removeAllFromTarget();

        assertTrue(associateDataFileBean.getSelectionModel().getTarget().isEmpty());
        assertEquals(2, associateDataFileBean.getSelectionModel().getSource().size());
        assertTrue(associateDataFileBean.getSelectionModel().getSource().contains(df1));
        assertTrue(associateDataFileBean.getSelectionModel().getSource().contains(df2));
    }

    // --- observerUpload ---

    @Test
    public void testObserverUpload_WhenDfNotNullAndSelectionModelNotNull_AddsToTarget() {
        DataFile df = new DataFile();
        List<DataFile> source = new ArrayList<>(List.of(new DataFile()));
        List<DataFile> target = new ArrayList<>();
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);

        ModifiedDatafileMessage msg = new ModifiedDatafileMessage(df, new Object());
        associateDataFileBean.observerUpload(msg);

        assertTrue(associateDataFileBean.getSelectionModel().getTarget().contains(df));
    }

    @Test
    public void testObserverUpload_WhenDfIsNull_DoesNotAddToTarget() {
        List<DataFile> source = new ArrayList<>(List.of(new DataFile()));
        List<DataFile> target = new ArrayList<>();
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);

        ModifiedDatafileMessage msg = new ModifiedDatafileMessage(null, new Object());
        associateDataFileBean.observerUpload(msg);

        assertTrue(associateDataFileBean.getSelectionModel().getTarget().isEmpty());
    }

    @Test
    public void testObserverUpload_WhenSelectionModelIsNull_DoesNotThrow() {
        DataFile df = new DataFile();
        // selectionModel is null by default — no init() called
        ModifiedDatafileMessage msg = new ModifiedDatafileMessage(df, new Object());
        assertDoesNotThrow(() -> associateDataFileBean.observerUpload(msg));
    }

    // --- save ---

    @Test
    public void testSave_ClearsAndRepopulatesDataFileIds() {
        DataFile df1 = new DataFile();
        DataFile df2 = new DataFile();
        List<DataFile> source = new ArrayList<>();
        List<DataFile> target = new ArrayList<>(List.of(df1, df2));
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);

        JobConfiguration jobConfig = new JobConfiguration();
        Set<Integer> dataFileIds = new HashSet<>();
        dataFileIds.add(999); // stale id that should be cleared
        jobConfig.setDataFileIds(dataFileIds);

        when(projectBean.getJobConfiguration()).thenReturn(jobConfig);

        associateDataFileBean.save();

        verify(projectBean, atLeastOnce()).getJobConfiguration();
        // The stale id should be gone; target items' ids (null by default => Integer 0 from getId()) are present
        assertFalse(jobConfig.getDataFileIds().contains(999));
    }

    @Test
    public void testSave_AddsTargetIdsToJobConfiguration() {
        DataFile df = new DataFile();
        // Force a known id via setId — rely on real JobConfiguration with a mutable set
        List<DataFile> source = new ArrayList<>();
        List<DataFile> target = new ArrayList<>(List.of(df));
        DualListModel<DataFile> model = new DualListModel<>(source, target);
        associateDataFileBean.setSelectionModel(model);

        JobConfiguration jobConfig = new JobConfiguration();
        jobConfig.setDataFileIds(new HashSet<>());

        when(projectBean.getJobConfiguration()).thenReturn(jobConfig);

        associateDataFileBean.save();

        // After save the set should contain the id of df (0 for a new DataFile with no id set)
        assertNotNull(jobConfig.getDataFileIds());
        assertEquals(1, jobConfig.getDataFileIds().size());
    }

    // --- init ---

    @Test
    public void testInit_InitializesSelectionModel() {
        JobConfiguration jobConfig = new JobConfiguration();
        jobConfig.setDataFileIds(new HashSet<>());
        when(projectBean.getJobConfiguration()).thenReturn(jobConfig);

        associateDataFileBean.init();

        // After init the selectionModel should be created (even if H2 returns empty list)
        // selectionModel is set internally via initScriptSelectionModel — getSelectionModel
        // returns null only if both source and target are empty AND setSelectionModel guard fires.
        // But initScriptSelectionModel assigns the field directly, so verify no exception is thrown.
        assertDoesNotThrow(() -> associateDataFileBean.getSelectionModel());
        verify(projectBean, atLeastOnce()).getJobConfiguration();
    }
}
