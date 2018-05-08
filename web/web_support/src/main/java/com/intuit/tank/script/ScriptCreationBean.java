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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;
import org.primefaces.model.UploadedFile;

import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.auth.Security;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.filter.FilterBean;
import com.intuit.tank.filter.FilterGroupBean;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.script.processor.ScriptProcessor;
import com.intuit.tank.util.UploadedFileIterator;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.exception.WatsParseException;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.FileInputStreamWrapper;
import com.intuit.tank.wrapper.SelectableWrapper;

@Named
@ConversationScoped
public class ScriptCreationBean implements Serializable {

    private static final Logger LOG = LogManager.getLogger(ScriptCreationBean.class);

    private static final long serialVersionUID = 1L;
    private String name;
    private String productName;
    private String creationMode = "Upload Script";
    private List<SelectableWrapper<ScriptFilterGroup>> groupWrappers;
    private List<SelectableWrapper<ScriptFilter>> filterWrappers;
    private UploadedFile file;

    @Inject
    private ScriptProcessor scriptProcessor;

    @Inject
    private Identity identity;
    
    @Inject
    private IdentityManager identityManager;
    
    @Inject
    private Security security;

    @Inject
    private FilterBean filterBean;
    @Inject
    private FilterGroupBean filterGroupBean;

    @Inject
    private Messages messages;
    
    @Inject
    private Conversation conversation;

    @Inject
    @Modified
    private Event<ModifiedScriptMessage> scriptEvent;

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file
     *            the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * @return the creationMode
     */
    public String getCreationMode() {
        return creationMode;
    }

    /**
     * @param creationMode
     *            the creationMode to set
     */
    public void setCreationMode(String creationMode) {
        this.creationMode = creationMode;
    }

    /**
     * @return the groupWrappers
     */
    public List<SelectableWrapper<ScriptFilterGroup>> getGroupWrappers() {
        if (groupWrappers == null) {
            groupWrappers = filterGroupBean.getSelectionList();
        }
        return groupWrappers;
    }

    public void cancel() {
    	conversation.end();
    }

    /**
     * @param groupWrappers
     *            the groupWrappers to set
     */
    public void setGroupWrappers(List<SelectableWrapper<ScriptFilterGroup>> groupWrappers) {
        this.groupWrappers = groupWrappers;
    }

    /**
     * @return the filterWrappers
     */
    public List<SelectableWrapper<ScriptFilter>> getFilterWrappers() {
        if (filterWrappers == null) {
            filterWrappers = filterBean.getSelectionList();
        }
        return filterWrappers;
    }

    /**
     * @param filterWrappers
     *            the filterWrappers to set
     */
    public void setFilterWrappers(List<SelectableWrapper<ScriptFilter>> filterWrappers) {
        this.filterWrappers = filterWrappers;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Initializes variables for starting the process of creating a new script.
     * 
     * @return
     */
    public String createNewScript() {
    	conversation.begin();
        return "success";
    }

    /**
     * Saves the script in the database.
     */
    @TsLoggedIn
    public String save() {
        String retVal = null;
        if (validate()) {
            UploadedFile item = getFile();
            try {
                Script script = new Script();
                script.setName(getName());
                script.setCreator(identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName());
                script.setProductName(productName);
                if (getCreationMode().equals("Upload Script")) {
                    UploadedFileIterator uploadedFileIterator = new UploadedFileIterator(item, "xml");
                    FileInputStreamWrapper w = uploadedFileIterator.getNext();
                    if (w == null) {
                        messages.error("Zip archive did not contain any xml files.");
                        return null;
                    }
                    scriptProcessor.setScript(script);
                    List<ScriptStep> steps = parseScript(new InputStreamReader(w.getInputStream()),
                            getSelectedFilters());
                    // iterate through the other files if needed
                    while ((w = uploadedFileIterator.getNext()) != null) {
                        messages.error("Uploaded zip file contained more than one xml file. Script creation only supports one file upload.");
                        return null;
                    }
                    if (steps != null) {
                        setScriptSteps(script, steps);
                    }
                }
                new ScriptDao().saveOrUpdate(script);
                scriptEvent.fire(new ModifiedScriptMessage(script, null));
                retVal = "success";
                conversation.end();
            } catch (Exception e) {
            	LOG.error("Failed to create Script " + e, e);
                messages.error(e.getMessage());
            }
        }
        return retVal;
    }

    private boolean validate() {
        if (StringUtils.isEmpty(name)) {
            messages.error("Script name cannot be empty.  Please enter a script name.");
        }
        if (getCreationMode().equalsIgnoreCase("upload script")) {
            if (getFile() == null || getFile().getContents().length == 0) {
                messages.error("Please select a script file.");
            }
        }
        return messages.isEmpty();
    }

    /**
     * @return List of selected script filters
     */
    private List<ScriptFilter> getSelectedFilters() {
        return getFilterWrappers().stream().filter(SelectableWrapper::isSelected).map(SelectableWrapper::getEntity).collect(Collectors.toList());
    }

    /**
     * Updates the filter wrappers for a given filterGroup selection
     */
    public void updateFilters(SelectableWrapper<ScriptFilterGroup> filterGroupWrapper) {
        boolean flag = !filterGroupWrapper.isSelected();
        for (ScriptFilter filter : filterGroupWrapper.getEntity().getFilters()) {
            updateFilterWrapperForGroup(filter, flag);
        }
    }

    /**
     * Helper function to set the selected flag for the given filter.
     * 
     * @param filter
     * @param flag
     */
    private void updateFilterWrapperForGroup(ScriptFilter filter, boolean flag) {
        getFilterWrappers().stream().filter(filterWrapper -> filterWrapper.getEntity().getId() == filter.getId()).findFirst().ifPresent(filterWrapper -> filterWrapper.setSelected(flag));
    }

    private List<ScriptStep> parseScript(Reader reader, List<ScriptFilter> filters) throws WatsParseException {
        MethodTimer timer = new MethodTimer(LOG, getClass(), "parseScript");
        timer.start();
        long st = System.currentTimeMillis();
        List<ScriptStep> steps = scriptProcessor.parseScript(reader, filters);
        long end = System.currentTimeMillis();
        LOG.debug("parsing xml:" + (end - st));

        timer.markAndLog("Parse Script with " + steps.size() + " steps");
        return steps;
    }

    private void setScriptSteps(Script script, List<ScriptStep> steps) {
        LOG.debug("script " + script.getName() + " has " + steps.size() + " steps");
        /*
         * int count = 1; List<ScriptStep> newSteps = new ArrayList<ScriptStep>(); for (ScriptStep e : steps) {
         * ScriptStep step = ScriptUtil.requestToStep(count, e); newSteps.add(step); count++; }
         */
        steps = new ArrayList<ScriptStep>(steps);
        script.getScriptSteps().clear();
        script.getScriptSteps().addAll(steps);
    }

    public boolean canCreateScript() {
        return security.hasRight(AccessRight.CREATE_SCRIPT);
    }

}
