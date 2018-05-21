package com.intuit.tank.job;

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
import java.util.List;
import java.util.UUID;

import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.project.Project;

public class ProjectNodeBean extends JobNodeBean {

    private static final long serialVersionUID = 1L;

    private List<ActJobNodeBean> jobBeans = new ArrayList<ActJobNodeBean>();

    public ProjectNodeBean(Project prj) {
        super();
        this.setName(prj.getName());
        this.setJobId("");
        this.setId(String.valueOf(prj.getId()));
        this.setReportMode("");
        this.setStatus("");
        this.setRegion("");
        this.setActiveUsers("");
        this.setNumFailures(new ValidationStatus());
        this.setTotalUsers("");
        this.setStartTime("");
        this.setEndTime("");
    }

    public ProjectNodeBean(String name) {
        super();
        this.setName(name);
        this.setJobId("");
        this.setId(UUID.randomUUID().toString());
        this.setReportMode("");
        this.setStatus("");
        this.setRegion("");
        this.setActiveUsers("");
        this.setNumFailures(new ValidationStatus());
        this.setTotalUsers("");
        this.setStartTime("");
        this.setEndTime("");
    }

    @Override
    public void reCalculate() {
        int tps = jobBeans.stream().mapToInt(JobNodeBean::getTps).sum();
        setTps(tps);
    }

    /**
     * @return the jobBeans
     */
    public List<ActJobNodeBean> getJobBeans() {
        return jobBeans;
    }

    /**
     * @param jobBeans
     *            the jobBeans to set
     */
    public void setJobBeans(List<ActJobNodeBean> jobBeans) {
        this.jobBeans = jobBeans;
    }

    public void addJob(ActJobNodeBean jobNode) {
        jobBeans.add(jobNode);
    }

    @Override
    public List<ActJobNodeBean> getSubNodes() {
        return jobBeans;
    }

    @Override
    public boolean hasSubNodes() {
        if (jobBeans.size() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean isKillable() {
        return false;
    }

    @Override
    public boolean isStopable() {
        return false;
    }

    @Override
    public boolean isRunnable() {
        return false;
    }

    @Override
    public boolean isPausable() {
        return false;
    }

    @Override
    public boolean isRampPausable() {
        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getType() {
        return "project";
    }

}
