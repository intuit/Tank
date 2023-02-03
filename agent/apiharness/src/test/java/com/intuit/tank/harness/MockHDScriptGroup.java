package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;

import java.util.ArrayList;
import java.util.List;

public class MockHDScriptGroup extends HDScriptGroup {
    private List<HDScript> groupSteps = new ArrayList<HDScript>();

    public void addGroupStep(HDScript script) {this.groupSteps.add(script);}

    @Override
    public List<HDScript> getGroupSteps() {
        return groupSteps;
    }
}
