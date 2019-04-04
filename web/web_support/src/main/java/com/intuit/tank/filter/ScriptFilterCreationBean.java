package com.intuit.tank.filter;

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
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import com.intuit.tank.util.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;

import com.intuit.tank.auth.Security;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.settings.AccessRight;

@Named
@ConversationScoped
public class ScriptFilterCreationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ScriptFilter filter;
    private String name;
    private String productName;
    private ScriptFilterType creationMode = ScriptFilterType.INTERNAL;
    private Integer selectedExternalScript;
    private List<ExternalScript> externalScripts;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private Identity identity;
	
    @Inject 
    private IdentityManager identityManager;
    
    @Inject
    private Security security;

    @Inject
    private Messages messages;
    
    @Inject
    private Conversation conversation;

    private boolean conditionProcessed = false;
    private boolean allConditionsPass;
    private String saveAsName;
    private boolean editing;

    public ScriptFilterType[] getScriptFilterTypeList() {
        return ScriptFilterType.values();
    }

    /**
     * @return the isEditing
     */
    public boolean isEditing() {
        return editing;
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

    public boolean isConditionProcessed() {
        return conditionProcessed;
    }

    public void setConditionProcessed(boolean isConditionProcessed) {
        this.conditionProcessed = isConditionProcessed;
    }

    public ScriptFilter getFilter() {
        return filter;
    }

    public void setFilter(ScriptFilter filter) {
        this.filter = filter;
    }

    public void editFilter(ScriptFilter filter) {
    	conversation.begin();
        this.editing = true;
        this.filter = filter;
        this.setName(filter.getName());
        this.setSelectedExternalScript(filter.getExternalScriptId());
        this.setCreationMode(filter.getFilterType());
        this.productName = filter.getProductName();
        if (!canEditFilter()) {
            messages.warn("You do not have permission to edit this filter.");
        }
    }

    public void newFilter() {
    	conversation.begin();
        this.editing = false;
        this.filter = new ScriptFilter();
        filter.setCreator(identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName());
    }

    public void removeCondition(ScriptFilterCondition condition) {
        getFilter().getConditions().remove(condition);
    }

    public void removeAction(ScriptFilterAction action) {
        getFilter().getActions().remove(action);
    }

    @TsLoggedIn
    public String save() {
        ScriptFilterDao sfDao = new ScriptFilterDao();

        if (creationMode == ScriptFilterType.INTERNAL) {
            validate();
            if (messages.isEmpty()) {
                try {
                    filter.setProductName(productName);
                    filter.setName(name);
                    filter.setFilterType(creationMode);
                    sfDao.saveOrUpdate(filter);
                    conversation.end();
                    return "success";
                } catch (Exception e) {
                    exceptionHandler.handle(e);
                }
            }
        } else {
            if (messages.isEmpty()) {
                try {
                    filter.setProductName(productName);
                    filter.setName(name);
                    filter.setExternalScriptId(selectedExternalScript);
                    filter.setFilterType(creationMode);
                    sfDao.saveOrUpdate(filter);
                    conversation.end();
                    return "success";
                } catch (Exception e) {
                    exceptionHandler.handle(e);
                }
            }
        }
        return "fail";
    }

    @TsLoggedIn
    public void saveAs() {
        if (StringUtils.isEmpty(saveAsName)) {
            messages.error("You must give the Filter a name.");
            return;
        }
        try {
            String originalName = name;
            if (originalName.equals(saveAsName)) {
                save();
            } else {
                ScriptFilter copied = new ScriptFilter();
                copied.setCreator(identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName());
                copied.setName(saveAsName);
                copied.setProductName(productName);
                copied.setAllConditionsMustPass(allConditionsPass);
                copied.setPersist(filter.getPersist());
                copied.setFilterType(filter.getFilterType());
                copied.setExternalScriptId(filter.getExternalScriptId());
                for (ScriptFilterAction action : filter.getActions()) {
                    ScriptFilterAction ca = new ScriptFilterAction();
                    ca.setAction(action.getAction());
                    ca.setKey(action.getKey());
                    ca.setValue(action.getValue());
                    ca.setScope(action.getScope());
                    copied.addAction(ca);
                }
                for (ScriptFilterCondition condition : filter.getConditions()) {
                    ScriptFilterCondition cc = new ScriptFilterCondition();
                    cc.setCondition(condition.getCondition());
                    cc.setValue(condition.getValue());
                    cc.setScope(condition.getScope());
                    copied.addCondition(cc);
                }
                copied = new ScriptFilterDao().saveOrUpdate(copied);
                messages.info("Filter " + originalName + " has been saved as " + copied.getName() + ".");
                editFilter(copied);
            }
        } catch (Exception e) {
            messages.error(e.getMessage());
        }
    }

    public void cancel() {
    	conversation.end();
    }

    private void validate() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ScriptFilterType getCreationMode() {
        return creationMode;
    }

    public void setCreationMode(ScriptFilterType creationMode) {
        this.creationMode = creationMode;
    }

    public Integer getSelectedExternalScript() {
        return selectedExternalScript;
    }

    public void setSelectedExternalScript(Integer selectedExternalScript) {
        this.selectedExternalScript = selectedExternalScript;
    }

    public List<ExternalScript> getExternalScripts() {
        ExternalScriptDao externalScriptDao = new ExternalScriptDao();
        externalScripts = externalScriptDao.findAll();
        return externalScripts;
    }

    public void setExternalScripts(List<ExternalScript> externalScripts) {
        this.externalScripts = externalScripts;
    }

    public List<ScriptFilterCondition> getConditions() {
        return new ArrayList<ScriptFilterCondition>(filter.getConditions());
    }

    public List<ScriptFilterAction> getActions() {
        return new ArrayList<ScriptFilterAction>(filter.getActions());
    }

    /**
     * @return the allConditionsPass
     */
    public boolean isAllConditionsPass() {
        allConditionsPass = filter.getAllConditionsMustPass();
        return allConditionsPass;
    }

    /**
     * @param allConditionsPass
     *            the allConditionsPass to set
     */
    public void setAllConditionsPass(boolean allConditionsPass) {
        this.allConditionsPass = allConditionsPass;
        filter.setAllConditionsMustPass(allConditionsPass);
    }

    public boolean canCreateFilter() {
        return security.hasRight(AccessRight.CREATE_FILTER);
    }

    public boolean canEditFilter() {
        return security.hasRight(AccessRight.EDIT_FILTER)
                || security.isOwner(filter);
    }

}
