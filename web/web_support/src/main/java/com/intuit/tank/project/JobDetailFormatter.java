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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.settings.TimeUtil;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;

public class JobDetailFormatter {

    private static final String BREAK = "<br/>\n";
    private static final long HOURS = 1000 * 60 * 60;

    public static String createJobDetails(JobValidator validator, Workload workload, JobInstance proposedJobInstance) {
        return buildDetails(validator, workload, proposedJobInstance, null);
    }

    public static String createJobDetails(JobValidator validator, String scriptName) {
        return buildDetails(validator, null, null, scriptName);
    }

    protected static String buildDetails(JobValidator validator, Workload workload, JobInstance proposedJobInstance,
            String scriptName) {
        StringBuilder sb = new StringBuilder();
        StringBuilder errorSB = new StringBuilder();
        TankConfig config = new TankConfig();
        if (proposedJobInstance != null) {

            if (StringUtils.isBlank(proposedJobInstance.getName())) {
                addError(errorSB, "Name cannot be null");
            }

            JobRegionDao jrd = new JobRegionDao();
            List<JobRegion> regions = new ArrayList<JobRegion>();
            for (EntityVersion ver : proposedJobInstance.getJobRegionVersions()) {
                JobRegion region = jrd.findById(ver.getObjectId());
                if (region != null) {
                    long users = TestParamUtil.evaluateExpression(region.getUsers(),
                            proposedJobInstance.getExecutionTime(),
                            proposedJobInstance.getSimulationTime(), proposedJobInstance.getRampTime());
                    if (users > 0) {
                        regions.add(new JobRegion(region.getRegion(), Long.toString(users)));
                    }
                }
            }
            Collections.sort(regions);
            long simulationTime = getSimulationTime(proposedJobInstance, workload, validator);
            addProperty(sb, "General Information", "",
                    "emphasis");
            addProperty(sb, "Name", StringUtils.isBlank(proposedJobInstance.getName()) ? "Name cannot be null"
                    : proposedJobInstance.getName(), StringUtils.isBlank(proposedJobInstance.getName()) ? "error"
                    : null);
            addProperty(sb, "Workload Type", proposedJobInstance.getIncrementStrategy().name());
            addProperty(sb, "Tank Http Client", config.getAgentConfig().getTankClientName(proposedJobInstance.getTankClientClass()));
            addProperty(sb, "Agent VM Type", getVmDetails(config, proposedJobInstance.getVmInstanceType()));
            addProperty(sb, "Assign Elastic Ips", Boolean.toString(proposedJobInstance.isUseEips()));
            addProperty(sb, "Max Users per Agent", Integer.toString(proposedJobInstance.getNumUsersPerAgent()));
            addProperty(sb, "Estimated Cost", calculateCost(config, proposedJobInstance, regions, simulationTime));
            addProperty(sb, "Location", proposedJobInstance.getLocation());
            addProperty(sb, "Logging Profile", LoggingProfile.fromString(proposedJobInstance.getLoggingProfile())
                    .getDisplayName());
            addProperty(sb, "Stop Behavior", StopBehavior.fromString(proposedJobInstance.getStopBehavior())
                    .getDisplay());
            addProperty(sb, "Run Scripts Until", proposedJobInstance.getTerminationPolicy().getDisplay(),
                    proposedJobInstance.getTerminationPolicy() == TerminationPolicy.time
                            && proposedJobInstance.getSimulationTime() == 0 ? "error" : null);
            sb.append(BREAK);
            addProperty(
                    sb,
                    "Simulation Time",
                    TimeUtil.toTimeString(simulationTime));
            if (proposedJobInstance.getTerminationPolicy() == TerminationPolicy.time
                    && proposedJobInstance.getSimulationTime() == 0) {
                addError(errorSB, "Simulation time not set.");
            }
            addProperty(sb, "Ramp Time", TimeUtil.toTimeString(proposedJobInstance.getRampTime()));
            addProperty(sb, "Initial Users", Integer.toString(proposedJobInstance.getBaselineVirtualUsers()));
            addProperty(sb, "User Increment", Integer.toString(proposedJobInstance.getUserIntervalIncrement()));
            // users and regions
            sb.append(BREAK);
            addProperty(sb, "Total Users", Integer.toString(proposedJobInstance.getTotalVirtualUsers()),
                    proposedJobInstance.getTotalVirtualUsers() == 0 ? "error" : "emphasis");
            if (proposedJobInstance.getTotalVirtualUsers() == 0) {
                addError(errorSB, "No users defined.");
            }

            for (JobRegion r : regions) {
                if (config.getStandalone()) {
                    addProperty(sb, "  Users", r.getUsers());
                } else {
                    addProperty(sb, "  " + r.getRegion().getDescription(), r.getUsers());
                }
            }
            sb.append(BREAK);
            sb.append(BREAK);

            int userPercentage = 0;
            for (TestPlan plan : workload.getTestPlans()) {
                userPercentage += plan.getUserPercentage();
            }
            if (userPercentage != 100) {
                addError(errorSB, "User Percentage of Test Plans does not add up to 100%");
            }
            // datafiles
            addProperty(sb, "Data Files", proposedJobInstance.getDataFileVersions().size() == 0 ? "None" : null,
                    "emphasis");
            DataFileDao dfd = new DataFileDao();
            Set<String> datafiles = new HashSet<String>();
            for (EntityVersion ver : proposedJobInstance.getDataFileVersions()) {
                DataFile df = dfd.findById(ver.getObjectId());
                if (df != null) {
                    addProperty(sb, "  " + df.getPath(), null);
                    datafiles.add(df.getPath());
                } else {
                    addProperty(sb, "  " + ver.getObjectId(), "data file not found.", "error");
                }
            }
            sb.append(BREAK);

            // variables
            addProperty(sb, "Global Variables", proposedJobInstance.getVariables().size() == 0 ? "None"
                    : " (Allow Overide: "
                            + proposedJobInstance.isAllowOverride() + ")", "emphasis");
            for (Entry<String, String> entry : proposedJobInstance.getVariables().entrySet()) {
                addProperty(sb, "  " + entry.getKey(), entry.getValue());
                if (entry.getValue().toLowerCase().endsWith(".csv") && !datafiles.contains(entry.getValue())) {
                    addProperty(sb, "  WARNING", "This variable, " + entry.getKey()
                            + " appears to be a reference to a datafile, " + entry.getValue()
                            + " that is not declared.", "error");
                }
            }
            sb.append(BREAK);

            // notifications
            addProperty(sb, "Notifications", proposedJobInstance.getNotificationVersions().size() == 0 ? "None" : null,
                    "emphasis");
            JobNotificationDao jnd = new JobNotificationDao();
            for (EntityVersion ver : proposedJobInstance.getNotificationVersions()) {
                JobNotification not = jnd.findById(ver.getObjectId());
                if (not != null) {
                    if (not.getLifecycleEvents().size() > 0) {
                        addProperty(sb, "  " + not.getRecipientList(), StringUtils.join(not.getLifecycleEvents(), ", "));
                    } else {
                        addProperty(sb, "  " + not.getRecipientList(), "no events selected", "error");
                    }
                }
            }
            sb.append(BREAK);
            addProperty(sb, "Scripts", "", "emphasis");
            // scripts
            List<ScriptGroupStep> stepsList = new ArrayList<ScriptGroupStep>();
            for (TestPlan plan : workload.getTestPlans()) {
                int numUsers = plan.getUserPercentage() > 0 ? proposedJobInstance.getTotalVirtualUsers() : 0;
                if (plan.getUserPercentage() < 100 && plan.getUserPercentage() > 0) {
                    numUsers = (int) Math.floor(numUsers * ((double) plan.getUserPercentage() / 100D));
                }
                addProperty(
                        sb,
                        "  " + plan.getName(),
                        plan.getUserPercentage() + "% : (" + numUsers
                                + " users) : estimated Time "
                                + TimeUtil.toTimeString(validator.getExpectedTime(plan.getName())),
                        userPercentage != 100 ? "error" : null);

                if (plan.getScriptGroups().size() == 0) {
                    addProperty(sb, "  " + plan.getName(), "contains no script groups", "error");
                }
                for (ScriptGroup group : plan.getScriptGroups()) {
                    addProperty(sb, "    " + group.getName(), "loop " + group.getLoop() + " time(s)");
                    if (group.getScriptGroupSteps().size() == 0) {
                        addProperty(sb, "    " + group.getName(), "contains no scripts", "error");
                    }
                    for (ScriptGroupStep s : group.getScriptGroupSteps()) {
                        stepsList.add(s);
                        addProperty(sb, "      " + s.getScript().getName(), "loop " + s.getLoop() + " time(s)");
                    }
                }
            }
            sb.append(BREAK);
            if (stepsList.size() == 0) {
                addError(errorSB, "No scripts defined.");
            }

        } else {
            addProperty(sb, scriptName, "Estimated Time " + validator.getDuration(scriptName), "emphasis");
            sb.append(BREAK);
            sb.append(BREAK);
        }
        addProperty(sb, "Variable Validation", "", "emphasis");
        addProperty(sb, "  Declared Variables", "", "emphasis");
        for (Entry<String, Set<String>> entry : validator.getDeclaredVariables().entrySet()) {
            for (String value : entry.getValue()) {
                addProperty(sb, "    " + entry.getKey(), value,
                        validator.isSuperfluous(entry.getKey()) ? "error" : null);
            }
        }
        sb.append(BREAK);
        addProperty(sb, "  Used Variables", "", "emphasis");
        for (String s : validator.getUsedVariables()) {
            addProperty(sb, "    ", s, validator.isOrphaned(s) ? "error" : null);
        }
        sb.append(BREAK);
        if (validator.isProcessAssignements()) {
            addProperty(sb, "  Assignements", "", "emphasis");
            for (Entry<String, Set<String>> entry : validator.getAssignments().entrySet()) {
                for (String value : entry.getValue()) {
                    addProperty(sb, "    " + entry.getKey(), value,
                            validator.isSuperfluous(entry.getKey()) ? "error" : null);
                }
            }
            sb.append(BREAK);
        }
        sb.append(BREAK);
        if (!validator.getBestPracticeViolations().isEmpty()) {
            StringBuilder tsb = new StringBuilder();
            addProperty(tsb, "Best Practice Violations", "", "emphasis");
            for (String s : validator.getBestPracticeViolations()) {
                addError(tsb, s);
            }
            tsb.append(BREAK);
            tsb.append(BREAK);
            sb.insert(0, tsb.toString());
        }

        if (errorSB.length() > 0) {
            sb = new StringBuilder().append("ERRORS").append(BREAK).append(errorSB.append(BREAK).toString())
                    .append(sb.toString());
        }
        return sb.toString();
    }

