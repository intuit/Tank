package com.intuit.tank.util;

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

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * 
 * Messages. Provides a drop in replacement for seam international status messages.
 * 
 * @author Kevin McGoldrick
 * 
 */
@Named
@ApplicationScoped
public class Messages {
    public void info(String s) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, s, ""));
    }
     
    public void warn(String s) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, s, ""));
    }
     
    public void error(String s) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, s, ""));
    }
     
    public void fatal(String s) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, s, ""));
    }
    
    public boolean isEmpty() {
    	return FacesContext.getCurrentInstance().getMessageList().isEmpty();
    }

    public void clear() {
        //Removed because it throws a UnsupportedOperationException on an unmodifiable list
    	//FacesContext.getCurrentInstance().getMessageList().clear();
    }
}
