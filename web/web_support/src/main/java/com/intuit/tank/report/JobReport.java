/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.report;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.PropertyComparer;
import com.intuit.tank.PropertyComparer.SortOrder;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.util.TimeFormatUtil;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.TimeUtil;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * JobReport
 * 
 * @author dangleton
 * 
 */
@Named
@RequestScoped
public class JobReport extends SelectableBean<JobReportData> implements Serializable, Multiselectable<JobReportData> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(JobReport.class);

    private JobReportOptions jobReportOptions = new JobReportOptions();
    private List<JobReportData> results = new ArrayList<JobReportData>();

    private SelectableWrapper<JobReportData> selectedResult;

    /**
     * @return the selectedResult
     */
    public SelectableWrapper<JobReportData> getSelectedResult() {
        return selectedResult;
    }

    /**
     * @param selectedResult
     *            the selectedResult to set
     */
    public void setSelectedResult(SelectableWrapper<JobReportData> selectedResult) {
        this.selectedResult = selectedResult;
    }

    /**
     * @return the jobReportInstance
     */
    public JobReportOptions getJobReportOptions() {
        return jobReportOptions;
    }

    /**
     * @return the results
     */
    public List<JobReportData> getEntityList(ViewFilterType viewFilter) {
        return results;
    }

    public void runReport() {
        List<JobInstance> all = new JobInstanceDao().findAll();
        filterDate(all);
        List<JobReportData> data = getJobReportData(all);
        filterDurationAndName(data);
        this.results = data;
        Collections.sort(this.results, new PropertyComparer<JobReportData>(JobInstance.PROPERTY_ID,
                SortOrder.DESCENDING));
        refresh();
    }

    /**
     * @param data
     */
    private void filterDurationAndName(List<JobReportData> data) {
        if (!StringUtils.isEmpty(jobReportOptions.getDurationStart())) {
            try {
                int duration = findDuration(jobReportOptions.getDurationStart());
                for (Iterator<JobReportData> iter = data.iterator(); iter.hasNext();) {
                    JobReportData job = iter.next();
                    if (duration > job.getDuration()) {
                        iter.remove();
                    }
                }
            } catch (Exception e) {
                LOG.warn("Error with min duration value of " + jobReportOptions.getDurationStart());
            }
        }
        if (!StringUtils.isEmpty(jobReportOptions.getDurationEnd())) {
            try {
                int duration = findDuration(jobReportOptions.getDurationEnd());
                for (Iterator<JobReportData> iter = data.iterator(); iter.hasNext();) {
                    JobReportData job = iter.next();
                    if (duration < job.getDuration()) {
                        iter.remove();
                    }
                }
            } catch (Exception e) {
                LOG.warn("Error with max duration value of " + jobReportOptions.getDurationEnd());
            }
        }
        if (!StringUtils.isEmpty(jobReportOptions.getProjectNameMatch())) {
            try {

                String match = jobReportOptions.getProjectNameMatch().toLowerCase().replace(".", "\\.")
                        .replace("\\", "\\\\").replace("*", ".*");
                for (Iterator<JobReportData> iter = data.iterator(); iter.hasNext();) {
                    JobReportData job = iter.next();
                    if (!job.getProjectName().toLowerCase().matches(match)) {
                        iter.remove();
                    }
                }
            } catch (Exception e) {
                LOG.warn("Error with max duration value of " + jobReportOptions.getDurationEnd());
            }
        }
    }

    /**
     * @param durationEnd
     * @return
     */
    private int findDuration(String duration) {
        int ret = 0;
        if (duration.indexOf(':') != -1) {
            ret = TimeFormatUtil.parseFormattedDuration(duration);
        } else {
            ret = (int) TimeUtil.parseTimeString(duration);
            ret = ret / 1000;
        }
        return ret;
    }

    /**
     * @param all
     * @return
     */
    private List<JobReportData> getJobReportData(List<JobInstance> all) {
        Set<Integer> workloadIds = new HashSet<Integer>();
        for (JobInstance job : all) {
            workloadIds.add(job.getWorkloadId());
        }
        List<Workload> workloads = workloadIds.isEmpty() ? new ArrayList<Workload>() : new WorkloadDao()
                .findForIds(new ArrayList<Integer>(workloadIds));
        Map<Integer, Project> projectMap = new HashMap<Integer, Project>();
        for (Workload w : workloads) {
            projectMap.put(w.getId(), w.getProject());
        }
        List<JobReportData> ret = new ArrayList<JobReportData>();
        for (JobInstance job : all) {
            Project p = projectMap.get(job.getWorkloadId());
            String name = p != null ? p.getName() : "N/A (Project Deleted)";
            ret.add(new JobReportData(name, job));
        }
        return ret;
    }

    /**
     * @param all
     */
    private void filterDate(List<JobInstance> all) {
        Date date = jobReportOptions.getStartTime();
        if (date != null) {
            for (Iterator<JobInstance> iter = all.iterator(); iter.hasNext();) {
                JobInstance job = iter.next();
                Date st = job.getStartTime() != null ? job.getStartTime() : job.getCreated();
                if (!date.before(st)) {
                    iter.remove();
                }
            }
        }
        date = jobReportOptions.getEndTime();
        if (date != null) {
            for (Iterator<JobInstance> iter = all.iterator(); iter.hasNext();) {
                JobInstance job = iter.next();
                Date st = job.getStartTime() != null ? job.getStartTime() : job.getCreated();
                if (!date.after(st)) {
                    iter.remove();
                }
            }
        }
        if (NumberUtils.isDigits(jobReportOptions.getMinUsers())) {
            try {
                int users = Integer.parseInt(jobReportOptions.getMinUsers());
                for (Iterator<JobInstance> iter = all.iterator(); iter.hasNext();) {
                    JobInstance job = iter.next();
                    if (job.getTotalVirtualUsers() < users) {
                        iter.remove();
                    }
                }
            } catch (NumberFormatException e) {
                LOG.warn("Error with min users value of " + jobReportOptions.getMinUsers());
            }
        }
        if (NumberUtils.isDigits(jobReportOptions.getMaxUsers())) {
            try {
                int users = Integer.parseInt(jobReportOptions.getMaxUsers());
                for (Iterator<JobInstance> iter = all.iterator(); iter.hasNext();) {
                    JobInstance job = iter.next();
                    if (job.getTotalVirtualUsers() > users) {
                        iter.remove();
                    }
                }
            } catch (NumberFormatException e) {
                LOG.warn("Error with max users value of " + jobReportOptions.getMaxUsers());
            }
        }
        if (NumberUtils.isDigits(jobReportOptions.getJobIdStart())) {
            try {
                int jobIdStart = NumberUtils.toInt(jobReportOptions.getJobIdStart());
                for (Iterator<JobInstance> iter = all.iterator(); iter.hasNext();) {
                    JobInstance job = iter.next();
                    if (job.getId() < jobIdStart) {
                        iter.remove();
                    }
                }
            } catch (NumberFormatException e) {
                LOG.warn("Error with max users value of " + jobReportOptions.getMaxUsers());
            }
        }
        if (NumberUtils.isDigits(jobReportOptions.getJobIdEnd())) {
            try {
                int jobIdStart = NumberUtils.toInt(jobReportOptions.getJobIdEnd());
                for (Iterator<JobInstance> iter = all.iterator(); iter.hasNext();) {
                    JobInstance job = iter.next();
                    if (job.getId() > jobIdStart) {
                        iter.remove();
                    }
                }
            } catch (NumberFormatException e) {
                LOG.warn("Error with max users value of " + jobReportOptions.getMaxUsers());
            }
        }

    }

    public static void main(String[] args) {
        StringUtils.trim(null);
        System.out.println(NumberUtils.isDigits(StringUtils.trim("123 ")));
        System.out.println(NumberUtils.toInt(StringUtils.trim("123 ")));
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void delete(JobReportData entity) {
        boolean removed = results.remove(entity);
        if (removed) {
            refresh();
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isCurrent() {
        return true;
    }

}
