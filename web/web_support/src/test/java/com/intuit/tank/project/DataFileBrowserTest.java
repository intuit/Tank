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

import javax.faces.model.SelectItem;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.DataFileBrowser;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * The class <code>DataFileBrowserTest</code> contains tests for the class <code>{@link DataFileBrowser}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class DataFileBrowserTest {
    /**
     * Run the DataFileBrowser() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testDataFileBrowser_1()
        throws Exception {
        DataFileBrowser result = new DataFileBrowser();
        assertNotNull(result);
    }

  

    /**
     * Run the boolean enablePrev() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testEnablePrev_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        boolean result = fixture.enablePrev();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertTrue(result);
    }

    /**
     * Run the boolean enablePrev() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testEnablePrev_2()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(0);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        boolean result = fixture.enablePrev();
        assertTrue(!result);
    }

    /**
     * Run the SelectItem[] getCreatorList() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCreatorList_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        SelectItem[] result = fixture.getCreatorList();
    }

    /**
     * Run the int getCurrentPage() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCurrentPage_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        int result = fixture.getCurrentPage();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertEquals(1, result);
    }

    /**
     * Run the String getDataFileDownloadLink(DataFile) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetDataFileDownloadLink_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        DataFile dataFile = new DataFile();

        String result = fixture.getDataFileDownloadLink(dataFile);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the String getDataFileDownloadLink(DataFile) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetDataFileDownloadLink_2()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        DataFile dataFile = null;

        String result = fixture.getDataFileDownloadLink(dataFile);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }


    /**
     * Run the List<DataFile> getEntityList(ViewFilterType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetEntityList_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<DataFile> result = fixture.getEntityList(viewFilter);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the List<DataFile> getEntityList(ViewFilterType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetEntityList_2()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<DataFile> result = fixture.getEntityList(viewFilter);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }


    /**
     * Run the int getInputPage() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetInputPage_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper(new DataFile()));

        int result = fixture.getInputPage();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertEquals(1, result);
    }

    /**
     * Run the int getNumEntriesToShow() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetNumEntriesToShow_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper(new DataFile()));

        int result = fixture.getNumEntriesToShow();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertEquals(1, result);
    }

    /**
     * Run the SelectableWrapper<DataFile> getSelectedFile() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectedFile_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        SelectableWrapper<DataFile> result = fixture.getSelectedFile();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }



    /**
     * Run the DataFile getViewDatafile() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetViewDatafile_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        DataFile result = fixture.getViewDatafile();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the void goToFirstPage() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGoToFirstPage_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        fixture.goToFirstPage();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }


    /**
     * Run the boolean isCurrent() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsCurrent_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        boolean result = fixture.isCurrent();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertTrue(result);
    }

    /**
     * Run the boolean isCurrent() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsCurrent_2()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        boolean result = fixture.isCurrent();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertTrue(result);
    }

 
    /**
     * Run the void nextSetOfEntries() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testNextSetOfEntries_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        fixture.nextSetOfEntries();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void prevSetOfEntries() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testPrevSetOfEntries_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));

        fixture.prevSetOfEntries();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }


    /**
     * Run the void setCurrentPage(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetCurrentPage_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        int currentPage = 1;

        fixture.setCurrentPage(currentPage);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void setInputPage(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetInputPage_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        int inputPage = 1;

        fixture.setInputPage(inputPage);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void setNumEntriesToShow(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetNumEntriesToShow_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        int numEntriesToShow = 1;

        fixture.setNumEntriesToShow(numEntriesToShow);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void setSelectedFile(SelectableWrapper<DataFile>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectedFile_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        SelectableWrapper<DataFile> selectedFile = new SelectableWrapper((Object) null);

        fixture.setSelectedFile(selectedFile);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void setViewDatafile(DataFile) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetViewDatafile_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        DataFile viewDatafile = new DataFile();

        fixture.setViewDatafile(viewDatafile);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void setViewDatafileId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetViewDatafileId_1()
        throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.setInputPage(1);
        fixture.setNumEntriesToShow(1);
        fixture.setViewDatafile(new DataFile());
        fixture.setCurrentPage(1);
        fixture.setSelectedFile(new SelectableWrapper((Object) null));
        int viewDatafileId = 1;

        fixture.setViewDatafileId(viewDatafileId);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }
}