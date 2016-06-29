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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;

import com.intuit.tank.auth.Security;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;

@Named
@ConversationScoped
public class FilterGroupCreationBean extends SelectableBean<ScriptFilter> implements Serializable,
        Multiselectable<ScriptFilter> {

    private static final long serialVersionUID = 1L;
    private ScriptFilterGroup sfg;

    @Inject
    private Messages messages;
    
    @Inject
    private Conversation conversation;

    @Inject
    private Identity identity;
	
    @Inject 
    private IdentityManager identityManager;
    
    @Inject
    private Security security;

    private String saveAsName;

    private boolean editing;

    /**
     * @return the isEditing
     */
    public boolean isEditing() {
        return editing;
    }

    /**
     * @return the saveAsName
     */
    public String getName() {
        return getSfg().getName();
    }

    /**
     * @param saveAsName
     *            the saveAsName to set
     */
    public void setName(String name) {
        getSfg().setName(name);
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

    public void editFilterGroup(ScriptFilterGroup filterGroup) {
    	conversation.begin();
        editing = true;
        this.sfg = new ScriptFilterGroupDao().findById(filterGroup.getId());
        this.saveAsName = sfg.getName();
        HashMap<Integer, SelectableWrapper<ScriptFilter>> map = new HashMap<Integer, SelectableWrapper<ScriptFilter>>();
        for (SelectableWrapper<ScriptFilter> filterWrapper : getSelectionList()) {
            map.put(filterWrapper.getEntity().getId(), filterWrapper);
        }
        Set<ScriptFilter> filters = sfg.getFilters();
        for (ScriptFilter filter : filters) {
            map.get(filter.getId()).setSelected(true);
        }
        if (!canEditFilterGroup()) {
            messages.warn("You do not have permission to edit this filter group.");
        }
    }

    public void newFilterGroup() {
    	conversation.begin();
        editing = false;
        this.sfg = new ScriptFilterGroup();
        sfg.setCreator(identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName());
    }

    public void cancel() {
    	conversation.end();
    }

    public ScriptFilterGroup getSfg() {
        if (sfg == null) {
            sfg = new ScriptFilterGroup();
        }
        return sfg;
    }

    public void setSfg(ScriptFilterGroup sfg) {
        this.sfg = sfg;
    }

    @TsLoggedIn
    public void save() {
        sfg.getFilters().clear();
        for (SelectableWrapper<ScriptFilter> envelope : getSelectionList()) {
            if (envelope.isSelected()) {
                sfg.getFilters().add(envelope.getEntity());
            }
        }

        new ScriptFilterGroupDao().saveOrUpdate(sfg);
        messages.info(sfg.getName() + " has been saved.");
    }

    @TsLoggedIn
    public void saveAs() {
        if (StringUtils.isEmpty(saveAsName)) {
            messages.error("You must give the Filter Group a name.");
            return;
        }
        try {
            String originalName = sfg.getName();
            if (originalName.equals(saveAsName)) {
                save();
            } else {
                ScriptFilterGroup copied = new ScriptFilterGroup();
                copied.setCreator(identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName());
                copied.setName(saveAsName);
                copied.setProductName(sfg.getProductName());
                copied = new ScriptFilterGroupDao().saveOrUpdate(copied);
                for (SelectableWrapper<ScriptFilter> envelope : getSelectionList()) {
                    if (envelope.isSelected()) {
                        copied.addFilter(envelope.getEntity());
                    }
                }
                copied = new ScriptFilterGroupDao().saveOrUpdate(copied);
                messages.info("Filter Group " + originalName + " has been saved as " + copied.getName() + ".");
                editFilterGroup(copied);
            }
        } catch (Exception e) {
            messages.error(e.getMessage());
        }
    }

    @Override
    public List<ScriptFilter> getEntityList(ViewFilterType viewFilter) {
        return new ScriptFilterDao().findAll();
    }

    @Override
    public void delete(ScriptFilter entity) {

    }

    @Override
    public boolean isCurrent() {
        return true;
    }

    public boolean canCreateFilterGroup() {
        return security.hasRight(AccessRight.CREATE_FILTER);
    }

    public boolean canEditFilterGroup() {
        return security.hasRight(AccessRight.EDIT_FILTER)
                || security.isOwner(sfg);
    }
}
