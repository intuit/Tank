package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;

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
}
