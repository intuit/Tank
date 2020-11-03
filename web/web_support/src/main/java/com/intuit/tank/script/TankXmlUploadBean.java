/**
 * 
 */
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

import java.io.InputStream;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.script.util.ScriptServiceUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.auth.Security;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.UploadedFileIterator;
import com.intuit.tank.wrapper.FileInputStreamWrapper;

@Named
@RequestScoped
public class TankXmlUploadBean implements Serializable {
    private static final Logger LOG = LogManager.getLogger(TankXmlUploadBean.class);

    private static final long serialVersionUID = 1L;

    private boolean useFlash = true;

    @Inject
    @Modified
    private Event<ModifiedScriptMessage> scriptEvent;

    @Inject
    private Identity identity;
    
    @Inject
    private IdentityManager identityManager;

    @Inject
    private Messages messages;

    @Inject
    private Security security;

    public TankXmlUploadBean() {
    }

    @TsLoggedIn
    public void handleFileUpload(FileUploadEvent event) throws Exception {
        UploadedFile item = event.getFile();

        if (item != null) {
            try {
                UploadedFileIterator uploadedFileIterator = new UploadedFileIterator(item, "xml");
                FileInputStreamWrapper w = uploadedFileIterator.getNext();
                while (w != null) {
                    processScript(w.getInputStream(), w.getFileName());
                    w = uploadedFileIterator.getNext();
                }
            } catch (Exception e) {
                LOG.error("Error extracting zip file: " + e.toString());
                messages.error("Error extracting zip file: " + e.toString());
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(item.getInputStream());
            }
        }
    }

    public void clearDialog() {
        messages.clear();
    }

    private void processScript(InputStream inputStream, String fileName) {
        ScriptDao dao = new ScriptDao();

        ScriptTO scriptTo = ScriptServiceUtil.parseXMLtoScriptTO(inputStream);
        Script script = ScriptServiceUtil.transferObjectToScript(scriptTo);
        if (script.getId() > 0) {
            Script existing = dao.findById(script.getId());
            if (existing == null) {
                LOG.error("Error updating script: Script passed with unknown id.");
                messages.error("Script " + fileName + " passed with unknown id.");
                return;
            }
            if (!existing.getName().equals(script.getName())) {
                LOG.error("Error updating script: Cannot change the name of an existing Script.");
                messages.error("Cannot change the name of an existing script.");
                return;
            }
            if (!security.isAdmin() && !security.isOwner(script)) {
                LOG.error("Error updating script: Cannot change the name of an existing Script.");
                messages.error("You do not have rights to modify " + script.getName() + ".");
                return;
            }
            script.setSerializedScriptStepId(existing.getSerializedScriptStepId());
        } else {
            script.setCreator(identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName());
        }
        script = dao.saveOrUpdate(script);
        LOG.info("Script " + script.getName() + " from file " + fileName + " has been added.");
        messages.info("Script " + script.getName() + " from file " + fileName + " has been added.");
        scriptEvent.fire(new ModifiedScriptMessage(script, this));
    }

    public boolean isUseFlash() {
        return useFlash;
    }

    public void setUseFlash(boolean useFlash) {
        this.useFlash = useFlash;
    }

}