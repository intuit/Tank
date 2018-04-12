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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.script.replace.ReplacementFactory;
import com.intuit.tank.script.replace.SearchMode;
import com.intuit.tank.search.script.CommonSection;
import com.intuit.tank.search.script.RequestStepSection;
import com.intuit.tank.search.script.SearchCriteria;
import com.intuit.tank.search.script.Section;
import com.intuit.tank.search.script.SleepTimeSection;
import com.intuit.tank.search.script.ThinkTimeSection;
import com.intuit.tank.search.script.VariableSection;
import com.intuit.tank.vm.common.util.MethodTimer;

@Named
@ConversationScoped
public class ScriptSearchBean implements Serializable {

    private static final Logger LOG = LogManager.getLogger(ScriptSearchBean.class);
    private static final long serialVersionUID = 1L;

    private SearchCriteria criteria = new SearchCriteria();

    private List<ScriptStep> searchMatch = new ArrayList<ScriptStep>();
    private String searchQuery;

    private List<SearchOptionWrapper> requestSections = new ArrayList<SearchOptionWrapper>();
    private List<SearchOptionWrapper> thinkTimeSections = new ArrayList<SearchOptionWrapper>();
    private List<SearchOptionWrapper> sleepTimeSections = new ArrayList<SearchOptionWrapper>();
    private List<SearchOptionWrapper> variableSections = new ArrayList<SearchOptionWrapper>();

    private List<ReplaceEntity> replaceEntity = new ArrayList<ReplaceEntity>();
    private int index = 0;
    // next X steps to be replaced
    private int nextX = 0;
    private ReplaceMode replaceMode = ReplaceMode.VALUE;

    private String replaceString = "";
    @Inject
    private Messages messages;
    @Inject
    private ScriptEditor editor;

    private boolean searching = false;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSearchMatchSize() {
        return searchMatch.size();
    }

    /**
     * @return the replaceString
     */
    public String getReplaceString() {
        return replaceString;
    }

    /**
     * @param replaceString
     *            the replaceString to set
     */
    public void setReplaceString(String replaceString) {
        this.replaceString = replaceString;
        updateReplaceEntity();
    }

    /**
     * @return the requestSections
     */
    public List<SearchOptionWrapper> getRequestSections() {
        return requestSections;
    }

    /**
     * @param requestSections
     *            the requestSections to set
     */
    public void setRequestSections(List<SearchOptionWrapper> requestSections) {
        this.requestSections = requestSections;
    }

    /**
     * @return the thinkTimeSections
     */
    public List<SearchOptionWrapper> getThinkTimeSections() {
        return thinkTimeSections;
    }

    /**
     * @param thinkTimeSections
     *            the thinkTimeSections to set
     */
    public void setThinkTimeSections(List<SearchOptionWrapper> thinkTimeSections) {
        this.thinkTimeSections = thinkTimeSections;
    }

    /**
     * @return the sleepTimeSections
     */
    public List<SearchOptionWrapper> getSleepTimeSections() {
        return sleepTimeSections;
    }

    /**
     * @param sleepTimeSections
     *            the sleepTimeSections to set
     */
    public void setSleepTimeSections(List<SearchOptionWrapper> sleepTimeSections) {
        this.sleepTimeSections = sleepTimeSections;
    }

    /**
     * @return the variableSections
     */
    public List<SearchOptionWrapper> getVariableSections() {
        return variableSections;
    }

    /**
     * @param variableSections
     *            the variableSections to set
     */
    public void setVariableSections(List<SearchOptionWrapper> variableSections) {
        this.variableSections = variableSections;
    }

    /**
     * @return the replaceEntity
     */
    public List<ReplaceEntity> getReplaceEntity() {
        return replaceEntity;
    }

    /**
     * @param replaceEntity
     *            the replaceEntity to set
     */
    public void setReplaceEntity(List<ReplaceEntity> replaceEntity) {
        this.replaceEntity = replaceEntity;
    }

