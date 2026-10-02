/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.ClientException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.models.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.models.DraftSteps;
import com.intuit.tank.rest.mvc.rest.models.LogicTestRequest;
import com.intuit.tank.rest.mvc.rest.models.LogicTestResult;
import com.intuit.tank.rest.mvc.rest.models.ScriptValidation;
import com.intuit.tank.rest.mvc.rest.models.StepMatch;
import com.intuit.tank.rest.mvc.rest.models.StepReplaceRequest;
import com.intuit.tank.rest.mvc.rest.models.StepSearchRequest;
import com.intuit.tank.rest.mvc.rest.models.ValidateStepsRequest;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.rest.mvc.rest.util.JobDetailFormatter;
import com.intuit.tank.rest.mvc.rest.util.JobValidator;
import com.intuit.tank.rest.mvc.rest.util.ProjectValidator;
import com.intuit.tank.rest.mvc.rest.util.ScriptFilterUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
import com.intuit.tank.script.CommonSection;
import com.intuit.tank.script.RequestStepSection;
import com.intuit.tank.script.Section;
import com.intuit.tank.script.SleepTimeSection;
import com.intuit.tank.script.ThinkTimeSection;
import com.intuit.tank.script.VariableSection;
import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.script.replace.AbstractReplacement;
import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.script.replace.ReplacementFactory;
import com.intuit.tank.script.replace.SearchMode;
import com.intuit.tank.vm.settings.AccessRight;
import jakarta.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScriptDraftServiceV2Impl implements ScriptDraftServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(ScriptDraftServiceV2Impl.class);
    private static final String SERVICE = "scripts";

    static final int MAX_STEPS = 20_000;
    static final int MAX_LOGIC_SCRIPT_LENGTH = 64 * 1024;

    /** Every searchable part of a step, by name. */
    static final Map<String, Section> SECTIONS = Stream.of(RequestStepSection.values(), VariableSection.values(),
                    ThinkTimeSection.values(), SleepTimeSection.values(), CommonSection.values())
            .flatMap(Stream::of)
            .collect(Collectors.toMap(s -> ((Enum<?>) s).name(), s -> (Section) s, (a, b) -> a, LinkedHashMap::new));

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private ServletContext servletContext;

    @Override
    public List<StepMatch> search(StepSearchRequest request) {
        RestAuthorization.requireUser(SERVICE);
        if (request == null) {
            throw new GenericServiceBadRequestException(SERVICE, "search", "request body is required");
        }
        List<Section> sections = sections(request.query(), request.sections());
        List<ScriptStep> steps = toSteps(request.steps());
        List<StepMatch> matches = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            ScriptStep step = steps.get(i);
            for (Section section : sections) {
                for (ReplaceEntity re : replacement(section).getReplacements(step, request.query(), "", SearchMode.all)) {
                    matches.add(new StepMatch(step.getUuid(), i, ((Enum<?>) re.getSection()).name(), re.getKey(), re.getValue()));
                }
            }
        }
        return matches;
    }

    @Override
    public DraftSteps replace(StepReplaceRequest request) {
        RestAuthorization.requireUser(SERVICE);
        if (request == null) {
            throw new GenericServiceBadRequestException(SERVICE, "replace", "request body is required");
        }
        List<Section> sections = sections(request.query(), request.sections());
        if (request.replacement() == null) {
            throw new GenericServiceBadRequestException(SERVICE, "replacement", "replacement is required");
        }
        ReplaceMode mode;
        try {
            mode = request.mode() == null ? ReplaceMode.VALUE : ReplaceMode.valueOf(request.mode().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GenericServiceBadRequestException(SERVICE, "mode", "mode must be KEY or VALUE");
        }
        Set<String> only = request.uuids() != null ? new HashSet<>(request.uuids()) : null;
        List<ScriptStep> steps = toSteps(request.steps());
        int changed = 0;
        for (ScriptStep step : steps) {
            if (only != null && !only.contains(step.getUuid())) {
                continue;
            }
            boolean stepChanged = false;
            for (Section section : sections) {
                // find every match first, as the web UI does, then replace each one
                for (ReplaceEntity re : replacement(section).getReplacements(step, request.query(), request.replacement(),
                        SearchMode.all)) {
                    replacement(re.getSection()).replace(step, request.replacement(), re.getKey(), mode);
                    stepChanged = true;
                }
            }
            if (stepChanged) {
                ScriptUtil.updateStepLabel(step);
                changed++;
            }
        }
        return new DraftSteps(toTransferObjects(steps), changed);
    }

    @Override
    public DraftSteps applyFilters(ApplyFiltersRequest request) {
        RestAuthorization.requireUser(SERVICE);
        if (request == null || request.filterIds() == null || request.filterIds().isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "filterIds", "at least one filter id is required");
        }
        List<ScriptFilter> filters = findFilters(request.filterIds());
        List<ScriptStep> steps = toSteps(request.steps());
        Map<String, String> before = fingerprints(steps);
        List<ScriptStep> filtered = new ArrayList<>(ScriptFilterUtil.applyFilters(filters, steps));
        Script draft = new Script();
        draft.setId(-1); // a draft, not a copy: steps keep their uuids and only new steps get one
        draft.setScriptSteps(filtered);
        ScriptUtil.setScriptStepLabels(draft);
        Map<String, String> after = fingerprints(draft.getScriptSteps());
        Set<String> removed = new HashSet<>(before.keySet());
        removed.removeAll(after.keySet());
        int changed = removed.size() + (int) after.entrySet().stream()
                .filter(e -> !e.getValue().equals(before.get(e.getKey()))).count();
        return new DraftSteps(toTransferObjects(draft.getScriptSteps()), changed);
    }

    @Override
    public ScriptValidation validate(ValidateStepsRequest request) {
        RestAuthorization.requireUser(SERVICE);
        if (request == null) {
            throw new GenericServiceBadRequestException(SERVICE, "validate", "request body is required");
        }
        Script script = new Script();
        script.setName(StringUtils.defaultIfBlank(request.name(), "script"));
        script.setScriptSteps(toSteps(request.steps()));
        JobValidator validator = new JobValidator(script);
        List<String> warnings = new ArrayList<>();
        validator.getBestPracticeViolations().stream().sorted().map(ProjectValidator::plainText).forEach(warnings::add);
        validator.getOrphanedVariables().stream().sorted()
                .forEach(v -> warnings.add("Variable '" + v + "' is used but never set."));
        validator.getSuperfluousVariables().stream().sorted()
                .forEach(v -> warnings.add("Variable '" + v + "' is set but never used."));
        return new ScriptValidation(validator.getExpectedTime(script.getName()), warnings,
                validator.getOrphanedVariables().stream().sorted().toList(),
                validator.getSuperfluousVariables().stream().sorted().toList(),
                validator.getDataFiles().stream().sorted().toList(),
                JobDetailFormatter.createJobDetails(validator, script.getName()));
    }

    @Override
    public LogicTestResult testLogic(LogicTestRequest request) {
        RestAuthorization.requireUser(SERVICE);
        if (request == null || StringUtils.isBlank(request.script())) {
            throw new GenericServiceBadRequestException(SERVICE, "script", "script is required");
        }
        if (request.script().length() > MAX_LOGIC_SCRIPT_LENGTH) {
            throw new GenericServiceBadRequestException(SERVICE, "script",
                    "script must be at most " + MAX_LOGIC_SCRIPT_LENGTH + " characters");
        }
        if (!RestAuthorization.hasRight(AccessRight.EDIT_SCRIPT)) {
            Script owned = request.scriptId() != null ? new ScriptDao().findById(request.scriptId()) : null;
            RestAuthorization.requireRightOrOwner(AccessRight.EDIT_SCRIPT, owned, SERVICE);
        }
        LogicStepTester tester = new ServletInjector<LogicStepTester>().getManagedBean(servletContext, LogicStepTester.class);
        try {
            LogicTestResult result = tester.test(request);
            LOGGER.info("{} tested a logic step ({} ms{})", RestAuthorization.currentUserName(), result.durationMs(),
                    result.timedOut() ? ", timed out" : "");
            return result;
        } catch (IllegalStateException e) {
            throw new ClientException(e.getMessage(), 429);
        }
    }

    private static List<Section> sections(String query, List<String> names) {
        if (StringUtils.isBlank(query)) {
            throw new GenericServiceBadRequestException(SERVICE, "query", "query is required");
        }
        if (names == null || names.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "sections", "at least one section is required");
        }
        List<String> unknown = names.stream().filter(n -> !SECTIONS.containsKey(n)).toList();
        if (!unknown.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "sections", "unknown sections " + unknown
                    + "; known sections are " + String.join(", ", SECTIONS.keySet()));
        }
        return names.stream().distinct().map(SECTIONS::get).toList();
    }

    private static AbstractReplacement replacement(Section section) {
        return ReplacementFactory.getReplacementForSection(section);
    }

    private static List<ScriptStep> toSteps(List<ScriptStepTO> steps) {
        if (steps == null) {
            throw new GenericServiceBadRequestException(SERVICE, "steps", "steps are required");
        }
        if (steps.size() > MAX_STEPS) {
            throw new GenericServiceBadRequestException(SERVICE, "steps", "at most " + MAX_STEPS + " steps");
        }
        if (steps.stream().anyMatch(s -> s == null)) {
            throw new GenericServiceBadRequestException(SERVICE, "steps", "steps must not contain empty entries");
        }
        return steps.stream().map(ScriptServiceUtil::transferObjectToScriptStep).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<ScriptStepTO> toTransferObjects(List<ScriptStep> steps) {
        return steps.stream().map(ScriptServiceUtil::scriptStepToTransferObject).toList();
    }

    private static List<ScriptFilter> findFilters(List<Integer> filterIds) {
        List<Integer> ids = filterIds.stream().distinct().toList();
        Map<Integer, ScriptFilter> byId = new ScriptFilterDao().findForIds(new ArrayList<>(ids)).stream()
                .collect(Collectors.toMap(ScriptFilter::getId, f -> f, (a, b) -> a));
        List<Integer> missing = ids.stream().filter(id -> !byId.containsKey(id)).toList();
        if (!missing.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "filterIds", "no script filters with ids " + missing);
        }
        return ids.stream().map(byId::get).toList();
    }

    /** Each step's content by uuid, to count what an operation changed. */
    private static Map<String, String> fingerprints(List<ScriptStep> steps) {
        Map<String, String> result = new HashMap<>();
        for (ScriptStep step : steps) {
            if (step.getUuid() == null) {
                continue;
            }
            ScriptStepTO to = ScriptServiceUtil.scriptStepToTransferObject(step).toBuilder()
                    .withStepIndex(0).withLabel(null).build();
            ObjectNode node = JSON.valueToTree(to);
            // step data are sets: sort them so equal content gives an equal fingerprint
            node.properties().forEach(field -> {
                if (field.getValue().isArray()) {
                    List<String> items = new ArrayList<>();
                    field.getValue().forEach(item -> items.add(item.toString()));
                    items.sort(null);
                    field.setValue(JSON.valueToTree(items));
                }
            });
            result.put(step.getUuid(), node.toString());
        }
        return result;
    }
}
