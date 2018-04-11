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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;

import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.PreferencesBean;
import com.intuit.tank.auth.Security;
import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.project.Script;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.util.Messages;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;
import com.intuit.tank.wrapper.VersionContainer;

@Named
@ViewScoped
public class ScriptBean extends SelectableBean<Script> implements Serializable, Multiselectable<Script> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(ScriptBean.class);

    @Inject
    private ScriptLoader scriptLoader;

    @Inject
    private Identity identity;
    
    @Inject
    private IdentityManager identityManager;

    @Inject
    private Security security;

    @Inject
    private Messages messages;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    @Modified
    private Event<ModifiedScriptMessage> scriptEvent;

    private SelectableWrapper<Script> selectedScript;
    
    private int version;

    @Inject
    private PreferencesBean userPrefs;

    private TablePreferences stepTablePrefs;

    private String saveAsName;

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

    @PostConstruct
    public void init() {
        tablePrefs = new TablePreferences(userPrefs.getPreferences().getScriptsTableColumns());
        stepTablePrefs = new TablePreferences(userPrefs.getPreferences().getScriptStepTableColumns());

        tablePrefs.registerListener(userPrefs);
    }

    /**
     * @return the stepTablePrefs
     */
    public TablePreferences getStepTablePrefs() {
        return stepTablePrefs;
    }

    /**
     * @param stepTablePrefs
     *            the selectedScript to set
     */
    public void setStepTablePrefs(TablePreferences stepTablePrefs) {
        this.stepTablePrefs = stepTablePrefs;
    }

    /**
     * @return the selectedScript
     */
    public SelectableWrapper<Script> getSelectedScript() {
        return selectedScript;
    }

    /**
     * @param selectedScript
     *            the selectedScript to set
     */
    public void setSelectedScript(SelectableWrapper<Script> selectedScript) {
        this.selectedScript = selectedScript;
        this.saveAsName = selectedScript.getEntity().getName();
    }

    /**
     * 
     * @inheritDoc
     */
    public void delete(Script script) {
        if (!security.hasRight(AccessRight.DELETE_SCRIPT) && !security.isOwner(script)) {
            messages.warn("You do not have permission to delete this script.");
        } else {
            try {
                new ScriptDao().delete(script.getId());
                scriptEvent.fire(new ModifiedScriptMessage(script, this));
            } catch (Exception e) {
                LOG.error("Error deleting Script: " + e.toString());
                exceptionHandler.handle(e);
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Script> getEntityList(ViewFilterType viewFilter) {
        VersionContainer<Script> container = scriptLoader.getVersionContainer(viewFilter);
        this.version = container.getVersion();
        return container.getEntities();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isCurrent() {
        return scriptLoader.isCurrent(version);
    }

    /**
     * @return the creatorList
     */
    public SelectItem[] getCreatorList() {
        return scriptLoader.getCreatorList();
    }

    public void saveAs(Script script) {
        if (StringUtils.isEmpty(saveAsName)) {
            messages.error("You must give the script a name.");
            return;
        }
        try {
            String originalName = script.getName();
            if (originalName.equals(saveAsName)) {
                messages.error("You did not change the script name.");
                return;
            } else {
                Script copyScript = ScriptUtil.copyScript(
                		identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName(),
                		saveAsName, script);
                copyScript = new ScriptDao().saveOrUpdate(copyScript);
                scriptEvent.fire(new ModifiedScriptMessage(copyScript, this));
                messages.info("Script " + originalName + " has been saved as "
                        + copyScript.getName() + ".");
            }
        } catch (Exception e) {
            messages.error(e.getMessage());
        }
    }

}
