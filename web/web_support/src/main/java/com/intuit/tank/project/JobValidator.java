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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import com.intuit.tank.common.ScriptAssignment;
import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.settings.TimeUtil;

public class JobValidator {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JobValidator.class);

    private Map<String, Set<String>> declaredVariables = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> assignments = new HashMap<String, Set<String>>();
    private Set<String> usedVariables = new HashSet<String>();
    private Set<String> orphanedVariables = new HashSet<String>();
    private Set<String> superfluousVariables = new HashSet<String>();
    private List<ScriptWrapper> scripts = new ArrayList<ScriptWrapper>();
    private Map<String, Long> expectedDuration = new HashMap<String, Long>();
    private boolean valid;
    private boolean processAssignments = false;
    private Set<String> bestPracticeViolations = new HashSet<String>();
    private Set<String> dataFiles = new HashSet<String>();

    public JobValidator(@Nonnull List<TestPlan> testplans, @Nonnull Map<String, String> globalVariables) {
        this(testplans, globalVariables, false);
    }

    public JobValidator(@Nonnull List<TestPlan> testplans, @Nonnull Map<String, String> globalVariables,
            boolean processAssignements) {
        this.processAssignments = processAssignements;
        for (Entry<String, String> entry : globalVariables.entrySet()) {
            putVariable(this.declaredVariables, entry.getKey(), entry.getValue(), "project");
        }
        extractScripts(testplans);
        processScripts(new HashMap<String, String>(globalVariables));
    }

    public JobValidator(@Nonnull Script s) {
        this.scripts.add(new ScriptWrapper(s, 1, s.getName(), s.getName()));
        processAssignments = true;
        processScripts(new HashMap<String, String>());
    }

    public boolean isValid() {
        return valid;
    }

    public Set<String> getDataFiles() {
        return dataFiles;
    }

    public Set<String> getBestPracticeViolations() {
        return bestPracticeViolations;
    }

    public Map<String, Set<String>> getAssignments() {
        return assignments;
    }

    public Map<String, Set<String>> getDeclaredVariables() {
        return declaredVariables;
    }

    public Set<String> getUsedVariables() {
        return usedVariables;
    }

    public boolean isOrphaned(String var) {
        return orphanedVariables.contains(var);
    }

    public boolean isProcessAssignements() {
        return this.processAssignments;
    }

    public boolean isSuperfluous(String var) {
        return superfluousVariables.contains(var);
    }

    public Set<String> getOrphanedVariables() {
        return orphanedVariables;
    }

    public Set<String> getSuperfluousVariables() {
        return superfluousVariables;
    }

    public long getDurationMs(String group) {
        Long duration = expectedDuration.get(group);
        return duration != null ? duration.longValue() : 0L;
    }

    public String getDuration(String group) {
        Long duration = expectedDuration.get(group);
        return TimeUtil.toTimeString(duration != null ? duration.longValue() : 0);
    }

    public long getExpectedTime(String group) {
        Long duration = expectedDuration.get(group);
        return duration != null ? duration.longValue() : 0;
    }

    private void putVariable(Map<String, Set<String>> map, String key, String value, String declarer) {
        Set<String> set = map.get(key);
        if (set == null) {
            set = new HashSet<String>();
            map.put(key, set);
        }
        set.add(declarer + ":: " + value);

    }

    private void extractScripts(List<TestPlan> testplans) {
        ScriptDao scriptDao = new ScriptDao();
        for (TestPlan plan : testplans) {
            for (ScriptGroup group : plan.getScriptGroups()) {
                for (ScriptGroupStep s : group.getScriptGroupSteps()) {
                    int loop = group.getLoop() * s.getLoop();
                    Script script = scriptDao.loadScriptSteps(s.getScript());
                    scripts.add(new ScriptWrapper(script, loop, plan.getName(), plan.getName() + "::" + group.getName()
                            + "::"
                            + script.getName()));
                }
            }
        }
    }

    private void processScripts(Map<String, String> globalVariables) {
        MethodTimer mt = new MethodTimer(LOG, this.getClass(), "processScripts");
        mt.start();
        boolean hasLoggingKeys = false;
        for (ScriptWrapper wrapper : scripts) {
            long milis = 0;
            Script s = wrapper.script;
            boolean hasRequest = false;
            for (ScriptStep step : s.getScriptSteps()) {
                if (step.getType().equals("request")) {
                    hasRequest = true;
                } else if (hasRequest && step.getType().equalsIgnoreCase("variable")) {
                    bestPracticeViolations
                            .add("&nbsp;&nbsp;Rule: Declare variables before request.<br/>&nbsp;&nbsp;&nbsp;&nbsp;Script "
                                    + s.getName() + " declares variable '"
                                    + step.getData().iterator().next().getKey() + "' at index " + step.getStepIndex());
                }
                if (StringUtils.isNotBlank(step.getLoggingKey())) {
                    hasLoggingKeys = true;
                }
                String dataFile = ScriptUtil.getDataFileUse(step);
                if (dataFile != null) {
                    String stripped = StringUtils.strip(dataFile, "'\"");
                    dataFiles.add(stripped);
                    if (!stripped.equals(dataFile)) {
                        bestPracticeViolations
                                .add("&nbsp;&nbsp;Rule: Do not hard Code Data File names.<br/>&nbsp;&nbsp;&nbsp;&nbsp;Script "
                                        + s.getName() + " uses a hard coded name for the data file '"
                                        + stripped + "' at index " + step.getStepIndex());
                    } else if (dataFile.equalsIgnoreCase(TankConstants.DEFAULT_CSV_FILE_NAME)) {
                        bestPracticeViolations
                                .add("&nbsp;&nbsp;Rule: Explicitly define Data File names.<br/>&nbsp;&nbsp;&nbsp;&nbsp;Script "
                                        + s.getName()
                                        + " uses the default data file 'csv-data.txt' at index "
                                        + step.getStepIndex());
                    }
                }
                milis += ScriptUtil.calculateStepDuration(step, globalVariables);
                for (Entry<String, String> entry : ScriptUtil.getDeclaredVariables(step).entrySet()) {
                    putVariable(this.declaredVariables, entry.getKey(), entry.getValue(), wrapper.location);
                }
                usedVariables.addAll(ScriptUtil.getUsedVariables(step));
                // if (processAssignments) {
                for (ScriptAssignment assignment : ScriptUtil.getAssignments(step)) {
                    putVariable(this.assignments, assignment.getVariable(), assignment.getAssignemnt(),
                            s.getName() + "[" + assignment.getScriptIndex() + "]");
                }
                usedVariables.addAll(this.assignments.keySet());
                // }
            }
            addDuration(wrapper.groupName, wrapper.loop, milis);
        }
        if (!hasLoggingKeys) {
            bestPracticeViolations
                    .add("&nbsp;&nbsp;Rule: No Logging Keys.<br/>&nbsp;&nbsp;&nbsp;&nbsp;No Logging keys are defined");
        }
        this.orphanedVariables.addAll(usedVariables);
        for (String s : declaredVariables.keySet()) {
            orphanedVariables.remove(s);
        }
        for (String s : assignments.keySet()) {
            orphanedVariables.remove(s);
        }
        this.superfluousVariables.addAll(declaredVariables.keySet());
        this.superfluousVariables.addAll(assignments.keySet());
        for (String s : usedVariables) {
            superfluousVariables.remove(s);
        }
        mt.endAndLog();
    }

    private void addDuration(String groupName, int loop, long milis) {
        Long val = this.expectedDuration.get(groupName);
        long l = val != null ? val.longValue() : 0L;
        l += milis * loop;
        expectedDuration.put(groupName, l);
    }

    private static class ScriptWrapper {
        private Script script;
        private String location;
        private String groupName;
        private int loop;

        public ScriptWrapper(Script script, int loop, String groupName, String location) {
            super();
            this.script = script;
            this.location = location;
            this.groupName = groupName;
            this.loop = loop;
        }

    }
}
