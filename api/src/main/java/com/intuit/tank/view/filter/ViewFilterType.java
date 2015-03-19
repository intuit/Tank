package com.intuit.tank.view.filter;

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

import java.util.Calendar;
import java.util.Date;

public enum ViewFilterType {
    ALL("All"),
    LAST_TWO_WEEKS("Last Two Weeks"),
    LAST_MONTH("Last 30 Days"),
    LAST_60_DAYS("Last 60 Days");

    private String display;

    ViewFilterType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public static Date getViewFilterDate(ViewFilterType viewFilter) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (viewFilter == ViewFilterType.LAST_MONTH) {
            calendar.add(Calendar.MONTH, -1);
        } else if (viewFilter == ViewFilterType.LAST_TWO_WEEKS) {
            calendar.add(Calendar.WEEK_OF_YEAR, -2);
        } else if (viewFilter == ViewFilterType.LAST_60_DAYS) {
            calendar.add(Calendar.DAY_OF_YEAR, -60);
        } else {
            calendar.add(Calendar.YEAR, -1000);
        }

        return calendar.getTime();
    }
}
