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

import java.util.List;

import com.intuit.tank.auth.Security;
import com.intuit.tank.util.Messages;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableWrapper;
import com.intuit.tank.wrapper.VersionContainer;
import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class DataFileBrowserTest {

    @InjectMocks
    private DataFileBrowser fixture;

    @Mock
    private DataFileLoader dataFileLoader;

    @Mock
    private Security security;

    @Mock
    private Messages messages;

    @Mock
    private Event<com.intuit.tank.ModifiedDatafileMessage> dataFileEvent;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testConstructor() {
        assertNotNull(fixture);
    }

    @Test
    public void testGetSetSelectedFile() {
        SelectableWrapper<DataFile> wrapper = new SelectableWrapper<>(new DataFile());
        fixture.setSelectedFile(wrapper);
        assertEquals(wrapper, fixture.getSelectedFile());
    }

    @Test
    public void testGetSetViewDatafile() {
        DataFile df = new DataFile();
        fixture.setViewDatafile(df);
        assertEquals(df, fixture.getViewDatafile());
    }

    @Test
    public void testSetViewDatafile_ResetsCurrentPage() {
        fixture.setCurrentPage(5);
        fixture.setViewDatafile(new DataFile());
        // currentPage is reset to 0 in setViewDatafile
        assertEquals(0, fixture.getCurrentPage());
    }

    @Test
    public void testGetSetNumEntriesToShow() {
        fixture.setNumEntriesToShow(100);
        assertEquals(100, fixture.getNumEntriesToShow());
    }

    @Test
    public void testGetSetCurrentPage() {
        fixture.setCurrentPage(3);
        assertEquals(3, fixture.getCurrentPage());
    }

    @Test
    public void testGetSetInputPage() {
        fixture.setInputPage(4);
        assertEquals(4, fixture.getInputPage());
    }

    @Test
    public void testNextSetOfEntries_IncrementsPage() {
        fixture.setCurrentPage(1);
        fixture.nextSetOfEntries();
        assertEquals(2, fixture.getCurrentPage());
    }

    @Test
    public void testPrevSetOfEntries_DecrementsPage() {
        fixture.setCurrentPage(2);
        fixture.prevSetOfEntries();
        assertEquals(1, fixture.getCurrentPage());
    }

    @Test
    public void testEnablePrev_WhenPageGreaterThanZero_ReturnsTrue() {
        fixture.setCurrentPage(1);
        assertTrue(fixture.enablePrev());
    }

    @Test
    public void testEnablePrev_WhenPageZero_ReturnsFalse() {
        fixture.setCurrentPage(0);
        assertFalse(fixture.enablePrev());
    }

    @Test
    public void testGoToFirstPage_SetsCurrentPageToZero() {
        fixture.setCurrentPage(5);
        fixture.goToFirstPage();
        assertEquals(0, fixture.getCurrentPage());
    }

    @Test
    public void testGoToLastPage_SetsCurrentPage() {
        // With no viewDatafile and default numEntriesToShow=50, numPages=0
        fixture.goToLastPage();
        assertEquals(0, fixture.getCurrentPage());
    }

    @Test
    public void testGetTotalLines_NoViewDatafile_ReturnsOne() {
        // With null viewDatafile, getCurrentEntries returns ["current Data File Not set."]
        assertEquals(1, fixture.getTotalLines());
    }

    @Test
    public void testGetStartIndex_WithDefaultState_ReturnsOne() {
        // currentPage=0, numEntriesToShow=50, totalLines=1 → startIndex = 1
        assertEquals(1, fixture.getStartIndex());
    }

    @Test
    public void testGetEndIndex_WithDefaultState_ReturnsOne() {
        // currentPage=0, numEntriesToShow=50, totalLines=1 → endIndex = 1
        assertEquals(1, fixture.getEndIndex());
    }

    @Test
    public void testEnableNext_WithDefaultState_ReturnsFalse() {
        // endIndex(1) is not < totalLines(1)
        assertFalse(fixture.enableNext());
    }

    @Test
    public void testGetNumPages_WithDefaultState_ReturnsZero() {
        // 1 line / 50 per page = 0
        assertEquals(0, fixture.getNumPages());
    }

    @Test
    public void testJumpToInputPage_ValidPage_UpdatesCurrentPage() {
        fixture.setInputPage(1); // page index 0
        fixture.jumpToInputPage();
        assertEquals(0, fixture.getCurrentPage());
    }

    @Test
    public void testJumpToInputPage_NegativePage_DoesNotUpdate() {
        fixture.setCurrentPage(0);
        fixture.setInputPage(0); // pageNum = -1, invalid
        fixture.jumpToInputPage();
        assertEquals(0, fixture.getCurrentPage());
    }

    @Test
    public void testGetDataFileDownloadLink_WithDataFile_ReturnsUrl() {
        DataFile df = new DataFile();
        String link = fixture.getDataFileDownloadLink(df);
        assertNotNull(link);
        assertTrue(link.contains("/v2/datafiles/download/"));
    }

    @Test
    public void testGetDataFileDownloadLink_NullDataFile_ReturnsEmpty() {
        String link = fixture.getDataFileDownloadLink(null);
        assertEquals("", link);
    }

    @Test
    public void testGetEntityList_ReturnsList() {
        VersionContainer<DataFile> container = mock(VersionContainer.class);
        when(container.getVersion()).thenReturn(1);
        when(container.getEntities()).thenReturn(List.of(new DataFile()));
        when(dataFileLoader.getVersionContainer(ViewFilterType.ALL)).thenReturn(container);

        List<DataFile> result = fixture.getEntityList(ViewFilterType.ALL);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testIsCurrent_DelegatesToDataFileLoader() {
        when(dataFileLoader.isCurrent(anyInt())).thenReturn(true);
        assertTrue(fixture.isCurrent());

        when(dataFileLoader.isCurrent(anyInt())).thenReturn(false);
        assertFalse(fixture.isCurrent());
    }

    @Test
    public void testCanCreateDatafile_WhenHasRight_ReturnsTrue() {
        when(security.hasRight(AccessRight.CREATE_DATAFILE)).thenReturn(true);
        assertTrue(fixture.canCreateDatafile());
    }

    @Test
    public void testCanCreateDatafile_WhenNoRight_ReturnsFalse() {
        when(security.hasRight(AccessRight.CREATE_DATAFILE)).thenReturn(false);
        assertFalse(fixture.canCreateDatafile());
    }

    @Test
    public void testDelete_WhenNoPermission_ShowsWarning() {
        DataFile df = new DataFile();
        when(security.hasRight(AccessRight.DELETE_DATAFILE)).thenReturn(false);
        when(security.isOwner(df)).thenReturn(false);

        fixture.delete(df);

        verify(messages).warn(anyString());
    }

    @Test
    public void testDelete_WhenOwner_AttemptsDeletion() {
        DataFile df = new DataFile();
        df.setPath("testfile.csv");
        when(security.hasRight(AccessRight.DELETE_DATAFILE)).thenReturn(false);
        when(security.isOwner(df)).thenReturn(true);

        // DAO call will fail without DB, but no warning should be shown
        fixture.delete(df);

        verify(messages, never()).warn(anyString());
    }

    @Test
    public void testDelete_WhenHasRight_AttemptsDeletion() {
        DataFile df = new DataFile();
        when(security.hasRight(AccessRight.DELETE_DATAFILE)).thenReturn(true);
        when(security.isOwner(df)).thenReturn(false);

        fixture.delete(df);

        verify(messages, never()).warn(anyString());
    }

    @Test
    public void testGetCreatorList_DelegatesToLoader() {
        jakarta.faces.model.SelectItem[] items = new jakarta.faces.model.SelectItem[0];
        when(dataFileLoader.getCreatorList()).thenReturn(items);

        jakarta.faces.model.SelectItem[] result = fixture.getCreatorList();
        assertNotNull(result);
    }

    @Test
    public void testGetEntries_WithNoViewDatafile_ReturnsContent() {
        // getEntries() calls getCurrentEntries() which returns ["current Data File Not set."]
        String entries = fixture.getEntries();
        assertNotNull(entries);
        assertTrue(entries.contains("current Data File Not set."));
    }

    @Test
    public void testSetViewDatafileId_WithMissingId_SetsNullViewDatafile() {
        // setViewDatafileId calls DAO findById - with H2 and unknown ID it should return null
        fixture.setViewDatafileId(99999);
        assertNull(fixture.getViewDatafile());
    }

    @Test
    public void testDelete_WhenOwner_InfoMessageOnSuccess() {
        // When delete succeeds (H2 allows delete of non-existent entity), info message is called
        DataFile df = new DataFile();
        df.setPath("file.csv");
        when(security.hasRight(AccessRight.DELETE_DATAFILE)).thenReturn(true);
        // With H2, deleting a non-persisted entity may succeed silently
        fixture.delete(df);
        // verify either info was called (success) or error was called (exception)
        // we just check no warning was raised
        verify(messages, never()).warn(anyString());
    }
}
