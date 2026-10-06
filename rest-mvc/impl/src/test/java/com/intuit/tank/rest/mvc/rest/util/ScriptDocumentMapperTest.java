/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.rest.mvc.rest.models.ScriptDocument;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.TimerAction;
import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.script.models.StepDataTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ScriptDocumentMapperTest {

    private static final ScriptDocument.Permissions ALL = new ScriptDocument.Permissions(true, true);

    private static ScriptStep step(String uuid, String type, RequestData... data) {
        ScriptStep step = new ScriptStep();
        step.setUuid(uuid);
        step.setType(type);
        step.setName(uuid);
        step.setData(new HashSet<>(Arrays.asList(data)));
        return step;
    }

    private static ScriptStep request(String uuid, String response) {
        ScriptStep step = step(uuid, ScriptConstants.REQUEST);
        step.setMethod("GET");
        step.setHostname("example.com");
        step.setSimplePath("/" + uuid);
        step.setResponse(response);
        return step;
    }

    private static ScriptStep auth(String uuid, String password) {
        return step(uuid, ScriptConstants.AUTHENTICATION,
                new RequestData(ScriptConstants.AUTH_USER_NAME, "svc", ""),
                new RequestData(ScriptConstants.AUTH_PASSWORD, password, ""));
    }

    private static ScriptStep timer(String uuid, TimerAction action, String pair) {
        return step(uuid, ScriptConstants.TIMER,
                new RequestData(ScriptConstants.IS_START, action.name(), ""),
                new RequestData(ScriptConstants.AGGREGATOR_PAIR, pair, ""),
                new RequestData(ScriptConstants.LOGGING_KEY, "checkout", ""));
    }

    private static Script script(ScriptStep... steps) {
        Script script = new Script();
        script.setId(5);
        script.setName("Checkout");
        script.setCreator("alice");
        script.setModified(new Date(1_700_000_000_000L));
        script.getScriptSteps().addAll(new ArrayList<>(Arrays.asList(steps)));
        return script;
    }

    private static String password(ScriptStepTO step) {
        return step.getData().stream().filter(d -> ScriptConstants.AUTH_PASSWORD.equals(d.getKey()))
                .map(StepDataTO::getValue).findFirst().orElse(null);
    }

    private static ScriptDocument document(Script script, List<ScriptStepTO> steps) {
        return new ScriptDocument(script.getId(), " Checkout v2 ", "Shop", "comments", "mallory", null, script.getModified(),
                steps, null);
    }

    @Test
    void toDocument_masksPasswordsAndOmitsResponses() {
        ScriptDocument doc = ScriptDocumentMapper.toDocument(script(request("r1", "<html/>"), auth("a1", "s3cret")), ALL);
        assertEquals("Checkout", doc.name());
        assertEquals("alice", doc.owner());
        assertSame(ALL, doc.permissions());
        assertEquals(2, doc.steps().size());
        assertNull(doc.steps().get(0).getResponse(), "responses come from the response endpoint");
        assertEquals(ScriptDocument.MASKED_PASSWORD, password(doc.steps().get(1)));
        assertTrue(doc.steps().get(1).getData().stream().anyMatch(d -> "svc".equals(d.getValue())), "other data is kept");
    }

    @Test
    void apply_keepsResponsesAndMaskedPasswordsByUuid() {
        Script script = script(request("r1", "<html/>"), auth("a1", "s3cret"));
        ScriptDocument doc = ScriptDocumentMapper.toDocument(script, ALL);

        List<String> errors = ScriptDocumentMapper.apply(document(script, doc.steps()), script);

        assertEquals(List.of(), errors);
        assertEquals("Checkout v2", script.getName());
        assertEquals("Shop", script.getProductName());
        assertEquals("alice", script.getCreator(), "the owner is not changed by the editor");
        assertEquals("<html/>", script.getScriptSteps().get(0).getResponse());
        assertEquals("s3cret", script.getScriptSteps().get(1).getData().stream()
                .filter(d -> ScriptConstants.AUTH_PASSWORD.equals(d.getKey())).findFirst().orElseThrow().getValue());
    }

    @Test
    void apply_newPasswordReplacesTheStoredOne() {
        Script script = script(auth("a1", "s3cret"));
        ScriptStepTO edited = ScriptDocumentMapper.toEditorStep(script.getScriptSteps().get(0));
        Set<StepDataTO> data = new HashSet<>();
        edited.getData().forEach(d -> data.add(ScriptConstants.AUTH_PASSWORD.equals(d.getKey())
                ? d.toBuilder().withValue("n3w").build() : d));
        ScriptStepTO changed = edited.toBuilder().clearData().withData(data).build();

        ScriptDocumentMapper.apply(document(script, List.of(changed)), script);

        assertEquals("n3w", script.getScriptSteps().get(0).getData().stream()
                .filter(d -> ScriptConstants.AUTH_PASSWORD.equals(d.getKey())).findFirst().orElseThrow().getValue());
    }

    @Test
    void apply_maskedPasswordWithoutStoredStepIsAnError() {
        Script script = script(auth("a1", "s3cret"));
        ScriptStepTO copy = ScriptDocumentMapper.toEditorStep(script.getScriptSteps().get(0)).toBuilder().withUuid("new").build();
        List<String> errors = ScriptDocumentMapper.apply(document(script, List.of(copy)), script);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("masked password"), errors.get(0));
    }

    @Test
    void apply_reordersRenumbersAndGivesNewStepsUuids() {
        Script script = script(request("r1", null), request("r2", null));
        ScriptDocument doc = ScriptDocumentMapper.toDocument(script, ALL);
        ScriptStepTO added = ScriptDocumentMapper.toEditorStep(request("x", null)).toBuilder().withUuid(null).build();
        List<ScriptStepTO> steps = List.of(doc.steps().get(1), added, doc.steps().get(0));

        ScriptDocumentMapper.apply(document(script, steps), script);

        List<ScriptStep> saved = script.getScriptSteps();
        assertEquals("r2", saved.get(0).getUuid());
        assertNotNull(saved.get(1).getUuid(), "a new step gets a uuid");
        assertEquals("r1", saved.get(2).getUuid());
        assertEquals(List.of(1, 2, 3), saved.stream().map(ScriptStep::getStepIndex).toList());
        assertNotNull(saved.get(0).getLabel(), "labels are refreshed");
    }

    @Test
    void fixTimerOrder_putsTheEndAfterTheStart() {
        List<ScriptStep> steps = new ArrayList<>(List.of(timer("end", TimerAction.STOP, "start"), request("r1", null),
                timer("start", TimerAction.START, "end")));
        ScriptDocumentMapper.fixTimerOrder(steps);
        assertEquals(List.of("start", "r1", "end"), steps.stream().map(ScriptStep::getUuid).toList());

        ScriptDocumentMapper.fixTimerOrder(steps);
        assertEquals(List.of("start", "r1", "end"), steps.stream().map(ScriptStep::getUuid).toList(), "already in order");
    }

    @Test
    void validate() {
        Script script = script(request("r1", null));
        List<ScriptStepTO> steps = new ArrayList<>(ScriptDocumentMapper.toDocument(script, ALL).steps());
        steps.add(steps.get(0));
        steps.add(steps.get(0).toBuilder().withUuid("r9").withType(" ").build());
        steps.add(null);
        ScriptDocument bad = new ScriptDocument(5, " ", "x".repeat(256), "y".repeat(1025), null, null, null, steps, null);
        String all = String.join("\n", ScriptDocumentMapper.validate(bad));
        for (String expected : List.of("name is required", "productName must be at most", "comments must be at most",
                "step 2 repeats uuid r1", "step 3 needs a type", "step 4 is empty")) {
            assertTrue(all.contains(expected), "missing " + expected + " in " + all);
        }
        ScriptDocument noSteps = new ScriptDocument(5, "n", null, null, null, null, null, null, null);
        assertTrue(ScriptDocumentMapper.validate(noSteps).get(0).startsWith("steps are required"));
        assertEquals(List.of(), ScriptDocumentMapper.validate(document(script, List.of())));
    }
}