    protected static long getSimulationTime(JobInstance proposedJobInstance, Workload workload, JobValidator validator) {
        long ret = proposedJobInstance.getSimulationTime();
        if (TerminationPolicy.script == proposedJobInstance.getTerminationPolicy()) {
            ret = 0;
            for (TestPlan plan : workload.getTestPlans()) {
                ret = Math.max(ret, validator.getExpectedTime(plan.getName()));
            }
        }
        return ret;
    }

    protected static String calculateCost(TankConfig config, JobInstance proposedJobInstance,
            List<JobRegion> regions, long simulationTime) {
        List<VmInstanceType> instanceTypes = config.getVmManagerConfig().getInstanceTypes();
        BigDecimal costPerHour = new BigDecimal(.5D);
        for (VmInstanceType type : instanceTypes) {

            if (type.getName().equals(proposedJobInstance.getVmInstanceType())) {
                costPerHour = new BigDecimal(type.getCost());
                break;
            }
        }
        long time = simulationTime + proposedJobInstance.getRampTime();
        int numMachines = 0;
        for (JobRegion region : regions) {
            int users = Integer.parseInt(region.getUsers());
            if (users > 0) {
                numMachines += (int) Math.ceil((double) users / (double) proposedJobInstance.getNumUsersPerAgent());
            }
        }
        // dynamoDB costs about 1.5 times the instance cost
        BigDecimal cost = estimateCost(numMachines, costPerHour, time);
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(cost.doubleValue());
    }

