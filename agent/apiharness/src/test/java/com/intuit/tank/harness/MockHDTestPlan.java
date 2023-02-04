package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;

import java.util.ArrayList;
import java.util.List;

public class MockHDTestPlan extends HDTestPlan {
    private List<HDScriptGroup> group = new ArrayList<>();

    private HDTestVariables variables;


    public void addGroup(HDScriptGroup scriptGroup) {group.add(scriptGroup);}

    public void setVariables(HDTestVariables variables) {
        this.variables = variables;
    }

    @Override
    public List<HDScriptGroup> getGroup() {
        return group;
    }

    @Override
    public HDTestVariables getVariables() {
        return variables;
    }
}
