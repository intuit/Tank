/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.Permissions;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.RegionUsers;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.ScriptGroupDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.ScriptRef;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.Settings;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.TestPlanDetail;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Converts between the project entity graph and {@link ProjectDetail}, and checks a {@link ProjectDetail}
 * before it is saved.
 */
public final class ProjectDetailMapper {

    static final int MAX_NAME_LENGTH = 255;
    static final int MAX_COMMENTS_LENGTH = 1024;

    private ProjectDetailMapper() {
    }

    /**
     * @param configuredRegions the regions agents can run in; ignored when {@code standalone}
     */
    public static ProjectDetail toDetail(Project project, Permissions permissions,
                                         Collection<VMRegion> configuredRegions, boolean standalone) {
        Workload workload = project.getWorkloads().get(0);
        JobConfiguration config = workload.getJobConfiguration();
        Settings settings = new Settings(
                config.getIncrementStrategy(),
                config.getTerminationPolicy(),
                config.getSimulationTimeExpression(),
                config.getRampTimeExpression(),
                config.getBaselineVirtualUsers(),
                config.getUserIntervalIncrement(),
                config.getTargetRampRate(),
                config.getLocation(),
                config.getStopBehavior(),
                config.getLoggingProfile(),
                config.getVmInstanceType(),
                config.getNumUsersPerAgent(),
                config.getTargetRatePerAgent() != null ? config.getTargetRatePerAgent() : 1.0,
                config.isUseTwoStep(),
                config.getTankClientClass(),
                config.getReportingMode(),
                config.isAllowOverride());

        List<TestPlanDetail> testPlans = workload.getTestPlans().stream()
                .map(plan -> new TestPlanDetail(plan.getName(), plan.getUserPercentage(),
                        plan.getScriptGroups().stream()
                                .map(group -> new ScriptGroupDetail(group.getName(), group.getLoop(),
                                        group.getScriptGroupSteps().stream()
                                                .map(step -> new ScriptRef(step.getScript().getId(),
                                                        step.getScript().getName(), step.getLoop()))
                                                .collect(Collectors.toList())))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        return new ProjectDetail(project.getId(), project.getName(), project.getProductName(), project.getComments(),
                project.getCreator(), project.getCreated(), project.getModified(), settings,
                regions(config.getJobRegions(), configuredRegions, standalone), testPlans,
                new TreeMap<>(config.getVariables()), config.getDataFileIds().stream().sorted().collect(Collectors.toList()),
                permissions);
    }

    /**
     * The regions as the editor shows them, without changing the stored ones: every configured region,
     * or the single standalone region, which combines the stored ones as the web UI does.
     */
    static List<RegionUsers> regions(Set<JobRegion> stored, Collection<VMRegion> configuredRegions, boolean standalone) {
        if (standalone) {
            for (JobRegion region : stored) {
                if (region.getRegion() == VMRegion.US_EAST || region.getRegion() == VMRegion.STANDALONE) {
                    return List.of(new RegionUsers(VMRegion.STANDALONE, region.getUsers(), region.getPercentage()));
                }
            }
            String users = stored.stream().map(JobRegion::getUsers).filter(u -> !"0".equals(u))
                    .collect(Collectors.joining(" + "));
            int percentage = stored.stream().mapToInt(r -> NumberUtils.toInt(r.getPercentage())).sum();
            return List.of(new RegionUsers(VMRegion.STANDALONE, StringUtils.defaultIfBlank(users, "0"),
                    Integer.toString(percentage)));
        }
        Map<VMRegion, RegionUsers> byRegion = new EnumMap<>(VMRegion.class);
        for (VMRegion region : configuredRegions) {
            byRegion.put(region, new RegionUsers(region, "0", "0"));
        }
        for (JobRegion region : stored) {
            byRegion.put(region.getRegion(), new RegionUsers(region.getRegion(), region.getUsers(), region.getPercentage()));
        }
        return new ArrayList<>(byRegion.values());
    }

    /**
     * @return the problems that stop {@code detail} from being saved; empty when it is valid. Does not
     *         check that scripts and data files exist.
     */
    public static List<String> validate(ProjectDetail detail) {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(detail.name())) {
            errors.add("name is required");
        } else if (detail.name().trim().length() > MAX_NAME_LENGTH) {
            errors.add("name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        if (detail.productName() != null && detail.productName().length() > MAX_NAME_LENGTH) {
            errors.add("productName must be at most " + MAX_NAME_LENGTH + " characters");
        }
        if (detail.comments() != null && detail.comments().length() > MAX_COMMENTS_LENGTH) {
            errors.add("comments must be at most " + MAX_COMMENTS_LENGTH + " characters");
        }
        if (StringUtils.isBlank(detail.owner())) {
            errors.add("owner is required");
        }
        validateSettings(detail.settings(), errors);
        validateRegions(detail.regions(), errors);
        validateTestPlans(detail.testPlans(), errors);
        if (detail.variables() != null && detail.variables().keySet().stream().anyMatch(StringUtils::isBlank)) {
            errors.add("variable names must not be blank");
        }
        if (detail.dataFileIds() != null && detail.dataFileIds().stream().anyMatch(id -> id == null)) {
            errors.add("dataFileIds must not contain null");
        }
        return errors;
    }

    private static void validateSettings(Settings s, List<String> errors) {
        if (s == null) {
            errors.add("settings are required");
            return;
        }
        if (s.incrementStrategy() == null) {
            errors.add("settings.incrementStrategy is required");
        }
        if (s.terminationPolicy() == null) {
            errors.add("settings.terminationPolicy is required");
        }
        if (StringUtils.isNotBlank(s.simulationTime()) && !TestParamUtil.isValidExpression(s.simulationTime())) {
            errors.add("settings.simulationTime cannot be parsed: " + s.simulationTime());
        }
        if (StringUtils.isNotBlank(s.rampTime()) && !TestParamUtil.isValidExpression(s.rampTime())) {
            errors.add("settings.rampTime cannot be parsed: " + s.rampTime());
        }
        if (s.baselineVirtualUsers() < 0) {
            errors.add("settings.baselineVirtualUsers must not be negative");
        }
        if (s.userIntervalIncrement() < 0) {
            errors.add("settings.userIntervalIncrement must not be negative");
        }
        if (s.targetRampRate() < 0) {
            errors.add("settings.targetRampRate must not be negative");
        }
        if (s.numUsersPerAgent() < 1) {
            errors.add("settings.numUsersPerAgent must be at least 1");
        }
        if (s.targetRatePerAgent() <= 0) {
            errors.add("settings.targetRatePerAgent must be greater than 0");
        }
        if (s.stopBehavior() != null && !isEnumName(StopBehavior.class, s.stopBehavior())) {
            errors.add("settings.stopBehavior is not a known value: " + s.stopBehavior());
        }
        if (s.loggingProfile() != null && !isEnumName(LoggingProfile.class, s.loggingProfile())) {
            errors.add("settings.loggingProfile is not a known value: " + s.loggingProfile());
        }
    }

    private static void validateRegions(List<RegionUsers> regions, List<String> errors) {
        if (regions == null) {
            errors.add("regions are required");
            return;
        }
        Set<VMRegion> seen = new HashSet<>();
        for (RegionUsers region : regions) {
            if (region == null || region.region() == null) {
                errors.add("every region needs a region name");
                continue;
            }
            if (!seen.add(region.region())) {
                errors.add("region " + region.region().name() + " is listed more than once");
            }
            if (StringUtils.isBlank(region.users()) || !TestParamUtil.isValidExpression(region.users())) {
                errors.add("users for region " + region.region().name() + " cannot be parsed: " + region.users());
            }
            int percentage = NumberUtils.toInt(region.percentage(), -1);
            if (!NumberUtils.isDigits(region.percentage()) || percentage > 100) {
                errors.add("percentage for region " + region.region().name() + " must be a whole number from 0 to 100");
            }
        }
    }

    private static void validateTestPlans(List<TestPlanDetail> plans, List<String> errors) {
        if (plans == null || plans.isEmpty()) {
            errors.add("at least one test plan is required");
            return;
        }
        for (TestPlanDetail plan : plans) {
            if (plan == null || StringUtils.isBlank(plan.name())) {
                errors.add("every test plan needs a name");
                continue;
            }
            if (plan.userPercentage() < 0 || plan.userPercentage() > 100) {
                errors.add("userPercentage of test plan " + plan.name() + " must be from 0 to 100");
            }
            for (ScriptGroupDetail group : nullToEmpty(plan.scriptGroups())) {
                if (group == null || StringUtils.isBlank(group.name())) {
                    errors.add("every script group in test plan " + plan.name() + " needs a name");
                    continue;
                }
                if (group.loop() < 1) {
                    errors.add("loop of script group " + group.name() + " must be at least 1");
                }
                for (ScriptRef script : nullToEmpty(group.scripts())) {
                    if (script == null) {
                        errors.add("script group " + group.name() + " contains an empty script entry");
                    } else if (script.loop() < 1) {
                        errors.add("loop of script " + script.scriptId() + " in group " + group.name() + " must be at least 1");
                    }
                }
            }
        }
    }

    /**
     * @return the ids of all scripts referenced by the test plans
     */
    public static Set<Integer> scriptIds(ProjectDetail detail) {
        return nullToEmpty(detail.testPlans()).stream()
                .flatMap(plan -> nullToEmpty(plan.scriptGroups()).stream())
                .flatMap(group -> nullToEmpty(group.scripts()).stream())
                .map(ScriptRef::scriptId)
                .collect(Collectors.toSet());
    }

    /**
     * Replaces the project's contents with a validated {@code detail}. The owner is applied too;
     * the caller checks whether it may change.
     *
     * @param scripts the scripts referenced by {@code detail}, by id
     */
    public static void apply(ProjectDetail detail, Project project, Map<Integer, Script> scripts) {
        project.setName(detail.name().trim());
        project.setProductName(detail.productName());
        project.setComments(detail.comments());
        project.setCreator(detail.owner());

        Workload workload = project.getWorkloads().get(0);
        workload.setName(detail.name().trim());
        JobConfiguration config = workload.getJobConfiguration();
        Settings s = detail.settings();
        config.setIncrementStrategy(s.incrementStrategy());
        config.setTerminationPolicy(s.terminationPolicy());
        config.setSimulationTimeExpression(StringUtils.trimToNull(s.simulationTime()));
        config.setRampTimeExpression(StringUtils.trimToNull(s.rampTime()));
        config.setBaselineVirtualUsers(s.baselineVirtualUsers());
        config.setUserIntervalIncrement(s.userIntervalIncrement());
        config.setTargetRampRate(s.targetRampRate());
        if (s.location() != null) {
            config.setLocation(s.location());
        }
        if (s.stopBehavior() != null) {
            config.setStopBehavior(s.stopBehavior());
        }
        if (s.loggingProfile() != null) {
            config.setLoggingProfile(s.loggingProfile());
        }
        if (s.vmInstanceType() != null) {
            config.setVmInstanceType(s.vmInstanceType());
        }
        config.setNumUsersPerAgent(s.numUsersPerAgent());
        config.setTargetRatePerAgent(s.targetRatePerAgent());
        config.setUseTwoStep(s.useTwoStep());
        if (s.tankClientClass() != null) {
            config.setTankClientClass(s.tankClientClass());
        }
        if (s.reportingMode() != null) {
            config.setReportingMode(s.reportingMode());
        }
        config.setAllowOverride(s.allowOverride());

        applyRegions(detail.regions(), config.getJobRegions());

        config.getVariables().clear();
        if (detail.variables() != null) {
            config.getVariables().putAll(detail.variables());
        }
        config.getDataFileIds().clear();
        if (detail.dataFileIds() != null) {
            config.getDataFileIds().addAll(detail.dataFileIds());
        }

        workload.getTestPlans().clear();
        for (TestPlanDetail planDetail : detail.testPlans()) {
            TestPlan plan = new TestPlan();
            plan.setName(planDetail.name());
            plan.setUserPercentage(planDetail.userPercentage());
            for (ScriptGroupDetail groupDetail : nullToEmpty(planDetail.scriptGroups())) {
                ScriptGroup group = new ScriptGroup();
                group.setName(groupDetail.name());
                group.setLoop(groupDetail.loop());
                for (ScriptRef ref : nullToEmpty(groupDetail.scripts())) {
                    ScriptGroupStep step = new ScriptGroupStep();
                    step.setScript(scripts.get(ref.scriptId()));
                    step.setLoop(ref.loop());
                    group.addScriptGroupStep(step);
                }
                plan.addScriptGroup(group);
            }
            workload.addTestPlan(plan);
        }
    }

    /**
     * Updates regions in place, so existing rows keep their ids, adds new ones and drops the rest.
     */
    private static void applyRegions(List<RegionUsers> regions, Set<JobRegion> stored) {
        Map<VMRegion, JobRegion> existing = new EnumMap<>(VMRegion.class);
        stored.stream().sorted(Comparator.comparing(JobRegion::getId)).forEach(r -> existing.putIfAbsent(r.getRegion(), r));
        stored.clear();
        for (RegionUsers region : regions) {
            JobRegion target = existing.get(region.region());
            if (target == null) {
                target = new JobRegion(region.region(), region.users().trim(), region.percentage());
            } else {
                target.setUsers(region.users().trim());
                target.setPercentage(region.percentage());
            }
            stored.add(target);
        }
    }

    private static <E extends Enum<E>> boolean isEnumName(Class<E> type, String name) {
        for (E constant : type.getEnumConstants()) {
            if (constant.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static <T> List<T> nullToEmpty(List<T> list) {
        return list != null ? list : List.of();
    }
}
