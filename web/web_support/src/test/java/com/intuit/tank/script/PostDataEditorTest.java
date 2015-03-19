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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.script.PostDataEditor;

/**
 * The class <code>PostDataEditorTest</code> contains tests for the class <code>{@link PostDataEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class PostDataEditorTest {
    /**
     * Run the void editPostData(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEditPostData_1()
        throws Exception {
        PostDataEditor fixture = new PostDataEditor();
        fixture.setPostData(new LinkedList());
        Set<RequestData> postData = new HashSet();

        fixture.editPostData(postData);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.PostDataEditor.setPostData(PostDataEditor.java:40)
    }

    /**
     * Run the List<RequestData> getPostData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetPostData_1()
        throws Exception {
        PostDataEditor fixture = new PostDataEditor();
        fixture.setPostData(null);

        List<RequestData> result = fixture.getPostData();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.PostDataEditor.setPostData(PostDataEditor.java:40)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getPostData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetPostData_2()
        throws Exception {
        PostDataEditor fixture = new PostDataEditor();
        fixture.setPostData(new LinkedList());

        List<RequestData> result = fixture.getPostData();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.PostDataEditor.setPostData(PostDataEditor.java:40)
        assertNotNull(result);
    }

    /**
     * Run the Set<RequestData> getRequestDataSet() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRequestDataSet_1()
        throws Exception {
        PostDataEditor fixture = new PostDataEditor();
        fixture.setPostData(new LinkedList());

        Set<RequestData> result = fixture.getRequestDataSet();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.PostDataEditor.setPostData(PostDataEditor.java:40)
        assertNotNull(result);
    }

    /**
     * Run the void insertParameter() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testInsertParameter_1()
        throws Exception {
        PostDataEditor fixture = new PostDataEditor();
        fixture.setPostData(new LinkedList());

        fixture.insertParameter();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.PostDataEditor.setPostData(PostDataEditor.java:40)
    }

    /**
     * Run the void removePostData(RequestData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRemovePostData_1()
        throws Exception {
        PostDataEditor fixture = new PostDataEditor();
        fixture.setPostData(new LinkedList());
        RequestData rd = new RequestData();

        fixture.removePostData(rd);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.PostDataEditor.setPostData(PostDataEditor.java:40)
    }

    /**
     * Run the void setPostData(List<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetPostData_1()
        throws Exception {
        PostDataEditor fixture = new PostDataEditor();
        fixture.setPostData(new LinkedList());
        List<RequestData> postData = new LinkedList();

        fixture.setPostData(postData);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.PostDataEditor.setPostData(PostDataEditor.java:40)
    }
}