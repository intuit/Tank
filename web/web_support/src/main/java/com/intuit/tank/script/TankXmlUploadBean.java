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

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.api.script.util.ScriptServiceUtil;
import com.intuit.tank.auth.Security;
import com.intuit.tank.config.TsLoggedIn;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.UploadedFileIterator;
import com.intuit.tank.wrapper.FileInputStreamWrapper;

@Named
public class TankXmlUploadBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(TankXmlUploadBean.class);

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
                StringBuilder sb = new StringBuilder();
                FileInputStreamWrapper w = uploadedFileIterator.getNext();
                while (w != null) {
                    processScript(w.getInputStream(), w.getFileName());
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(w.getFileName());
                    w = uploadedFileIterator.getNext();

                }
            } catch (Exception e) {
                LOG.error("Error extracting zip file: " + e.toString());
                messages.error("Error extracting zip file: " + e.toString());
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(item.getInputstream());
            }
        }
    }

    public void clearDialog() {
        messages.clear();
    }

    public void processScript(InputStream inputStream, String fileName) throws Exception {
        try {
            ScriptDao dao = new ScriptDao();
            JAXBContext ctx = JAXBContext.newInstance(ScriptTO.class.getPackage().getName());
            ScriptTO scriptTo = (ScriptTO) ctx.createUnmarshaller().unmarshal(inputStream);
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
            } else {
                script.setCreator(identityManager.lookupById(User.class, identity.getAccount().getId()).getLoginName());
            }
            script = dao.saveOrUpdate(script);
            messages.info("Script " + script.getName() + " from file " + fileName + " has been added.");
            scriptEvent.fire(new ModifiedScriptMessage(script, this));
        } catch (Exception e) {
            LOG.error("Error unmarshalling script: " + e.getMessage() + " from file " + fileName, e);
            messages.error("Error unmarshalling script: " + e.toString() + " from file " + fileName);
        }
    }

    public boolean isUseFlash() {
        return useFlash;
    }

    public void setUseFlash(boolean useFlash) {
        this.useFlash = useFlash;
    }

}