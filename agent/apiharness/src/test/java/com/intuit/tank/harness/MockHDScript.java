package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptUseCase;

import java.util.ArrayList;
import java.util.List;

public class MockHDScript extends HDScript {
    private List<HDScriptUseCase> useCase = new ArrayList<>();

    public void addUseCase(HDScriptUseCase scriptUseCase) { useCase.add(scriptUseCase);}

    @Override
    public List<HDScriptUseCase> getUseCase() {
        return useCase;
    }


}
