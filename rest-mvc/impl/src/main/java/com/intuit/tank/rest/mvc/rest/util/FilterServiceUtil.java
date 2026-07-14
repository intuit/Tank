/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.filters.models.FilterActionTO;
import com.intuit.tank.filters.models.FilterConditionTO;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.util.ScriptFilterType;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

public class FilterServiceUtil {

    /**
     * @param g
     * @return
     */
    public static FilterGroupTO filterGroupToTO(ScriptFilterGroup g) {
        return FilterGroupTO.builder()
                .withId(g.getId())
                .withName(g.getName())
                .withProductName(g.getProductName())
                .build();
    }

    public static FilterTO filterToTO(ScriptFilter g) {
        return FilterTO.builder()
                .withId(g.getId())
                .withCreated(g.getCreated())
                .withModified(g.getModified())
                .withCreator(g.getCreator())
                .withName(g.getName())
                .withProductName(g.getProductName())
                .withPersist(g.getPersist())
                .withAllConditionsMustPass(g.getAllConditionsMustPass())
                .withFilterType(g.getFilterType().name())
                .withExternalScriptId(g.getExternalScriptId())
                .withConditions(g.getConditions().stream()
                        .map(FilterServiceUtil::conditionToTO)
                        .sorted(conditionComparator())
                        .collect(Collectors.toList()))
                .withActions(g.getActions().stream()
                        .map(FilterServiceUtil::actionToTO)
                        .sorted(actionComparator())
                        .collect(Collectors.toList()))
                .build();

    }

    public static ScriptFilter toScriptFilter(FilterTO request, ScriptFilter filter) {
        filter.setName(request.getName());
        filter.setProductName(request.getProductName());
        if (request.getCreator() != null) {
            filter.setCreator(request.getCreator());
        }
        filter.setPersist(request.isPersist());
        filter.setAllConditionsMustPass(request.isAllConditionsMustPass());
        filter.setFilterType(request.getFilterType() != null
                ? ScriptFilterType.valueOf(request.getFilterType())
                : ScriptFilterType.INTERNAL);
        filter.setExternalScriptId(request.getExternalScriptId());
        filter.setConditions(request.getConditions() == null
                ? new HashSet<>()
                : request.getConditions().stream()
                        .map(FilterServiceUtil::toScriptFilterCondition)
                        .collect(Collectors.toSet()));
        filter.setActions(request.getActions() == null
                ? new HashSet<>()
                : request.getActions().stream()
                        .map(FilterServiceUtil::toScriptFilterAction)
                        .collect(Collectors.toSet()));
        return filter;
    }

    private static FilterConditionTO conditionToTO(ScriptFilterCondition condition) {
        return FilterConditionTO.builder()
                .withScope(condition.getScope())
                .withCondition(condition.getCondition())
                .withValue(condition.getValue())
                .build();
    }

    private static FilterActionTO actionToTO(ScriptFilterAction action) {
        return FilterActionTO.builder()
                .withAction(action.getAction())
                .withScope(action.getScope())
                .withKey(action.getKey())
                .withValue(action.getValue())
                .build();
    }

    private static ScriptFilterCondition toScriptFilterCondition(FilterConditionTO condition) {
        ScriptFilterCondition result = new ScriptFilterCondition();
        result.setScope(condition.getScope());
        result.setCondition(condition.getCondition());
        result.setValue(condition.getValue());
        return result;
    }

    private static ScriptFilterAction toScriptFilterAction(FilterActionTO action) {
        ScriptFilterAction result = new ScriptFilterAction();
        result.setAction(action.getAction());
        result.setScope(action.getScope());
        result.setKey(action.getKey());
        result.setValue(action.getValue());
        return result;
    }

    private static Comparator<FilterConditionTO> conditionComparator() {
        return Comparator.comparing(FilterConditionTO::getScope, Comparator.nullsFirst(String::compareTo))
                .thenComparing(FilterConditionTO::getCondition, Comparator.nullsFirst(String::compareTo))
                .thenComparing(FilterConditionTO::getValue, Comparator.nullsFirst(String::compareTo));
    }

    private static Comparator<FilterActionTO> actionComparator() {
        return Comparator.comparing(
                        (FilterActionTO action) -> action.getAction() == null ? null : action.getAction().name(),
                        Comparator.nullsFirst(String::compareTo))
                .thenComparing(FilterActionTO::getScope, Comparator.nullsFirst(String::compareTo))
                .thenComparing(FilterActionTO::getKey, Comparator.nullsFirst(String::compareTo))
                .thenComparing(FilterActionTO::getValue, Comparator.nullsFirst(String::compareTo));
    }
}
