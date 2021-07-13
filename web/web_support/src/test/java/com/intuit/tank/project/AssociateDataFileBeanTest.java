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

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.primefaces.model.DualListModel;

import com.intuit.tank.ModifiedDatafileMessage;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

/**
 * The class <code>AssociateDataFileBeanTest</code> contains tests for the class
 * <code>{@link AssociateDataFileBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class AssociateDataFileBeanTest {
    
    @Inject
    private AssociateDataFileBean associateDataFileBean;
    
    /**
     * Run the AssociateDataFileBean() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testAssociateDataFileBean_1()
            throws Exception {
        assertNotNull(associateDataFileBean);
    }

    /**
     * Run the DualListModel<DataFile> getSelectionModel() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectionModel_1()
            throws Exception {

        DualListModel<DataFile> result = associateDataFileBean.getSelectionModel();

    }

    /**
     * Run the void observerUpload(ModifiedDatafileMessage) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testObserverUpload_1()
            throws Exception {
        ModifiedDatafileMessage msg = new ModifiedDatafileMessage(new DataFile(), new Object());

        associateDataFileBean.observerUpload(msg);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.ModifiedEntityMessage.<init>(ModifiedEntityMessage.java:26)
        // at com.intuit.tank.ModifiedDatafileMessage.<init>(ModifiedDatafileMessage.java:21)
    }

    /**
     * Run the void observerUpload(ModifiedDatafileMessage) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testObserverUpload_2()
            throws Exception {
        ModifiedDatafileMessage msg = new ModifiedDatafileMessage(new DataFile(), new Object());

        associateDataFileBean.observerUpload(msg);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.ModifiedEntityMessage.<init>(ModifiedEntityMessage.java:26)
        // at com.intuit.tank.ModifiedDatafileMessage.<init>(ModifiedDatafileMessage.java:21)
    }

    /**
     * Run the void observerUpload(ModifiedDatafileMessage) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testObserverUpload_3()
            throws Exception {
        ModifiedDatafileMessage msg = new ModifiedDatafileMessage(new DataFile(), new Object());

        associateDataFileBean.observerUpload(msg);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.ModifiedEntityMessage.<init>(ModifiedEntityMessage.java:26)
        // at com.intuit.tank.ModifiedDatafileMessage.<init>(ModifiedDatafileMessage.java:21)
    }

    /**
     * Run the void setSelectionModel(DualListModel<DataFile>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectionModel_1()
            throws Exception {
        DualListModel<DataFile> selectionModel = new DualListModel();

        associateDataFileBean.setSelectionModel(selectionModel);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.AssociateDataFileBean.setSelectionModel(AssociateDataFileBean.java:63)
    }

    /**
     * Run the void setSelectionModel(DualListModel<DataFile>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectionModel_2()
            throws Exception {
        DualListModel<DataFile> selectionModel = new DualListModel();

        associateDataFileBean.setSelectionModel(selectionModel);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.AssociateDataFileBean.setSelectionModel(AssociateDataFileBean.java:63)
    }

    /**
     * Run the void setSelectionModel(DualListModel<DataFile>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectionModel_3()
            throws Exception {
        DualListModel<DataFile> selectionModel = null;

        associateDataFileBean.setSelectionModel(selectionModel);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.AssociateDataFileBean.setSelectionModel(AssociateDataFileBean.java:63)
    }

    /**
     * Run the void setSelectionModel(DualListModel<DataFile>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectionModel_4()
            throws Exception {
        DualListModel<DataFile> selectionModel = new DualListModel();

        associateDataFileBean.setSelectionModel(selectionModel);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.AssociateDataFileBean.setSelectionModel(AssociateDataFileBean.java:63)
    }
}