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

import java.util.LinkedList;
import java.util.List;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.primefaces.model.file.CommonsUploadedFile;
import org.primefaces.model.file.UploadedFile;

import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.script.ScriptCreationBean;
import com.intuit.tank.wrapper.SelectableWrapper;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

/**
 * The class <code>ScriptCreationBeanTest</code> contains tests for the class <code>{@link ScriptCreationBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class ScriptCreationBeanTest {
    
    @Inject
    private ScriptCreationBean scriptCreationBean;
    
    /**
     * Run the ScriptCreationBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testScriptCreationBean_1()
        throws Exception {
        assertNotNull(scriptCreationBean);
    }


    /**
     * Run the void cancel() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    @Disabled
    public void testCancel_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        scriptCreationBean.cancel();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
    }

    /**
     * Run the String createNewScript() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    @Disabled
    public void testCreateNewScript_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        String result = scriptCreationBean.createNewScript();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
        assertNotNull(result);
    }

    /**
     * Run the String getCreationMode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCreationMode_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        String result = scriptCreationBean.getCreationMode();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
        assertNotNull(result);
    }

    /**
     * Run the UploadedFile getFile() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetFile_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        UploadedFile result = scriptCreationBean.getFile();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
        assertNotNull(result);
    }


    /**
     * Run the List<SelectableWrapper<ScriptFilter>> getFilterWrappers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetFilterWrappers_2()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        List<SelectableWrapper<ScriptFilter>> result = scriptCreationBean.getFilterWrappers();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
        assertNotNull(result);
    }


    /**
     * Run the List<SelectableWrapper<ScriptFilterGroup>> getGroupWrappers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetGroupWrappers_2()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        List<SelectableWrapper<ScriptFilterGroup>> result = scriptCreationBean.getGroupWrappers();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        String result = scriptCreationBean.getName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
        assertNotNull(result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());

        String result = scriptCreationBean.getProductName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
        assertNotNull(result);
    }

    /**
     * Run the void setCreationMode(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetCreationMode_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());
        String creationMode = "";

        scriptCreationBean.setCreationMode(creationMode);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
    }

    /**
     * Run the void setFile(UploadedFile) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetFile_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());
        UploadedFile file = new CommonsUploadedFile();

        scriptCreationBean.setFile(file);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
    }

    /**
     * Run the void setFilterWrappers(List<SelectableWrapper<ScriptFilter>>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetFilterWrappers_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());
        List<SelectableWrapper<ScriptFilter>> filterWrappers = new LinkedList<>();

        scriptCreationBean.setFilterWrappers(filterWrappers);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
    }

    /**
     * Run the void setGroupWrappers(List<SelectableWrapper<ScriptFilterGroup>>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetGroupWrappers_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());
        List<SelectableWrapper<ScriptFilterGroup>> groupWrappers = new LinkedList<>();

        scriptCreationBean.setGroupWrappers(groupWrappers);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());
        String name = "";

        scriptCreationBean.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        scriptCreationBean.setFile(new CommonsUploadedFile());
        scriptCreationBean.setProductName("");
        scriptCreationBean.setCreationMode("");
        scriptCreationBean.setFilterWrappers(new LinkedList<>());
        scriptCreationBean.setName("");
        scriptCreationBean.setGroupWrappers(new LinkedList<>());
        String productName = "";

        scriptCreationBean.setProductName(productName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptCreationBean.setFile(ScriptCreationBean.java:107)
    }

}