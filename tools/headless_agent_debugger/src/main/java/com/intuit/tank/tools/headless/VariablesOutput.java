package com.intuit.tank.tools.headless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.intuit.tank.harness.data.HDTestPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VariablesOutput implements StepListener, ScriptChangedListener {

    private static final Logger LOG = LogManager.getLogger(VariablesOutput.class);

    @SuppressWarnings("unused")
    private HeadlessDebuggerSetup parent;
    private VarsTableModel initVarsModel = new VarsTableModel();
    private VarsTableModel afterVarsModel = new VarsTableModel();

    public VariablesOutput(HeadlessDebuggerSetup parent) {
        parent.addScriptChangedListener(this);
        parent.addStepChangedListener(this);
        this.parent = parent;

    }

    protected void displayVars() {
        LOG.info("******** VARIABLES *********");
        // initial variable values
        String sb = initVarsModel.keys.stream().map(key -> key + " = " + initVarsModel.values.get(key) + '\n').collect(Collectors.joining());
        LOG.info("Initial Variable Values");
        LOG.info(sb);

        // completed variable values
        sb = afterVarsModel.keys.stream().map(key -> key + " = " + afterVarsModel.values.get(key) + '\n').collect(Collectors.joining());
        LOG.info("Completed Variable Values");
        LOG.info(sb);
    }

    @Override
    public void stepChanged(DebugStep step) {
        stepEntered(step);
        stepExited(step);
    }

    @Override
    public void stepEntered(DebugStep step) {
        if (step != null) {
            initVarsModel.setVariables(step.getEntryVariables());
        } else {
            initVarsModel.setVariables(null);
        }

    }

    public void stepExited(DebugStep step) {
        if (step != null) {
            afterVarsModel.setVariables(step.getExitVariables());
        } else {
            afterVarsModel.setVariables(null);
        }
    }

    @Override
    public void scriptChanged(HDTestPlan plan) {
        initVarsModel.setVariables(null);
        afterVarsModel.setVariables(null);
    }

    private static class VarsTableModel {
        private Map<String, String> values = new HashMap<String, String>();
        private List<String> keys = new ArrayList<String>();

        private VarsTableModel() {
            keys = new ArrayList<String>();
        }

        private void setVariables(Map<String, String> map) {
            values.clear();
            keys.clear();
            if (map != null && !map.isEmpty()) {
                values.putAll(map);
                keys.addAll(values.keySet());
                Collections.sort(keys);
            }
        }
    }

}
