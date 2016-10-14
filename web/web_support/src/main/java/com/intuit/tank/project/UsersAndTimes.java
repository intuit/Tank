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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.seam.international.status.Messages;

import com.intuit.tank.ProjectBean;
import com.intuit.tank.dao.JobConfigurationDao;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.TimeUtil;

/**
 * 
 * UsersAndTimes
 * 
 * @author dangleton
 * 
 */
@Named
@ConversationScoped
public class UsersAndTimes implements Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(UsersAndTimes.class);

    @Inject
    private ProjectBean projectBean;

    private TankConfig tankConfig = new TankConfig();

    @Inject
    private Messages messages;

    private List<JobRegion> jobRegions;

    /**
     * initializes the instance variables with the project and workload
     * 
     * @param project
     *            the value of the project that should be assigned.
     * @param workload
     *            the value of the workload that should be assigned.
     */
    public void init() {
        getJobRegions();
    }

    public List<JobRegion> getJobRegions() {
        if (jobRegions == null) {

            Set<JobRegion> regions = projectBean.getJobConfiguration().getJobRegions();

            Set<VMRegion> configuredRegions = new HashSet<VMRegion>(tankConfig.getVmManagerConfig()
                    .getConfiguredRegions());
            if (tankConfig.getStandalone()) {
                JobRegion standaloneRegion = new JobRegion(VMRegion.STANDALONE, "0");
                boolean found = false;
                for (JobRegion region : regions) {
                    if (region.getRegion() == VMRegion.US_EAST || region.getRegion() == VMRegion.STANDALONE) {
                        standaloneRegion = region;
                        standaloneRegion.setRegion(VMRegion.STANDALONE);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    regions.clear();
                    regions.add(standaloneRegion);
                }
            } else {
                for (JobRegion region : regions) {
                    configuredRegions.remove(region.getRegion());
                }
                for (VMRegion region : configuredRegions) {
                    if (tankConfig.getStandalone()) {

                    } else {
                        regions.add(new JobRegion(region, "0"));
                    }
                }
            }
            jobRegions = new ArrayList<JobRegion>(regions);
            Collections.sort(jobRegions);
        }
        return jobRegions;

    }

    //
    // /**
    // * @return The number of users in the east region.
    // */
    // public String getEastUsers() {
    // return getUsersForRegion(VMRegion.US_EAST);
    // }
    //
    // /**
    // * Sets the number of east users
    // *
    // * @param eastUsers
    // * The number of users in the east region.
    // */
    // public void setEastUsers(String users) {
    // if (!TestParamUtil.isValidExpression(users)) {
    // messages.error("Cannot parse users " + users);
    // } else {
    // setUsersForRegion(VMRegion.US_EAST, users);
    // }
    // }
    //
    // /**
    // * @return the number of users in the west region.
    // */
    // public String getWestUsers() {
    // return getUsersForRegion(VMRegion.US_WEST_1);
    // }
    //
    // /**
    // * Sets the number of users in the west region
    // *
    // * @param users
    // * the number of users in the west region
    // */
    // public void setWestUsers(String users) {
    // if (!TestParamUtil.isValidExpression(users)) {
    // messages.error("Cannot parse users " + users);
    // } else {
    // setUsersForRegion(VMRegion.US_WEST_1, users);
    // }
    // }
    //
    // /**
    // * Helps in setting the values of the users in a particular region
    // *
    // * @param region
    // * The region where the users are to be set.
    // * @param users
    // * The value of the number of users that is to be set.
    // */
    // private void setUsersForRegion(VMRegion region, String users) {
    // Set<JobRegion> jobRegions = projectBean.getJobConfiguration().getJobRegions();
    //
    // if (jobRegions.isEmpty() || jobRegions.size() < 2) {
    // JobRegion ejr = new JobRegion();
    // ejr.setUsers("0");
    // ejr.setRegion(VMRegion.US_EAST);
    //
    // JobRegion wjr = new JobRegion();
    // wjr.setUsers("0");
    // wjr.setRegion(VMRegion.US_WEST_1);
    // jobRegions.add(ejr);
    // jobRegions.add(wjr);
    // }
    // for (JobRegion jobRegion : jobRegions) {
    // if (jobRegion.getRegion() == region) {
    // jobRegion.setUsers(users);
    // break;
    // }
    // }
    // }
    //
    // /**
    // * Returns the number of users in a given region
    // *
    // * @param region
    // * The region.
    // * @return the number of users in the given region
    // * @see com.intuit.tank.vm.api.enumerated.VMRegion
    // */
    // private String getUsersForRegion(VMRegion region) {
    // String retVal = "0";
    // Set<JobRegion> jobRegions = projectBean.getJobConfiguration().getJobRegions();
    // for (JobRegion jobRegion : jobRegions) {
    // if (jobRegion.getRegion() == region) {
    // retVal = jobRegion.getUsers();
    // break;
    // }
    // }
    // return retVal;
    // }

    /**
     * Persists the workload object in the database. It also fires an event for project modified.
     */
    public void save() {
        // WorkloadDao wd = new WorkloadDao();
        // workload.setJobConfiguration(jobConfiguration);
        // wd.saveOrUpdate(workload);
        // projectEvent.fire(project);
//        projectBean.getJobConfiguration().getJobRegions().clear();
//        projectBean.getJobConfiguration().getJobRegions().addAll(getJobRegions());
        new JobConfigurationDao().saveOrUpdate(projectBean.getJobConfiguration());
        jobRegions = null;
    }

    public void copyTo(Workload copyTo) {
        JobConfiguration jobConfiguration = projectBean.getJobConfiguration();
        JobConfiguration jc = copyTo.getJobConfiguration();
        jc.setBaselineVirtualUsers(jobConfiguration.getBaselineVirtualUsers());
        jc.setDataFileIds(new HashSet<Integer>(jobConfiguration.getDataFileIds()));
        jc.setIncrementStrategy(jobConfiguration.getIncrementStrategy());
        jc.setLocation(jobConfiguration.getLocation());
        jc.setRampTime(jobConfiguration.getRampTime());
        jc.setReportingMode(jobConfiguration.getReportingMode());
        jc.setSimulationTime(jobConfiguration.getSimulationTime());
        jc.setTerminationPolicy(jobConfiguration.getTerminationPolicy());
        jc.setUserIntervalIncrement(jobConfiguration.getUserIntervalIncrement());
        jc.setVariables(jobConfiguration.getVariables());
        Set<JobRegion> regions = new HashSet<JobRegion>();
        for (JobRegion jr : jobConfiguration.getJobRegions()) {
            regions.add(copyRegion(jr));
        }
        jc.setJobRegions(regions);
        jc.setParent(copyTo);
        copyTo.setJobConfiguration(jc);
    }

    /**
     * @param jr
     * @return
     */
    private JobRegion copyRegion(JobRegion jr) {
        JobRegion ret = new JobRegion();
        ret.setRegion(jr.getRegion());
        ret.setUsers(jr.getUsers());
        return ret;
    }

    /**
     * @return The termination policy of the job.
     * @see com.intuit.tank.vm.api.enumerated.TerminationPolicy
     */
    public TerminationPolicy getTerminationPolicy() {
        return projectBean.getJobConfiguration().getTerminationPolicy();
    }

    /**
     * 
     * @return
     */
    public TerminationPolicy[] getTerminationPolicyList() {
        return TerminationPolicy.values();
    }

    /**
     * Sets the termination policy for the job. If the termination policy is 'Script Loops Completed' the value that is
     * set is 'TerminationPolicy.script' else the value is set to 'TerminationPolicy.time'
     * 
     * @param terminationPolicy
     * @see com.intuit.tank.vm.api.enumerated.TerminationPolicy
     */
    public void setTerminationPolicy(TerminationPolicy terminationPolicy) {
        projectBean.getJobConfiguration().setTerminationPolicy(terminationPolicy);
    }

    /**
     * @return the simulation time in the following format hh:mm:ss
     */
    public String getSimulationTime() {
        if (projectBean.getJobConfiguration().getSimulationTimeExpression() != null) {
            return projectBean.getJobConfiguration().getSimulationTimeExpression();
        }
        return TimeUtil.toTimeString(projectBean.getJobConfiguration().getSimulationTime());
    }

    /**
     * Sets the simulation time for the job
     * 
     * @param simulationTime
     *            the simulation time is in the format hh:mm:ss
     */
    public void setSimulationTime(String simulationTime) {
        try {
            if (!TestParamUtil.isValidExpression(simulationTime)) {
                messages.error("Cannot parse simulation time " + simulationTime);
            } else {
                projectBean.getJobConfiguration().setSimulationTimeExpression(simulationTime);
            }
        } catch (RuntimeException e) {
            messages.error("Cannot format simulation time " + simulationTime);
        }
    }

    /**
     * @return the ramp time for the job in the format hh:mm:ss
     */
    public String getRampTime() {
        if (projectBean.getJobConfiguration().getRampTimeExpression() != null) {
            return projectBean.getJobConfiguration().getRampTimeExpression();
        }
        return TimeUtil.toTimeString(projectBean.getJobConfiguration().getRampTime());
    }

    /**
     * Sets the ramp time for the job.
     * 
     * @param rampTime
     *            The ramp time should be in the following format hh:mm:ss
     */
    public void setRampTime(String rampTime) {
        try {
            if (!TestParamUtil.isValidExpression(rampTime)) {
                messages.error("Cannot parse ramp time " + rampTime);
            } else {
                projectBean.getJobConfiguration().setRampTimeExpression(rampTime);
            }
        } catch (RuntimeException e) {
            messages.error("Cannot format ramp time " + rampTime);
        }
    }

    /**
     * @return the increment strategy for the job
     */
    public IncrementStrategy getIncrementStrategy() {
        return projectBean.getJobConfiguration().getIncrementStrategy();
    }

    public IncrementStrategy[] getIncrementStrategyList() {
        return IncrementStrategy.values();
    }

    /**
     * Sets the increment strategy for the job
     * 
     * @param workloadType
     *            The increment strategy for the job.
     */
    public void setIncrementStrategy(IncrementStrategy strategy) {
        projectBean.getJobConfiguration().setIncrementStrategy(strategy);
    }

    /**
     * @return The number of start users for the job
     */
    public String getStartUsers() {
        return String.valueOf(projectBean.getJobConfiguration().getBaselineVirtualUsers());
    }

    /**
     * Sets the number of start users for the job.
     * 
     * @param startUsers
     *            String representation of an integer value
     */
    public void setStartUsers(String startUsers) {
        projectBean.getJobConfiguration().setBaselineVirtualUsers(Integer.parseInt(startUsers));
    }

    /**
     * @return the number of users increment for the job
     */
    public String getUserIncrement() {
        if (projectBean.getJobConfiguration().getUserIntervalIncrement() > 0) {
            return String.valueOf(projectBean.getJobConfiguration().getUserIntervalIncrement());
        }
        return "1";
    }

    /**
     * Sets the number of users to be incremented for the job
     * 
     * @param startUsers
     *            String representation of an integer value.
     */
    public void setUserIncrement(String startUsers) {
        if (NumberUtils.isNumber(startUsers)) {
            projectBean.getJobConfiguration().setUserIntervalIncrement(Integer.parseInt(startUsers));
        }
    }

    /**
     * @return total number of east and west region users
     */
    public int getTotalUsers() {
        int totalUsers = 0;
        for (JobRegion jobRegion : getJobRegions()) {
            try {
                if (NumberUtils.isDigits(jobRegion.getUsers())) {
                    totalUsers += Integer.valueOf(jobRegion.getUsers());
                }
            } catch (NumberFormatException e) {
                LOG.info("cannot parse users" + jobRegion.getUsers());
            }
        }
        return totalUsers;
    }

}
