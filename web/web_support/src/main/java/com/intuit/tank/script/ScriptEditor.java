package com.intuit.tank.script;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import com.intuit.tank.util.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.PreferencesBean;
import com.intuit.tank.auth.Security;
import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.converter.ScriptStepHolder;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.filter.FilterBean;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.project.JobDetailFormatter;
import com.intuit.tank.project.JobValidator;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.util.ScriptFilterUtil;
import com.intuit.tank.vm.settings.AccessRight;

@Named
@ConversationScoped
public class ScriptEditor implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger LOG = LogManager.getLogger(ScriptEditor.class);

    private Script script;

    private String variableKey;
    private String variableValue;

    private String minThinkTime;
    private String maxThinkTime;

    private String jobDetails;
    private String sleepTime;

    private List<String> gotoGroupList = new ArrayList<String>();

    @Inject
    private VariableEditor variableEditor;
    @Inject
    private ClearCookiesEditor clearCookiesEditor;
    @Inject
    private CookieStepEditor cookieStepEditor;
    @Inject
    private AuthenticationEditor authenticationEditor;
    @Inject
    private ThinkTimeEditor thinkTimeEditor;
    @Inject
    private SleepTimeEditor sleepTimeEditor;
    @Inject
    private ScriptRequestEditor requestEditor;
    @Inject
    private ScriptSearchBean searchBean;
    @Inject
    private FilterBean filterBean;
    @Inject
    private AggregatorEditor aggregatorEditor;

    @Inject
    private LogicStepEditor logicStepEditor;

    @Inject
    private Identity identity;
    
    @Inject
    private IdentityManager identityManager;
    
    @Inject
    private Security security;
    
    @Inject
    private Conversation conversation;

    @Inject
    @Modified
    private Event<ModifiedScriptMessage> scriptEvent;

    @Inject
    private CopyBuffer copyBuffer;

    @Inject
    private Messages messages;

    private int currentPage = 0;
    private int numRowsVisible = 20;
    private String saveAsName;

    private List<ScriptStep> selectedSteps = new ArrayList<ScriptStep>();

    private ScriptStep[] selectedStepsArr;
    private List<ScriptStep> steps;
    private List<ScriptStepHolder> orderList;

    private ScriptStep currentStep;

    @Inject
    private PreferencesBean userPrefs;
    protected TablePreferences tablePrefs;
    protected TableViewState tableState = new TableViewState();

    @PostConstruct
    public void init() {
        tablePrefs = new TablePreferences(userPrefs.getPreferences().getScriptStepTableColumns());
        tablePrefs.registerListener(userPrefs);
    }

    /**
     * @return the tablePrefs
     */
    public TablePreferences getTablePrefs() {
        return tablePrefs;
    }

    /**
     * @return the tableState
     */
    public TableViewState getTableState() {
        return tableState;
    }

    public List<ScriptStepHolder> getOrderList() {
        if (orderList == null) {
            orderList = new ArrayList<ScriptStepHolder>(steps.size());
            for (int i = 0; i < steps.size(); i++) {
                ScriptStep step = steps.get(i);
                orderList.add(new ScriptStepHolder(i + 1, step.getUuid(), step
                        .getLabel()));
            }
        }
        return orderList;
    }

    public void clearOrderList() {
        orderList = null;
    }

    public void createScriptDetails() {
        JobValidator validator = new JobValidator(script);
        jobDetails = JobDetailFormatter.createJobDetails(validator, script.getName());
    }

    public String getScriptDetails() {
        return jobDetails;
    }

    /**
     * @return the saveAsName
     */
    public String getSaveAsName() {
        return saveAsName;
    }

    /**
     * @param saveAsName
     *            the saveAsName to set
     */
    public void setSaveAsName(String saveAsName) {
        this.saveAsName = saveAsName;
    }

    public void setOrderList(List<ScriptStepHolder> list) {
        Map<String, ScriptStep> map = new HashMap<String, ScriptStep>();
        for (ScriptStep step : steps) {
            map.put(step.getUuid(), step);
        }
        steps.clear();
        for (ScriptStepHolder h : list) {
            steps.add(map.get(h.getUuid()));
        }
        clearOrderList();
    }

    public String getPrettyString(String s, String mimetype) {
        if (StringUtils.isNotBlank(s)) {
            if (StringUtils.containsIgnoreCase(mimetype, "json")) {
                try {
                    JsonParser parser = new JsonParser();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonElement el = parser.parse(s);
                    s = gson.toJson(el); // done
                } catch (JsonSyntaxException e) {
                    LOG.warn("Cannot format json string: " + e);
                }
            } else if (StringUtils.containsIgnoreCase(mimetype, "xml")) {
                try {
                    StringWriter sw;
                    final OutputFormat format = OutputFormat.createPrettyPrint();
                    final org.dom4j.Document document = DocumentHelper.parseText(s);
                    sw = new StringWriter();
                    final XMLWriter writer = new XMLWriter(sw, format);
                    writer.write(document);
                    s = sw.toString();
                } catch (Exception e) {
                    LOG.warn("Cannot format xml string: " + e);
                }
            }
            s = s.trim();
        }
        return s;
    }

    /**
     * @return the currentStep
     */
    public ScriptStep getCurrentStep() {
        return currentStep;
    }

    /**
     * @param currentStep
     *            the currentStep to set
     */
    public void setCurrentStep(ScriptStep currentStep) {
        this.currentStep = currentStep;
    }

    /**
     * Sets the current editing script to the passed in parameter script.
     * 
     * @param script
     */
    public String editScript(Script s) {
    	conversation.begin();
        this.script = new ScriptDao().findById(s.getId());
        ScriptUtil.setScriptStepLabels(script);
        steps = script.getScriptSteps();
        saveAsName = script.getName();
        if (!canEditScript()) {
            messages.warn("You do not have permission to edit this script.");
        }
        return "success";
    }

    public String cancel() {
    	conversation.end();
        this.script = null;
        return "success";
    }

    public void enterFilterMode() {
    }

    /**
     * applies all selected filters to the script currently being edited
     * 
     * @return
     */
    public String reapplyFilters() {
        List<Integer> selectedFilterIds = filterBean.getSelectedFilterIds();
        try {
            ScriptFilterUtil.applyFilters(selectedFilterIds, script);
            ScriptUtil.setScriptStepLabels(script);
            messages.info("Applied " + selectedFilterIds.size()
                    + " filter(s) to \"" + script.getName() + "\".");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        messages.error("Error applying filters to \"" + script.getName()
                + "\".");
        return "failure";
    }

    public void selectAll() {
        this.selectedSteps.clear();
        selectedSteps.addAll(steps);
        setSelectedStepsArr(steps.toArray(new ScriptStep[0]));
    }

    public void deselectAll() {
        this.selectedSteps.clear();
        setSelectedStepsArr(new ScriptStep[0]);
    }

    /**
     * @return the numRowsVisible
     */
    public int getNumRowsVisible() {
        return numRowsVisible;
    }

    /**
     * @param numRowsVisible
     *            the numRowsVisible to set
     */
    public void setNumRowsVisible(int numRowsVisible) {
        this.numRowsVisible = numRowsVisible;
    }

    /**
     * @return the gotoGroupList
     */
    public List<String> getGotoGroupList() {
        return gotoGroupList;
    }

    public List<String> complete(String query) {
        List<String> suggestions = null;
        if (StringUtils.isEmpty(query)) {
            suggestions = gotoGroupList;
        } else {
            suggestions = new ArrayList<String>();
            for (String s : gotoGroupList) {
                if (s.startsWith(query)) {
                    suggestions.add(s);
                }
            }
        }
        return suggestions;
    }

    public void populateGroupList() {
        gotoGroupList.clear();
        for (ScriptStep step : script.getScriptSteps()) {
            if ((!StringUtils.isEmpty(step.getScriptGroupName()))
                    && (!gotoGroupList.contains(step.getScriptGroupName()))) {
                gotoGroupList.add(step.getScriptGroupName());
            }
        }
        // selectedSteps.clear();
    }

    /**
     * @return the selectedSteps
     */
    public List<ScriptStep> getSelectedSteps() {
        return selectedSteps;
    }

    /**
     * @param selectedSteps
     *            the selectedSteps to set
     */
    public void setSelectedSteps(List<ScriptStep> selectedSteps) {
        this.selectedSteps = selectedSteps;
    }

    /**
     * @return the script
     */
    public Script getScript() {
        return script;
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScript(Script script) {
        this.script = script;
        this.saveAsName = script.getName();
    }

    /**
     * @return the script
     */
    public String getScriptName() {
        return script.getName();
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScriptName(String name) {
        this.script.setName(name);
    }

    /**
     * @return the variableKey
     */
    public String getVariableKey() {
        return variableKey;
    }

    /**
     * @param variableKey
     *            the variableKey to set
     */
    public void setVariableKey(String variableKey) {
        this.variableKey = variableKey;
    }

    /**
     * @return the variableValue
     */
    public String getVariableValue() {
        return variableValue;
    }

    /**
     * @param variableValue
     *            the variableValue to set
     */
    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    /**
     * @return the minThinkTime
     */
    public String getMinThinkTime() {
        return minThinkTime;
    }

    /**
     * @param minThinkTime
     *            the minThinkTime to set
     */
    public void setMinThinkTime(String minThinkTime) {
        this.minThinkTime = minThinkTime;
    }

    /**
     * @return the maxThinkTime
     */
    public String getMaxThinkTime() {
        return maxThinkTime;
    }

    /**
     * @param maxThinkTime
     *            the maxThinkTime to set
     */
    public void setMaxThinkTime(String maxThinkTime) {
        this.maxThinkTime = maxThinkTime;
    }

    /**
     * @return the sleepTime
     */
    public String getSleepTime() {
        return sleepTime;
    }

    /**
     * @param sleepTime
     *            the sleepTime to set
     */
    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * Saves the script to persistent storage
     */
    public void save() {
        new ScriptDao().saveOrUpdate(script);
        // ScriptSearchService s3 = new ScriptSearchService();
        // s3.saveScript(script);
        scriptEvent.fire(new ModifiedScriptMessage(script, this));
        messages.info("Script " + script.getName() + " has been saved.");
    }

    public void saveAs() {
        if (StringUtils.isEmpty(saveAsName)) {
            messages.error("You must give the script a name.");
            return;
        }
        try {
            String originalName = script.getName();
            if (originalName.equals(saveAsName)) {
                save();
            } else {
                Script copyScript = ScriptUtil.copyScript(
                		identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName()
                		, saveAsName, script);
                copyScript = new ScriptDao().saveOrUpdate(copyScript);
                scriptEvent.fire(new ModifiedScriptMessage(copyScript, this));
                messages.info("Script " + originalName + " has been saved as "
                        + copyScript.getName() + ".");
                editScript(copyScript);
            }
        } catch (Exception e) {
            messages.error(e.getMessage());
        }
    }

    /**
     * Does something to insert think time into the script.
     */
    public void insertThinkTime() {
        ScriptStep thinkTimeScriptStep = ScriptStepFactory.createThinkTime(
                getMinThinkTime(), getMaxThinkTime());
        steps.add(getInsertIndex(), thinkTimeScriptStep);
        minThinkTime = "";
        maxThinkTime = "";
        reindexScriptSteps();
    }

    /**
     * Does something to insert a sleep time into the script.
     */
    public void insertSleepTime() {
        ScriptStep sleepTimeScriptStep = ScriptStepFactory
                .createSleepTime(getSleepTime());
        steps.add(getInsertIndex(), sleepTimeScriptStep);
        sleepTime = "";
        reindexScriptSteps();
    }

    /**
     * Does something to insert a variable request into the script.
     */
    public void insertVariable() {
        ScriptStep variableStep = ScriptStepFactory.createVariable(
                getVariableKey(), getVariableValue());
        steps.add(getInsertIndex(), variableStep);
        variableKey = "";
        variableValue = "";
        reindexScriptSteps();
    }

    /**
     * Deletes a request from the script.
     * 
     * @param request
     */
    public void deleteRequest(ScriptStep step) {
        doDelete(step);
        messages.info("Script Step " + step.getLabel() + " has been deleted.");
    }

    /**
     * Deletes a request from the script.
     * 
     * @param request
     */
    private void doDelete(ScriptStep step) {
        if (aggregatorEditor.isAggregator(step)) {
            ScriptStep stepPair = aggregatorEditor.getAggregatorPair(step);
            steps.remove(stepPair);
            searchBean.removeFromSearchMatch(stepPair);
        }
        steps.remove(step);
        searchBean.removeFromSearchMatch(step);
        reindexScriptSteps();
    }

    /**
     * 
     * @return
     */
    public List<ScriptStep> getSteps() {
        return steps;
    }

    /**
     * @param steps
     *            the steps to set
     */
    public void setSteps(List<ScriptStep> steps) {
        this.steps = steps;
    }

    /**
     * 
     * @param scriptStep
     */
    public boolean isVariable(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.VARIABLE);
    }

    /**
     * 
     * @param scriptStep
     */
    public boolean isClearVariable(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.CLEAR);
    }

    /**
     * 
     * @param scriptStep
     */
    public boolean isThinkTime(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.THINK_TIME);
    }

    /**
     * 
     * @param scriptStep
     */
    public boolean isLogicStep(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.LOGIC);
    }

    /**
     * 
     * @param scriptStep
     */
    public boolean isCookieStep(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.COOKIE);
    }
    /**
     * 
     * @param scriptStep
     */
    public boolean isAuthStep(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.AUTHENTICATION);
    }

    /**
     * 
     * @param scriptStep
     */
    public boolean isSleepTime(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.SLEEP);
    }

    /**
     * 
     * @param scriptStep
     */
    public boolean isRequest(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.REQUEST);
    }

    /**
     * @param scriptStep
     * @return
     */
    public boolean isAggregator(ScriptStep scriptStep) {
        return scriptStep.getType().equals(ScriptConstants.TIMER);
    }

    /**
     * 
     * @param scriptStep
     */
    public void editVariable(ScriptStep scriptStep) {
        variableEditor.editVariable(scriptStep);
        clearOrderList();
    }

    /**
     * 
     * @param scriptStep
     */
    public void editThinkTime(ScriptStep scriptStep) {
        thinkTimeEditor.editThinkTime(scriptStep);
        clearOrderList();
    }

    /**
     * 
     * @param scriptStep
     */
    public void editCookieStep(ScriptStep scriptStep) {
        cookieStepEditor.editCookieStep(scriptStep);
        clearOrderList();
    }
    /**
     * 
     * @param scriptStep
     */
    public void editAuthStep(ScriptStep scriptStep) {
        authenticationEditor.editAuthentication(scriptStep);
        clearOrderList();
    }

    /**
     * 
     * @param scriptStep
     */
    public void editSleepTime(ScriptStep scriptStep) {
        sleepTimeEditor.editSleepTime(scriptStep);
        clearOrderList();
    }

    /**
     * 
     * @param scriptStep
     */
    public void editLogicStep(ScriptStep scriptStep) {
        logicStepEditor.editLogicStep(scriptStep);
        clearOrderList();
    }

    public void editAggregator(ScriptStep step) {
        aggregatorEditor.editAggregator(step);
        clearOrderList();
    }

    public void addTimerGroup() {
        aggregatorEditor.insertAggregator();
        clearOrderList();
    }

    /**
     * 
     * @param scriptStep
     */
    public void editRequest(ScriptStep scriptStep) {
        requestEditor.editRequest(scriptStep);
        clearOrderList();
    }

    public void paste() {
        int insertIndex = getInsertIndex();
        for (ScriptStep step : copyBuffer.getBuffer()) {
            ScriptStep copy = ScriptUtil.copyScriptStep(step);
            this.steps.add(insertIndex, copy);
            insertIndex++;
        }
        clearOrderList();
        reIndexAndCheck();
    }

    public void insert(ScriptStep step) {
        ScriptUtil.updateStepLabel(step);
        this.steps.add(getInsertIndex(), step);
        clearOrderList();
        reindexScriptSteps();
    }

    private int getInsertIndex() {
        int ret = steps.size();
        if (!selectedSteps.isEmpty()) {
            for (ScriptStep s : selectedSteps) {
                ret = Math.min(ret, steps.indexOf(s));
            }
        }
        return ret;
    }

    public void insert(ScriptStep step, int index) {
        ScriptUtil.updateStepLabel(step);
        steps.add(index, step);
        clearOrderList();
        reindexScriptSteps();
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage
     *            the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return the selectedStepsArr
     */
    public ScriptStep[] getSelectedStepsArr() {
        return selectedStepsArr;
    }

    public void deleteSelected() {
        for (ScriptStep step : selectedSteps) {
            doDelete(step);
        }
        messages.info("Deleted " + selectedSteps.size()
                + " selected Script Steps.");
        selectedSteps.clear();
        setSelectedStepsArr(new ScriptStep[0]);
    }

    public void copySelected() {
        copyBuffer.clear();
        copyBuffer.getBuffer().addAll(selectedSteps);
    }

    public boolean isCopyEnabled() {
        return !selectedSteps.isEmpty();
    }

    public boolean isDeleteEnabled() {
        return !selectedSteps.isEmpty();
    }

    /**
     * @param selectedStepsArr
     *            the selectedStepsArr to set
     */
    public void setSelectedStepsArr(ScriptStep[] selectedStepsArr) {
        this.selectedStepsArr = selectedStepsArr;
        if (selectedStepsArr != null) {
            selectedSteps.clear();
            selectedSteps.addAll(Arrays.asList(selectedStepsArr));
        }
    }

    /**
     * Reindexes the script steps.
     */
    public void reindexScriptSteps() {
        int index = 1;
        for (ScriptStep step : steps) {
            step.setStepIndex(index);
            index++;
        }
    }

    public void reIndexAndCheck() {
        reindexScriptSteps();
        for (ScriptStep step : steps) {
            if (isAggregator(step)) {
                aggregatorEditor.checkFixOrder(step);
            }
        }
        Collections.sort(steps);
    }

    public boolean canEditScript() {
        return security.hasRight(AccessRight.EDIT_SCRIPT)
                || security.isOwner(script);
    }

    public ScriptStep getPreviousRequest(ScriptStep step) {
        ScriptStep ret = null;
        int index = getInsertIndex();
        if (step != null) {
            index = step.getStepIndex();
        }
        for (int i = index; --i >= 0;) {
            ScriptStep candidate = getSteps().get(i);
            if (ScriptConstants.REQUEST.equals(candidate.getType())) {
                ret = candidate;
                break;
            }
        }
        return ret;
    }

}