    protected static BigDecimal estimateCost(int numInstances, BigDecimal costPerHour, long time) {
        BigDecimal cost = BigDecimal.ZERO;
        // calculate the number of machines and the expected run time
        BigDecimal hours = new BigDecimal(Math.max(1, Math.ceil(time / HOURS)));
        cost = cost.add(costPerHour.multiply(new BigDecimal(numInstances)).multiply(hours));
        // dynamoDB costs about 1.5 times the instance cost
        cost = cost.add(cost.multiply(new BigDecimal(1.5D)));
        return cost;
    }

    protected static String getVmDetails(TankConfig config, String vmInstanceType) {
        List<VmInstanceType> instanceTypes = config.getVmManagerConfig().getInstanceTypes();
        StringBuilder sb = new StringBuilder();
        sb.append(vmInstanceType);
        for (VmInstanceType type : instanceTypes) {
            if (type.getName().equals(vmInstanceType)) {
                sb.append(" (cpus=").append(type.getCpus())
                        .append(" ecus=").append(type.getEcus())
                        .append(" memory=").append(type.getMemory())
                        .append(" cost=$").append(type.getCost()).append(" per hour)");
                break;
            }
        }
        return sb.toString();
    }

    protected static void addProperty(StringBuilder sb, String key, String value) {
        addProperty(sb, key, value, null);
    }

    private static void addProperty(StringBuilder sb, String key, String value, String style) {
        if (StringUtils.isNotBlank(style)) {
            sb.append("<span style=\"font-weight: bold;\">");
        }
        sb.append(StringUtils.replace(key, " ", "&nbsp;"));
        if (StringUtils.isNotBlank(style)) {
            sb.append("</span>");
        }
        if (StringUtils.isNotBlank(value)) {
            if (StringUtils.isNotBlank(key)) {
                sb.append(": ");
            }
            if (StringUtils.isNotBlank(style)) {
                sb.append("<span class=\"" + style + "\">");
            }
            sb.append(value);
            if (StringUtils.isNotBlank(style)) {
                sb.append("</span>");
            }
        }
        sb.append(BREAK);
    }

    protected static void addError(StringBuilder sb, String message) {
        sb.append("<span class=\"error\" style=\"font-weight: bold;\">");
        sb.append(message);
        sb.append("</span>");
        sb.append(BREAK);
    }
}
