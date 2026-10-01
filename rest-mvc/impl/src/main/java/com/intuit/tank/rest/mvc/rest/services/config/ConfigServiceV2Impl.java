/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.config;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.filter.ConditionMatch;
import com.intuit.tank.filter.ConditionScope;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.models.InstanceTypeOption;
import com.intuit.tank.rest.mvc.rest.models.Option;
import com.intuit.tank.rest.mvc.rest.models.UiOptions;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.script.FailureTypes;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.api.enumerated.DataLocation;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.ValidationType;
import com.intuit.tank.vm.script.util.AddActionScope;
import com.intuit.tank.vm.script.util.RemoveActionScope;
import com.intuit.tank.vm.script.util.ReplaceActionScope;
import com.intuit.tank.vm.settings.LogicStepConfig;
import com.intuit.tank.vm.settings.SelectableItem;
import com.intuit.tank.vm.settings.TankConfig;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConfigServiceV2Impl implements ConfigServiceV2 {

    private static final String SERVICE = "config";
    /** Prefix given to the names of users removed by data deletion requests. */
    static final String DELETED_USER_PREFIX = "deleted_user_";

    @Override
    public UiOptions getOptions() {
        RestAuthorization.requireUser(SERVICE);
        TankConfig config = new TankConfig();

        List<Option> regions = config.getStandalone()
                ? List.of(region(VMRegion.STANDALONE))
                : config.getVmManagerConfig().getConfiguredRegions().stream().sorted()
                        .map(ConfigServiceV2Impl::region).collect(Collectors.toList());

        List<InstanceTypeOption> instanceTypes = config.getVmManagerConfig().getInstanceTypes().stream()
                .map(t -> new InstanceTypeOption(t.getName(), t.getDisplay(), t.getUsers(), t.isDefault()))
                .collect(Collectors.toList());

        LogicStepConfig logicStep = config.getLogicStepConfig();

        return new UiOptions(
                selectable(config.getProductConfig().getProducts()),
                selectable(config.getLocationsConfig().getLocations()),
                regions,
                options(LoggingProfile.values(), Enum::name, LoggingProfile::getDisplayName, LoggingProfile::getDescription),
                options(StopBehavior.values(), Enum::name, StopBehavior::getDisplay, StopBehavior::getDescription),
                options(TerminationPolicy.values(), Enum::name, TerminationPolicy::getDisplay, null),
                options(IncrementStrategy.values(), Enum::name, IncrementStrategy::getDisplay, null),
                instanceTypes,
                byLabel(config.getAgentConfig().getTankClientMap()),
                byLabel(config.getAgentConfig().getResultsTypeMap()),
                stepOptions(),
                filterOptions(),
                logicStep != null ? new UiOptions.LogicStepOptions(logicStep.getInsertBefore(), logicStep.getAppendAfter())
                        : new UiOptions.LogicStepOptions("", ""));
    }

    @Override
    public List<String> getUserNames() {
        RestAuthorization.requireUser(SERVICE);
        return new UserDao().findAll().stream()
                .map(User::getName)
                .filter(name -> name != null && !name.startsWith(DELETED_USER_PREFIX))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private static Map<String, List<Option>> stepOptions() {
        Map<String, List<Option>> options = new LinkedHashMap<>();
        options.put("failureTypes", options(FailureTypes.values(), FailureTypes::getValue, FailureTypes::getDisplayName, null));
        options.put("validationTypes", validationTypes());
        options.put("dataLocations", options(DataLocation.values(), Enum::name, Enum::name, null));
        options.put("authSchemes", options(AuthScheme.values(), Enum::name, AuthScheme::getRepresentation, null));
        options.put("requestFormats", List.of(
                Option.of(ScriptConstants.NVP_TYPE, "Key-Value"),
                Option.of(ScriptConstants.XML_TYPE, "XML"),
                Option.of(ScriptConstants.JSON_TYPE, "JSON"),
                Option.of(ScriptConstants.PLAIN_TEXT_TYPE, "Plain Text"),
                Option.of(ScriptConstants.MULTI_PART_TYPE, "Multi-Part")));
        options.put("responseFormats", List.of(
                Option.of("json", "JSON"),
                Option.of("raw", "RAW"),
                Option.of("xml", "XML")));
        return options;
    }

    private static Map<String, List<Option>> filterOptions() {
        Map<String, List<Option>> options = new LinkedHashMap<>();
        options.put("filterTypes", options(ScriptFilterType.values(), Enum::name, ScriptFilterType::getDisplay, null));
        options.put("conditionScopes", options(ConditionScope.values(), Enum::name, ConditionScope::getValue, null));
        options.put("conditionMatches", options(ConditionMatch.values(), Enum::name, ConditionMatch::getValue, null));
        options.put("actionTypes", options(ScriptFilterActionType.values(), Enum::name, Enum::name, null));
        options.put("addActionScopes", options(AddActionScope.values(), AddActionScope::getValue, AddActionScope::getValue, null));
        options.put("removeActionScopes", options(RemoveActionScope.values(), RemoveActionScope::getValue, RemoveActionScope::getValue, null));
        options.put("replaceActionScopes", options(ReplaceActionScope.values(), ReplaceActionScope::getValue, ReplaceActionScope::getValue, null));
        options.put("validationTypes", validationTypes());
        // filters cannot jump to a group, so that failure type is not offered (as in the JSF filter editor)
        options.put("onFailOptions", Arrays.stream(FailureTypes.values())
                .filter(t -> t != FailureTypes.gotoGroupRequest)
                .map(t -> Option.of(t.getValue(), t.getDisplayName()))
                .collect(Collectors.toList()));
        return options;
    }

    private static List<Option> validationTypes() {
        return options(ValidationType.values(), Enum::name, ValidationType::getRepresentation, null);
    }

    private static Option region(VMRegion region) {
        return new Option(region.name(), region.getName(), region.getDescription());
    }

    private static List<Option> selectable(Collection<SelectableItem> items) {
        return items.stream().map(i -> Option.of(i.getValue(), i.getDisplayName())).collect(Collectors.toList());
    }

    /**
     * Options from a display-name to value map, sorted by display name as in the JSF pages.
     */
    private static List<Option> byLabel(Map<String, String> labelToValue) {
        return labelToValue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> Option.of(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
    }

    private static <T> List<Option> options(T[] values, Function<T, String> value, Function<T, String> label,
                                            Function<T, String> description) {
        return Arrays.stream(values)
                .map(v -> new Option(value.apply(v), label.apply(v), description != null ? description.apply(v) : null))
                .collect(Collectors.toList());
    }
}
