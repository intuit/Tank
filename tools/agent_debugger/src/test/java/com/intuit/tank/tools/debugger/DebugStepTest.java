package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.runner.ErrorContainer;

public class DebugStepTest {

    @Test
    public void methodComesFromTheRequestStep() {
        HDRequest request = new HDRequest();
        request.setMethod("POST");
        RequestStep step = new RequestStep();
        step.setRequest(request);

        assertEquals("POST", new DebugStep(step).getMethod());
    }

    @Test
    public void methodIsEmptyForStepsThatAreNotRequests() {
        assertEquals("", new DebugStep(new ThinkTimeStep()).getMethod());
    }

    @Test
    public void holdsWhatHappenedWhenTheStepRan() {
        DebugStep step = new DebugStep(new ThinkTimeStep());
        BaseRequest request = mock(BaseRequest.class);
        BaseResponse response = mock(BaseResponse.class);
        step.setEntryVariables(Map.of("a", "1"));
        step.setExitVariables(Map.of("a", "2"));
        step.setRequest(request);
        step.setResponse(response);
        step.setLogEntry("log");
        VariableStep replacement = new VariableStep();
        step.setStepRun(replacement);

        assertEquals(Map.of("a", "1"), step.getEntryVariables());
        assertEquals(Map.of("a", "2"), step.getExitVariables());
        assertSame(request, step.getRequest());
        assertSame(response, step.getResponse());
        assertEquals("log", step.getLogEntry());
        assertSame(replacement, step.getStepRun());
    }

    @Test
    public void clearForgetsTheRunButKeepsTheStep() {
        ThinkTimeStep thinkTime = new ThinkTimeStep();
        DebugStep step = new DebugStep(thinkTime);
        step.setEntryVariables(Map.of("a", "1"));
        step.setExitVariables(Map.of("a", "2"));
        step.setRequest(mock(BaseRequest.class));
        step.setResponse(mock(BaseResponse.class));
        step.setLogEntry("log");

        step.clear();

        assertNull(step.getEntryVariables());
        assertNull(step.getExitVariables());
        assertNull(step.getRequest());
        assertNull(step.getResponse());
        assertNull(step.getLogEntry());
        assertSame(thinkTime, step.getStepRun());
    }

    @Test
    public void errorsAreCopied() {
        DebugStep step = new DebugStep(new ThinkTimeStep());
        assertTrue(step.getErrors().isEmpty());
        List<ErrorContainer> errors = new ArrayList<>();
        errors.add(new ErrorContainer("body", null, null, "missing"));

        step.setErrors(errors);
        errors.clear();

        assertEquals(1, step.getErrors().size());
        assertEquals("missing", step.getErrors().get(0).getReason());
    }
}
