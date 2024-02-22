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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.util.Messages;

import com.intuit.tank.ModifiedProjectMessage;
import com.intuit.tank.ProjectBean;
import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;
import com.intuit.tank.vm.settings.AccessRight;

@Named
@RequestScoped
public class CreateProjectBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TankSecurityContext securityContext;
    
    @Inject
    private Security security;

    @Inject
    @Modified
    private Event<ModifiedProjectMessage> projectEvent;

    @Inject
    private Messages messages;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private ProjectBean projectBean;

    private String name;
    private String productName;
    private String scriptDriver = ScriptDriver.Tank.name();
    private String comments;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the scriptDriver
     */
    public String getScriptDriver() {
        return scriptDriver;
    }

    /**
     * @param scriptDriver
     *            the scriptDriver to set
     */
    public void setScriptDriver(String scriptDriver) {
        this.scriptDriver = scriptDriver;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * 
     */
    public String createNewProject() {
        Project project = new Project();
        Workload workload = new Workload();

        List<Workload> workloads = new ArrayList<Workload>();
        workload.setName(name);
        workloads.add(workload);
        workload.setParent(project);
        TestPlan testPlan = TestPlan.builder().name("Main").usersPercentage(100).build();
        workload.addTestPlan(testPlan);

        project.setName(name);
        project.setWorkloads(workloads);
        project.setScriptDriver(ScriptDriver.valueOf(scriptDriver));
        project.setComments(getComments());
        project.setProductName(getProductName());
        project.setCreator(securityContext.getCallerPrincipal().getName());
        try {
            project = new ProjectDao().saveOrUpdateProject(project);
            projectEvent.fire(new ModifiedProjectMessage(project, this));
            messages.info("Project " + name + " has been created.");
            projectBean.openProject(project);
        } catch (Throwable t) {
            exceptionHandler.handle(t);
            return null;
        }
        return "success";
    }

    public void cancel() {

    }

    public boolean canCreateProject() {
        return security.hasRight(AccessRight.CREATE_PROJECT);
    }

}
