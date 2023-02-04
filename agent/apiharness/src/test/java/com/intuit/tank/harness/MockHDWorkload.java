package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDWorkload;

import java.util.ArrayList;
import java.util.List;

public class MockHDWorkload extends HDWorkload {
    private List<HDTestPlan> plans = new ArrayList<>();

    @Override
    public List<HDTestPlan> getPlans() {
        return plans;
    }

    public void addPlan(HDTestPlan testPlan) { this.plans.add(testPlan); }
}
