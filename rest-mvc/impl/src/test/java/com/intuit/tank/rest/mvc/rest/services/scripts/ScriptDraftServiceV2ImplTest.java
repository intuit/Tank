/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.ClientException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.models.DraftSteps;
import com.intuit.tank.rest.mvc.rest.models.LogicTestRequest;
import com.intuit.tank.rest.mvc.rest.models.LogicTestResult;
import com.intuit.tank.rest.mvc.rest.models.ScriptValidation;
import com.intuit.tank.rest.mvc.rest.models.StepMatch;
import com.intuit.tank.rest.mvc.rest.models.StepReplaceRequest;
import com.intuit.tank.rest.mvc.rest.models.StepSearchRequest;
import com.intuit.tank.rest.mvc.rest.models.ValidateStepsRequest;
import com.intuit.tank.rest.mvc.rest.util.JobDetailFormatter;
import com.intuit.tank.rest.mvc.rest.util.JobValidator;
import com.intuit.tank.rest.mvc.rest.util.ScriptFilterUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.vm.settings.AccessRight;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ScriptDraftServiceV2ImplTest {

    @InjectMocks
    private ScriptDraftServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;
    private final List<AutoCloseable> closeables = new ArrayList<>();
    private final LogicStepTester tester = mock(LogicStepTester.class);

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        useConfig(true, Map.of(AccessRight.EDIT_SCRIPT, List.of("editors")));
        actAs(user("alice", "editors"));
        closeables.add(Mockito.mockConstruction(ServletInjector.class, (injector, ctx) ->
                when(injector.getManagedBean(eq(servletContext), eq(LogicStepTester.class))).thenReturn(tester)));
    }

    @AfterEach
    void tearDown() throws Exception {
        for (AutoCloseable c : closeables) {
            c.close();
        }
        mocks.close();
        reset();
    }

    private static ScriptStepTO request(String uuid, String host, String headerValue) {
        ScriptStep step = new ScriptStep();
        step.setUuid(uuid);
        step.setType(ScriptConstants.REQUEST);
        step.setMethod("GET");
        step.setHostname(host);
        step.setSimplePath("/" + uuid);
        Set<RequestData> headers = new HashSet<>();
        headers.add(new RequestData("X-Env", headerValue, "requestHeader"));
        step.setRequestheaders(headers);
        return ScriptServiceUtil.scriptStepToTransferObject(step);
    }

    private static List<ScriptStepTO> steps() {
        return List.of(request("a", "qa.example.com", "qa"), request("b", "prod.example.com", "prod"),
                request("c", "qa.example.com", "qa"));
    }

    // search

    @Test
    void search_findsEachMatchingPart() {
        List<StepMatch> matches = service.search(new StepSearchRequest(steps(), "qa.*", List.of("host")));
        assertEquals(List.of("a", "c"), matches.stream().map(StepMatch::uuid).toList());
        assertEquals(List.of(0, 2), matches.stream().map(StepMatch::position).toList());
        assertEquals("host", matches.get(0).section());
        assertEquals("qa.example.com", matches.get(0).value());

        List<StepMatch> headers = service.search(new StepSearchRequest(steps(), "prod", List.of("requestHeaderValue")));
        assertEquals(1, headers.size());
        assertEquals("X-Env", headers.get(0).key());
    }

    @Test
    void search_validation() {
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.search(new StepSearchRequest(steps(), " ", List.of("host"))));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.search(new StepSearchRequest(steps(), "x", List.of())));
        GenericServiceBadRequestException e = assertThrows(GenericServiceBadRequestException.class,
                () -> service.search(new StepSearchRequest(steps(), "x", List.of("hostname"))));
        assertTrue(e.getMessage().contains("[hostname]"), e.getMessage());
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.search(new StepSearchRequest(null, "x", List.of("host"))));
    }

    @Test
    void search_andReplaceResponseData() {
        ScriptStep step = ScriptServiceUtil.transferObjectToScriptStep(request("a", "h", "v"));
        Set<RequestData> responseData = new HashSet<>();
        responseData.add(new RequestData("$.status", "OK", "assignment"));
        step.setResponseData(responseData);
        List<ScriptStepTO> draft = List.of(ScriptServiceUtil.scriptStepToTransferObject(step));

        List<StepMatch> matches = service.search(new StepSearchRequest(draft, "OK", List.of("responseContent")));
        assertEquals(List.of("a"), matches.stream().map(StepMatch::uuid).toList());

        DraftSteps replaced = service.replace(new StepReplaceRequest(draft, "OK", List.of("responseContent"), "SUCCESS", null, null));
        assertEquals(1, replaced.changed());
        assertEquals("SUCCESS", replaced.steps().get(0).getResponseData().iterator().next().getValue());
    }

    // replace

    @Test
    void replace_inAllOrChosenSteps() {
        DraftSteps all = service.replace(new StepReplaceRequest(steps(), "qa.*", List.of("host"), "stage.example.com", null, null));
        assertEquals(2, all.changed());
        assertEquals(List.of("stage.example.com", "prod.example.com", "stage.example.com"),
                all.steps().stream().map(ScriptStepTO::getHostname).toList());

        DraftSteps one = service.replace(new StepReplaceRequest(steps(), "qa.*", List.of("host"), "stage.example.com", "VALUE",
                List.of("c")));
        assertEquals(1, one.changed());
        assertEquals("qa.example.com", one.steps().get(0).getHostname());
        assertEquals("stage.example.com", one.steps().get(2).getHostname());
    }

    @Test
    void replace_keyMode() {
        DraftSteps result = service.replace(new StepReplaceRequest(steps(), "X-Env", List.of("requestHeaderKey"), "X-Stage",
                "key", List.of("a")));
        assertEquals(1, result.changed());
        assertEquals("X-Stage", result.steps().get(0).getRequestheaders().iterator().next().getKey());
    }

    @Test
    void replace_validation() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.replace(
                new StepReplaceRequest(steps(), "x", List.of("host"), null, null, null)));
        assertThrows(GenericServiceBadRequestException.class, () -> service.replace(
                new StepReplaceRequest(steps(), "x", List.of("host"), "y", "BOTH", null)));
    }

    // apply filters

    @Test
    void applyFilters_inOrderAndCountsChanges() {
        ScriptFilter first = new ScriptFilter();
        first.setId(2);
        ScriptFilter second = new ScriptFilter();
        second.setId(1);
        try (MockedConstruction<ScriptFilterDao> dao = Mockito.mockConstruction(ScriptFilterDao.class,
                     (mock, ctx) -> when(mock.findForIds(anyList())).thenReturn(List.of(second, first)));
             MockedStatic<ScriptFilterUtil> util = Mockito.mockStatic(ScriptFilterUtil.class)) {
            util.when(() -> ScriptFilterUtil.applyFilters(anyList(), anyList())).thenAnswer(i -> {
                List<ScriptStep> in = i.getArgument(1);
                in.get(0).setHostname("filtered.example.com"); // change one step
                in.remove(1);                                  // drop another
                return in;
            });
            DraftSteps result = service.applyFilters(new ApplyFiltersRequest(steps(), List.of(2, 1)));
            assertEquals(2, result.changed());
            assertEquals(List.of("a", "c"), result.steps().stream().map(ScriptStepTO::getUuid).toList(),
                    "draft steps keep their uuids");
            assertEquals(List.of(1, 2), result.steps().stream().map(ScriptStepTO::getStepIndex).toList());
            util.verify(() -> ScriptFilterUtil.applyFilters(eq(List.of(first, second)), anyList()));
        }
    }

    @Test
    void applyFilters_validation() {
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.applyFilters(new ApplyFiltersRequest(steps(), List.of())));
        try (MockedConstruction<ScriptFilterDao> dao = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findForIds(anyList())).thenReturn(List.of()))) {
            GenericServiceBadRequestException e = assertThrows(GenericServiceBadRequestException.class,
                    () -> service.applyFilters(new ApplyFiltersRequest(steps(), List.of(9))));
            assertTrue(e.getMessage().contains("[9]"), e.getMessage());
        }
    }

    // validate

    @Test
    void validate() {
        try (MockedConstruction<JobValidator> validators = Mockito.mockConstruction(JobValidator.class, (mock, ctx) -> {
                 when(mock.getExpectedTime("Checkout")).thenReturn(4_200L);
                 when(mock.getBestPracticeViolations()).thenReturn(Set.of("&nbsp;Rule: x.<br/>&nbsp;detail"));
                 when(mock.getOrphanedVariables()).thenReturn(Set.of("token"));
                 when(mock.getSuperfluousVariables()).thenReturn(Set.of());
                 when(mock.getDataFiles()).thenReturn(Set.of("users.csv"));
             });
             MockedStatic<JobDetailFormatter> formatter = Mockito.mockStatic(JobDetailFormatter.class)) {
            formatter.when(() -> JobDetailFormatter.createJobDetails(any(JobValidator.class), eq("Checkout"))).thenReturn("<p/>");
            ScriptValidation result = service.validate(new ValidateStepsRequest("Checkout", steps()));
            assertEquals(4_200L, result.durationMs());
            assertEquals(List.of("Rule: x. detail", "Variable 'token' is used but never set."), result.warnings());
            assertEquals(List.of("users.csv"), result.dataFiles());
            assertEquals("<p/>", result.detailsHtml());
        }
    }

    // logic test

    @Test
    void logicTest_runsForEditors() {
        LogicTestRequest request = new LogicTestRequest(null, "log('x')", Map.of(), null, Map.of(), null, Map.of());
        when(tester.test(request)).thenReturn(new LogicTestResult("x", false, 3));
        assertEquals("x", service.testLogic(request).output());
    }

    @Test
    void logicTest_ownersOfTheScriptMayTest() {
        actAs(user("bob"));
        Script bobs = new Script();
        bobs.setCreator("bob");
        LogicTestRequest request = new LogicTestRequest(5, "log('x')", null, null, null, null, null);
        when(tester.test(request)).thenReturn(new LogicTestResult("x", false, 3));
        try (MockedConstruction<ScriptDao> dao = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenAnswer(i -> i.<Integer>getArgument(0) == 5 ? bobs : null))) {
            assertEquals("x", service.testLogic(request).output());
            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.testLogic(
                    new LogicTestRequest(6, "log('x')", null, null, null, null, null)));
            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.testLogic(
                    new LogicTestRequest(null, "log('x')", null, null, null, null, null)));
        }
    }

    @Test
    void logicTest_limits() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.testLogic(
                new LogicTestRequest(null, " ", null, null, null, null, null)));
        assertThrows(GenericServiceBadRequestException.class, () -> service.testLogic(
                new LogicTestRequest(null, "x".repeat(ScriptDraftServiceV2Impl.MAX_LOGIC_SCRIPT_LENGTH + 1), null, null, null, null, null)));
        when(tester.test(any())).thenThrow(new IllegalStateException("busy"));
        ClientException busy = assertThrows(ClientException.class, () -> service.testLogic(
                new LogicTestRequest(null, "log('x')", null, null, null, null, null)));
        assertEquals(429, busy.getStatusCode());
    }

    @Test
    void needsAUser() {
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class,
                () -> service.search(new StepSearchRequest(steps(), "x", List.of("host"))));
    }

    @Test
    void everySectionHasAReplacement() {
        ScriptDraftServiceV2Impl.SECTIONS.values().forEach(section ->
                assertNotNull(com.intuit.tank.script.replace.ReplacementFactory.getReplacementForSection(section), section.toString()));
    }
}
