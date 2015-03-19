package com.intuit.tank.util;

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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.catalina.core.DummyRequest;
import org.apache.catalina.core.DummyResponse;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.enunciate.modules.jersey.JerseyAdaptedHttpServletRequest;
import org.codehaus.enunciate.modules.jersey.JerseyAdaptedHttpServletResponse;
import org.junit.*;

import static org.junit.Assert.*;

import org.primefaces.webapp.MultipartRequest;

import com.intuit.tank.util.UserNameFilter;
import com.sun.jersey.spi.container.WebApplication;

/**
 * The class <code>UserNameFilterTest</code> contains tests for the class <code>{@link UserNameFilter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class UserNameFilterTest {
    /**
     * Run the void destroy() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDestroy_1()
        throws Exception {
        UserNameFilter fixture = new UserNameFilter();

        fixture.destroy();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.UserNameFilter.destroy(UserNameFilter.java:43)
    }

    /**
     * Run the void doFilter(ServletRequest,ServletResponse,FilterChain) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test(expected = java.io.IOException.class)
    public void testDoFilter_1()
        throws Exception {
        UserNameFilter fixture = new UserNameFilter();
        ServletRequest request = new JerseyAdaptedHttpServletRequest(new MultipartRequest(new DummyRequest(), new ServletFileUpload()), new MediaType());
        ServletResponse response = new JerseyAdaptedHttpServletResponse(new DummyResponse(), (WebApplication) null);
        FilterChain chain = null;

        fixture.doFilter(request, response, chain);

    }

    /**
     * Run the void doFilter(ServletRequest,ServletResponse,FilterChain) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test(expected = java.io.IOException.class)
    public void testDoFilter_2()
        throws Exception {
        UserNameFilter fixture = new UserNameFilter();
        ServletRequest request = new JerseyAdaptedHttpServletRequest(new MultipartRequest(new DummyRequest(), new ServletFileUpload()), new MediaType());
        ServletResponse response = new JerseyAdaptedHttpServletResponse(new DummyResponse(), (WebApplication) null);
        FilterChain chain = null;

        fixture.doFilter(request, response, chain);

    }

    /**
     * Run the void doFilter(ServletRequest,ServletResponse,FilterChain) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test(expected = java.io.IOException.class)
    public void testDoFilter_3()
        throws Exception {
        UserNameFilter fixture = new UserNameFilter();
        ServletRequest request = new JerseyAdaptedHttpServletRequest(new MultipartRequest(new DummyRequest(), new ServletFileUpload()), new MediaType());
        ServletResponse response = new JerseyAdaptedHttpServletResponse(new DummyResponse(), (WebApplication) null);
        FilterChain chain = null;

        fixture.doFilter(request, response, chain);

    }

    /**
     * Run the void doFilter(ServletRequest,ServletResponse,FilterChain) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test(expected = java.io.IOException.class)
    public void testDoFilter_4()
        throws Exception {
        UserNameFilter fixture = new UserNameFilter();
        ServletRequest request = new JerseyAdaptedHttpServletRequest(new MultipartRequest(new DummyRequest(), new ServletFileUpload()), new MediaType());
        ServletResponse response = new JerseyAdaptedHttpServletResponse(new DummyResponse(), (WebApplication) null);
        FilterChain chain = null;

        fixture.doFilter(request, response, chain);

    }

    /**
     * Run the void doFilter(ServletRequest,ServletResponse,FilterChain) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test(expected = java.io.IOException.class)
    public void testDoFilter_5()
        throws Exception {
        UserNameFilter fixture = new UserNameFilter();
        ServletRequest request = new JerseyAdaptedHttpServletRequest(new MultipartRequest(new DummyRequest(), new ServletFileUpload()), new MediaType());
        ServletResponse response = new JerseyAdaptedHttpServletResponse(new DummyResponse(), (WebApplication) null);
        FilterChain chain = null;

        fixture.doFilter(request, response, chain);

    }

    /**
     * Run the void init(FilterConfig) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testInit_1()
        throws Exception {
        UserNameFilter fixture = new UserNameFilter();
        FilterConfig filterConfig = null;

        fixture.init(filterConfig);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.UserNameFilter.init(UserNameFilter.java:26)
    }
}