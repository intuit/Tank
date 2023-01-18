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

import com.intuit.tank.ModifiedProjectMessage;
import com.intuit.tank.ProjectBean;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.util.Messages;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;


@EnableAutoWeld
@ActivateScopes({RequestScoped.class, SessionScoped.class})
public class CreateProjectBeanTest {

    @Mock
    private ProjectBean projectBean;
    @Mock
    private ProjectDao projectDao;
    @Mock
    private TankSecurityContext securityContext;
    @Mock
    private Event<ModifiedProjectMessage> projectEvent;
    @Mock
    private Messages messages;

    @InjectMocks
    private CreateProjectBean createProjectBean;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }
    @Test
    public void testCreateProjectBean() {
        assertNotNull(createProjectBean);
    }

    @Test
    public void testCreateNewProject() throws Exception {
        // Arrange
        Principal principal = () -> "TESTDUMMY";
        Mockito.when(securityContext.getCallerPrincipal()).thenReturn(principal);
        Mockito.when(projectDao.saveOrUpdateProject(Mockito.any(Project.class))).thenReturn(new Project());
        Mockito.doNothing().when(projectEvent).fire(Mockito.any(ModifiedProjectMessage.class));
        Mockito.doNothing().when(messages).info(Mockito.anyString());
        Mockito.doNothing().when(projectBean).openProject(Mockito.any(Project.class));
        createProjectBean.setName("ProjectName");

        // Act
        String result = createProjectBean.createNewProject();

        // Assert
        assertEquals("success", result);
    }


    /**
     * Run the void cancel() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testCancel_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        createProjectBean.cancel();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }


    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getComments();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getProductName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the String getScriptDriver() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetScriptDriver_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getScriptDriver();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String comments = "";

        createProjectBean.setComments(comments);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String name = "";

        createProjectBean.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String productName = "";

        createProjectBean.setProductName(productName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }

    /**
     * Run the void setScriptDriver(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetScriptDriver_1()
        throws Exception {
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String scriptDriver = "";

        createProjectBean.setScriptDriver(scriptDriver);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }
}