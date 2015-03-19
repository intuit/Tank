/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank;

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

import java.util.Arrays;
import java.util.List;

import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.ColumnPreferences.Hidability;
import com.intuit.tank.project.ColumnPreferences.Visibility;

/**
 * TableColumnPrefs
 * 
 * @author dangleton
 * 
 */
public final class DefaultTableColumnUtil {
    private DefaultTableColumnUtil() {
    }

    public static final List<ColumnPreferences> PROJECT_COL_PREFS = Arrays.asList(new ColumnPreferences[] {
            new ColumnPreferences("selectColumn", "Select", 20, Visibility.VISIBLE, Hidability.NON_HIDABLE),
            new ColumnPreferences("idColumn", "ID", 75, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("nameColumn", "Name", 250, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("productColumn", "Product", 135, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("commentsColumn", "Comments", 110, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("createColumn", "Create Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("modifiedColumn", "Modified Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("ownerColumn", "Owner", 100, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("actionsColumn", "Actions", 75, Visibility.VISIBLE, Hidability.NON_HIDABLE)
    });

    public static final List<ColumnPreferences> SCRIPTS_COL_PREFS = Arrays.asList(new ColumnPreferences[] {
            new ColumnPreferences("selectColumn", "Select", 20, Visibility.VISIBLE, Hidability.NON_HIDABLE),
            new ColumnPreferences("idColumn", "ID", 75, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("nameColumn", "Name", 250, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("productColumn", "Product", 135, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("commentsColumn", "Comments", 110, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("createColumn", "Create Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("modifiedColumn", "Modified Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("ownerColumn", "Owner", 100, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("actionsColumn", "Actions", 75, Visibility.VISIBLE, Hidability.NON_HIDABLE)
    });

    public static final List<ColumnPreferences> DATA_FILES_COL_PREFS = Arrays.asList(new ColumnPreferences[] {
            new ColumnPreferences("selectColumn", "Select", 20, Visibility.VISIBLE, Hidability.NON_HIDABLE),
            new ColumnPreferences("idColumn", "ID", 75, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("nameColumn", "Name", 250, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("createColumn", "Create Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("modifiedColumn", "Modified Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("ownerColumn", "Owner", 100, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("actionsColumn", "Actions", 75, Visibility.VISIBLE, Hidability.NON_HIDABLE)
    });

    public static final List<ColumnPreferences> JOBS_COL_PREFS = Arrays.asList(new ColumnPreferences[] {
            new ColumnPreferences("nameColumn", "Name", 250, Visibility.VISIBLE, Hidability.NON_HIDABLE),
            new ColumnPreferences("idColumn", "ID", 75, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("statusColumn", "Status", 150, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("regionColumn", "Region", 150, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("activeUsersColumn", "Active Users", 75, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("totalUsersColumn", "Total Users", 75, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("tpsColumn", "TPS", 50, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("validationColumn", "Validation Failures", 100, Visibility.VISIBLE,
                    Hidability.HIDABLE),
            new ColumnPreferences("startTimeColumn", "Start Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("endTimeColumn", "End Time", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("actionsColumn", "Actions", 210, Visibility.VISIBLE, Hidability.NON_HIDABLE)
    });

    public static final List<ColumnPreferences> SCRIPT_STEPS_COL_PREFS = Arrays.asList(new ColumnPreferences[] {
            new ColumnPreferences("indexColumn", "Index", 50, Visibility.VISIBLE, Hidability.NON_HIDABLE),
            new ColumnPreferences("groupColumn", "Group Name", 125, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("nameColumn", "Request Name", 125, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("methodColumn", "Method", 125, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("protocolColumn", "Protocol", 100, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("hostColumn", "Host", 125, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("pathColumn", "Path", 150, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("dataColumn", "Data", 250, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("mimeColumn", "MIME Type", 125, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("loggingColumn", "Logging Key", 125, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("failureColumn", "On Failure", 125, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("commentsColumn", "Comments", 110, Visibility.VISIBLE, Hidability.HIDABLE),
            new ColumnPreferences("validationColumn", "Validation", 75, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("assignmentColumn", "Assignment", 75, Visibility.HIDDEN, Hidability.HIDABLE),
            new ColumnPreferences("actionsColumn", "Actions", 75, Visibility.VISIBLE, Hidability.NON_HIDABLE)
    });

}
