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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Named;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.intuit.tank.admin.Deleted;
import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.prefs.PreferencesChangedListener;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.rest.mvc.rest.util.TableColumnDefaults;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ReportUtil;

/**
 * PreferencesBean
 * 
 * @author dangleton
 * 
 */

@Named
@SessionScoped
public class PreferencesBean implements Serializable, PreferencesChangedListener {

    private static final long serialVersionUID = 1L;
    private String preferredDateTimeFormat = TankConstants.DATE_FORMAT;
    private String preferredTimeStampFormat = ReportUtil.DATE_FORMAT;

    private FastDateFormat timestampFormat;

    private FastDateFormat dateTimeFotmat;

    private Preferences preferences;

    private int screenWidth = 1200;
    private int screenHeight = 600;

    private TimeZone clientTimeZone = TimeZone.getTimeZone("PST");

    /**
     * Constructor
     */
    @PostConstruct
    public void init() {
        dateTimeFotmat = FastDateFormat.getInstance(TankConstants.DATE_FORMAT_WITH_TIMEZONE, clientTimeZone);
        timestampFormat = FastDateFormat.getInstance(preferredTimeStampFormat);
    }

    /**
     * @return the preferences
     */
    public Preferences getPreferences() {
        return preferences;
    }

    /**
     * @return the clientTimeZone
     */
    public TimeZone getClientTimeZone() {
        return clientTimeZone;
    }

    /**
     * @param clientTimeZone
     *            the clientTimeZone to set
     */
    public void setClientTimeZone(TimeZone clientTimeZone) {
        this.clientTimeZone = clientTimeZone;
        dateTimeFotmat = FastDateFormat.getInstance(TankConstants.DATE_FORMAT, clientTimeZone);
    }

    /**
     * 
     * @param preferences
     */
    public void observeLogin(@Observes(notifyObserver = Reception.IF_EXISTS) @Deleted Preferences preferences) {
        init(preferences.getCreator());
    }

    /**
     *
     * @param owner
     */
    public void init(String owner) {
        preferences = new PreferencesDao().getForOwner(owner);
        validatePrefs(owner);
    }

    /**
     *
     * @param width
     * @param height
     */
    public void setScreenSizes(String width, String height) {
        if (NumberUtils.isDigits(width)) {
            this.screenWidth = NumberUtils.toInt(width) - 20;
        }
        if (NumberUtils.isDigits(height)) {
            this.screenHeight = NumberUtils.toInt(height) - 20;
        }
    }

    /**
     * @return the screenWidth
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * @param screenWidth
     *            the screenWidth to set
     */
    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * @return the screenHeight
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * @param screenHeight
     *            the screenHeight to set
     */
    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    private void validatePrefs(String owner) {
        TableColumnDefaults.Result result = TableColumnDefaults.ensureDefaults(preferences, owner);
        preferences = result.preferences();
        if (result.changed()) {
            preferences = new PreferencesDao().saveOrUpdate(preferences);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prefsChanged() {
        if (preferences != null) {
            new PreferencesDao().saveOrUpdate(preferences);
        }

    }

    /**
     * Format the date according to the user's preferences
     * 
     * @param date
     * @return
     */
    public String formatDate(Date date) {
        String ret = null;
        if (date != null) {
            ret = dateTimeFotmat.format(date);
        }
        return ret;
    }

    public String getCollectionFilterString(Collection<? extends Object> c) {
        StringBuilder sb = new StringBuilder();
        if (c != null) {
            for (Object o : c) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(o.toString());
            }
        }
        return sb.toString();
    }

    /**
     * @return the timestampFormat
     */
    public FastDateFormat getTimestampFormat() {
        return timestampFormat;
    }

    /**
     * @param timestampFormat
     *            the timestampFormat to set
     */
    public void setTimestampFormat(FastDateFormat timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    /**
     * @return the dateTimeFotmat
     */
    public FastDateFormat getDateTimeFormat() {
        return dateTimeFotmat;
    }

    /**
     * @param dateTimeFotmat
     *            the dateTimeFotmat to set
     */
    public void setDateTimeFotmat(FastDateFormat dateTimeFotmat) {
        this.dateTimeFotmat = dateTimeFotmat;
    }

}
