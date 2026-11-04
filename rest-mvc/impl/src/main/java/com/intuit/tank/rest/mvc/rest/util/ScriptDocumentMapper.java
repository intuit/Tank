/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.rest.mvc.rest.models.ScriptDocument;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.TimerAction;
import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.script.models.StepDataTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converts between a stored script and the editor's {@link ScriptDocument}, and applies an edited document
 * the way the web UI's script editor saves.
 */
public final class ScriptDocumentMapper {

    static final int MAX_NAME_LENGTH = 255;
    static final int MAX_COMMENTS_LENGTH = 1024;

    private ScriptDocumentMapper() {
    }

    public static ScriptDocument toDocument(Script script, ScriptDocument.Permissions permissions) {
        List<ScriptStepTO> steps = script.getScriptSteps().stream()
                .map(ScriptDocumentMapper::toEditorStep)
                .collect(Collectors.toList());
        return new ScriptDocument(script.getId(), script.getName(), script.getProductName(), script.getComments(),
                script.getCreator(), script.getCreated(), script.getModified(), steps, permissions);
    }

    /** A step without its recorded response, and with any password masked. */
    static ScriptStepTO toEditorStep(ScriptStep step) {
        ScriptStepTO to = ScriptServiceUtil.scriptStepToTransferObject(step);
        ScriptStepTO.ScriptStepTOBuilder builder = to.toBuilder().withResponse(null);
        if (isAuthentication(to.getType()) && to.getData() != null) {
            Set<StepDataTO> data = to.getData().stream()
                    .map(d -> isPassword(d.getKey()) && d.getValue() != null
                            ? d.toBuilder().withValue(ScriptDocument.MASKED_PASSWORD).build() : d)
                    .collect(Collectors.toCollection(HashSet::new));
            builder.clearData().withData(data);
        }
        return builder.build();
    }

    /**
     * @return the problems that stop the document from being saved; empty when it is valid
     */
    public static List<String> validate(ScriptDocument document) {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(document.name())) {
            errors.add("name is required");
        } else if (document.name().trim().length() > MAX_NAME_LENGTH) {
            errors.add("name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        if (document.productName() != null && document.productName().length() > MAX_NAME_LENGTH) {
            errors.add("productName must be at most " + MAX_NAME_LENGTH + " characters");
        }
        if (document.comments() != null && document.comments().length() > MAX_COMMENTS_LENGTH) {
            errors.add("comments must be at most " + MAX_COMMENTS_LENGTH + " characters");
        }
        if (document.steps() == null) {
            errors.add("steps are required (send an empty list for a script with no steps)");
            return errors;
        }
        Set<String> uuids = new HashSet<>();
        for (int i = 0; i < document.steps().size(); i++) {
            ScriptStepTO step = document.steps().get(i);
            if (step == null) {
                errors.add("step " + (i + 1) + " is empty");
                continue;
            }
            if (StringUtils.isBlank(step.getType())) {
                errors.add("step " + (i + 1) + " needs a type");
            }
            if (step.getUuid() != null && !uuids.add(step.getUuid())) {
                errors.add("step " + (i + 1) + " repeats uuid " + step.getUuid());
            }
        }
        return errors;
    }

    /**
     * Replaces the script's header and steps with a validated document, keeping recorded responses and
     * masked passwords from the stored steps with the same {@code uuid}.
     *
     * @return the problems found while applying it; when not empty the script must not be saved
     */
    public static List<String> apply(ScriptDocument document, Script script) {
        Map<String, ScriptStep> stored = new HashMap<>();
        for (ScriptStep step : script.getScriptSteps()) {
            if (step.getUuid() != null) {
                stored.put(step.getUuid(), step);
            }
        }
        List<String> errors = new ArrayList<>();
        List<ScriptStep> steps = new ArrayList<>();
        for (int i = 0; i < document.steps().size(); i++) {
            ScriptStepTO to = document.steps().get(i).toBuilder().withResponse(null).build();
            ScriptStep step = ScriptServiceUtil.transferObjectToScriptStep(to);
            ScriptStep previous = to.getUuid() != null ? stored.get(to.getUuid()) : null;
            if (previous != null) {
                step.setResponse(previous.getResponse());
            }
            if (isAuthentication(step.getType())) {
                for (RequestData data : step.getData()) {
                    if (isPassword(data.getKey()) && ScriptDocument.MASKED_PASSWORD.equals(data.getValue())) {
                        String kept = previous != null ? password(previous) : null;
                        if (kept == null) {
                            errors.add("step " + (i + 1) + " has a masked password but no stored password to keep");
                        }
                        data.setValue(kept);
                    }
                }
            }
            steps.add(step);
        }
        fixTimerOrder(steps);

        script.setName(document.name().trim());
        script.setProductName(document.productName());
        script.setComments(document.comments());
        script.getScriptSteps().clear();
        script.getScriptSteps().addAll(steps);
        ScriptUtil.setScriptStepLabels(script);
        return errors;
    }

    /**
     * Puts each timer's end after its start, as the web UI's editor does before it saves.
     */
    static void fixTimerOrder(List<ScriptStep> steps) {
        Map<String, Integer> positions = new HashMap<>();
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).getUuid() != null) {
                positions.put(steps.get(i).getUuid(), i);
            }
        }
        for (int i = 0; i < steps.size(); i++) {
            ScriptStep step = steps.get(i);
            if (!ScriptConstants.TIMER.equals(step.getType()) || !isTimerStart(step)) {
                continue;
            }
            Integer pair = positions.get(value(step, ScriptConstants.AGGREGATOR_PAIR));
            if (pair != null && pair < i) {
                ScriptStep end = steps.get(pair);
                steps.set(pair, step);
                steps.set(i, end);
                positions.put(step.getUuid(), pair);
                positions.put(end.getUuid(), i);
            }
        }
    }

    private static boolean isTimerStart(ScriptStep step) {
        return TimerAction.START.name().equals(value(step, ScriptConstants.IS_START));
    }

    private static String value(ScriptStep step, String key) {
        return step.getData().stream().filter(d -> key.equals(d.getKey())).map(RequestData::getValue).findFirst().orElse(null);
    }

    private static String password(ScriptStep step) {
        return step.getData().stream().filter(d -> isPassword(d.getKey())).map(RequestData::getValue).findFirst().orElse(null);
    }

    private static boolean isAuthentication(String type) {
        return ScriptConstants.AUTHENTICATION.equals(type);
    }

    private static boolean isPassword(String key) {
        return ScriptConstants.AUTH_PASSWORD.equals(key);
    }
}
