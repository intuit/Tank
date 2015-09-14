package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

public enum AccessRight {
    DELETE_SCRIPT("Delete Script"),
    EDIT_SCRIPT("Edit Script"),
    CREATE_SCRIPT("Create Script"),
    DELETE_PROJECT("Delete Project"),
    EDIT_PROJECT("Edit Project"),
    CREATE_PROJECT("Create Project"),
    DELETE_DATAFILE("Delete Data File"),
    EDIT_DATAFILE("Edit Data File"),
    CREATE_DATAFILE("Create Data File"),
    DELETE_FILTER("Delete Filter"),
    EDIT_FILTER("Edit Filter"),
    CREATE_FILTER("Create Filter"),
    CONTROL_JOB("Control Job");

    private String display;

    AccessRight(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return this.display;
    }

}