    /**
     * @return the searchQuery
     */
    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * @param searchQuery
     *            the searchQuery to set
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public boolean getSearching() {
        return searching;
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public void toggleSearching() {
        if (searching) {
            resetVariables();
            searching = false;
        } else if (!searching) {
            buildOptions();
            searching = true;
        }
        editor.deselectAll();
    }

    public ReplaceMode getReplaceMode() {
        return replaceMode;
    }

    public void setReplaceMode(ReplaceMode replaceMode) {
        this.replaceMode = replaceMode;
    }

    public int getNextX() {
        return nextX;
    }

    public void setNextX(int nextX) {
        this.nextX = nextX;
    }

    private void buildOptions() {

        for (RequestStepSection requestSection : RequestStepSection.values()) {
            SearchOptionWrapper sow = new SearchOptionWrapper(requestSection);
            requestSections.add(sow);
        }
        for (ThinkTimeSection thinkTimeSection : ThinkTimeSection.values()) {
            SearchOptionWrapper sow = new SearchOptionWrapper(thinkTimeSection);
            thinkTimeSections.add(sow);
        }
        for (SleepTimeSection sleepTimeSection : SleepTimeSection.values()) {
            SearchOptionWrapper sow = new SearchOptionWrapper(sleepTimeSection);
            sleepTimeSections.add(sow);
        }
        for (VariableSection variableSection : VariableSection.values()) {
            SearchOptionWrapper sow = new SearchOptionWrapper(variableSection);
            variableSections.add(sow);
        }
    }

    private void resetVariables() {
        requestSections.clear();
        thinkTimeSections.clear();
        sleepTimeSections.clear();
        variableSections.clear();
        searchQuery = null;
        searchMatch.clear();
        criteria = new SearchCriteria();
    }

    public String search() {
        buildOptions();
        return "success";
    }

    public String cancel() {
        resetVariables();
        return "success";
    }

    public Script getScript() {
        return editor.getScript();
    }

    public List<ScriptStep> getSteps() {
        return editor.getSteps();
    }

    public void runSearch() {
        searchMatch = new ArrayList<ScriptStep>();
        Set<Section> newList = getSelectedSectionList();
        criteria.setScript(getScript());
        criteria.getCriteria().clear();
        criteria.getCriteria().addAll(newList);
        criteria.setSearchQuery(searchQuery);
        MethodTimer mt = new MethodTimer(LOG, ScriptSearchBean.class, "runSearch");
        ReplacementFactory rb = new ReplacementFactory();
        for (ScriptStep step : getSteps()) {
            for (Section section : criteria.getCriteria()) {
                List<ReplaceEntity> identifyReplacement = rb.getReplacementForSection(section)
                        .getReplacements(step, searchQuery, "", SearchMode.all);
                if (!identifyReplacement.isEmpty()) {
                    searchMatch.add(step);
                }
            }
        }
        mt.endAndLog();
        //
        // ScriptSearchService s3 = new ScriptSearchService();
        // Script script = getScript();
        // if (s3.search(script.getId()).size() == 0) {
        // s3.saveScript(script);
        // }
        // Set<Section> newList = getSelectedSectionList();
        // criteria.setScript(script);
        // criteria.getCriteria().clear();
        // criteria.getCriteria().addAll(newList);
        // criteria.setSearchQuery(searchQuery);
        // List<String> search = s3.search(script.getId(), searchQuery,
        // criteria);
        // filterSteps(search);
    }

    private Set<Section> getSelectedSectionList() {
        Set<Section> newList = new HashSet<Section>();

        List<Section> criteriaValues = new ArrayList<Section>();
        criteriaValues.addAll(getSelectedRequestStepSection());
        criteriaValues.addAll(getSelectedSleepTimeSection());
        criteriaValues.addAll(getSelectedThinkTimeSection());
        criteriaValues.addAll(getSelectedVariableSection());

        if (!criteriaValues.isEmpty()) {
            for (Section section : criteriaValues) {
                newList.add(section);
                if (section.equals(RequestStepSection.searchRequest)) {
                    newList.addAll(Arrays.asList(RequestStepSection.values()));
                }
            }
        } else {
            newList.add(CommonSection.search);
        }
        return newList;
    }

    private void filterSteps(List<String> search) {
        List<ScriptStep> steps = getSteps();
        for (String string : search) {
            for (ScriptStep scriptStep : steps) {
                if (scriptStep.getUuid().equals(string)) {
                    searchMatch.add(scriptStep);
                }
            }
        }
    }

    /**
     * @return the searchMatch
     */
    public List<ScriptStep> getSearchMatch() {
        return searchMatch;
    }

    /**
     * @return the selectedScriptStepSection
     */
    public List<Section> getSelectedRequestStepSection() {
        return requestSections.stream().filter(SearchOptionWrapper::isSelected).map(SearchOptionWrapper::getValue).collect(Collectors.toList());
    }

    /**
     * @return the selectedThinkTimeSection
     */
    public List<Section> getSelectedThinkTimeSection() {
        return thinkTimeSections.stream().filter(SearchOptionWrapper::isSelected).map(SearchOptionWrapper::getValue).collect(Collectors.toList());
    }

    /**
     * @return the selectedSleepTimeSection
     */
    public List<Section> getSelectedSleepTimeSection() {
        return sleepTimeSections.stream().filter(SearchOptionWrapper::isSelected).map(SearchOptionWrapper::getValue).collect(Collectors.toList());
    }

    /**
     * @return the selectedVariableSection
     */
    public List<Section> getSelectedVariableSection() {
        return variableSections.stream().filter(SearchOptionWrapper::isSelected).map(SearchOptionWrapper::getValue).collect(Collectors.toList());
    }

    public void initReplacementDialog() {
        replaceString = "";
        index = -1;
        next();
    }

    public void next() {
        index++;
        updateReplaceEntity();
    }

    public void previous() {
        if (index > 0) {
            index--;
        }
        updateReplaceEntity();
    }

    private void updateReplaceEntity() {
        if (searchMatch.size() == 0) {
            index = 0;
            messages.info("Nothing more to search for.");
            replaceEntity.clear();
            return;
        } else {
            index = index % searchMatch.size();
        }
        replaceEntity.clear();

        for (Section wrapper : criteria.getCriteria()) {
            replaceEntity.addAll(ReplacementFactory.getReplacementForSection(wrapper).getReplacements(
                    searchMatch.get(index), criteria.getSearchQuery(), replaceString, SearchMode.all));
        }
    }

    /**
     * replaces the next x steps in the script
     */
    public void replaceNextX() {
        int totalCount = index + nextX;
        if (totalCount >= searchMatch.size()) {
            totalCount = searchMatch.size();
        }

        for (int i = index; i < totalCount; i++) {
            replace();
        }
    }

    public void replaceAll() {
        for (int i = index; i < searchMatch.size(); i++) {
            replace();
        }
    }

    public void replace() {
        for (ReplaceEntity re : replaceEntity) {
            ScriptStep step = searchMatch.get(index);
            replaceInStep(step, re.getSection(), re.getKey());
        }
        next();
    }

    private void replaceInStep(ScriptStep step, Section section, String key) {
        ReplacementFactory.getReplacementForSection(section).replace(step, replaceString, key, replaceMode);
        ScriptUtil.updateStepLabel(step);
    }

    /**
     * removes step from the searchMatch list
     * 
     * @param step
     *            the step to be removed
     */
    public void removeFromSearchMatch(ScriptStep step) {
        searchMatch.remove(step);
    }

    /**
     * gets the current ScriptStep for the replace dialog
     * 
     * @return the current ScriptStep
     */
    public ScriptStep getCurrentScriptStep() {
        if (searchMatch.size() == 0) {
            return null;
        } else {
            return searchMatch.get(index);
        }
    }

    /**
     * @return the possible replace mode values
     */
    public ReplaceMode[] getReplaceModeList() {
        return ReplaceMode.values();
    }

}
