package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.TestStep;

import java.util.ArrayList;
import java.util.List;

public class MockHDScriptUseCase extends HDScriptUseCase {
    private List<TestStep> scriptSteps = new ArrayList<>();

    public void addScriptSteps(TestStep step){ this.scriptSteps.add(step);}

    @Override
    public List<TestStep> getScriptSteps() {
        return scriptSteps;
    }
}
