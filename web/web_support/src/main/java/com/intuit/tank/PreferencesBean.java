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
import java.util.List;
import java.util.TimeZone;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Named;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.FastDateFormat;

import com.intuit.tank.admin.Deleted;
import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.prefs.PreferencesChangedListener;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.ColumnPreferences.Hidability;
import com.intuit.tank.project.ColumnPreferences.Visibility;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ReportUtil;

/**
 * PreferencesBean
 * 
 * @author dangleton
 * 
 */
@SessionScoped
@Named
public class PreferencesBean implements Serializable, PreferencesChangedListener {

    private static final long serialVersionUID = 1L;
    private String preferredDateTimeFormat = TankConstants.DATE_FORMAT;
    private String preferredTimeStampFormat = ReportUtil.DATE_FORMAT;

    private FastDateFormat timestampFormat;

    private FastDateFormat dateTimeFotmat;

    private Preferences preferences;

    private int screenWidth = 1200;
    private int screenHeight = 600;

    private TimeZone clientTimeZone = TimeZone.getTimeZone("GMT");

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
    }

    /**
     * 
     * @param user
     */
    public void observeLogin(@Observes(notifyObserver = Reception.IF_EXISTS) @Deleted Preferences preferences) {
        init(preferences.getCreator());
    }

    public void init(String owner) {
        preferences = new PreferencesDao().getForOwner(owner);
        validatePrefs(owner);
    }

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

    /**
     * @param owner
     * @return
     */
    private void validatePrefs(String owner) {
        if (preferences == null) {
            preferences = new Preferences();
            preferences.setCreator(owner);
        }
        boolean needsSave = false;
        needsSave |= checkPrefs(preferences.getCreator(), preferences.getProjectTableColumns(),
                DefaultTableColumnUtil.PROJECT_COL_PREFS);
        needsSave |= checkPrefs(preferences.getCreator(), preferences.getDatafilesTableColumns(),
                DefaultTableColumnUtil.DATA_FILES_COL_PREFS);
        needsSave |= checkPrefs(preferences.getCreator(), preferences.getJobsTableColumns(),
                DefaultTableColumnUtil.JOBS_COL_PREFS);
        needsSave |= checkPrefs(preferences.getCreator(), preferences.getScriptsTableColumns(),
                DefaultTableColumnUtil.SCRIPTS_COL_PREFS);
        needsSave |= checkPrefs(preferences.getCreator(), preferences.getScriptStepTableColumns(),
                DefaultTableColumnUtil.SCRIPT_STEPS_COL_PREFS);
        if (needsSave) {
            preferences = new PreferencesDao().saveOrUpdate(preferences);
        }

    }

    private boolean checkPrefs(String owner, List<ColumnPreferences> existingPrefs,
            List<ColumnPreferences> defaultPreferences) {
        boolean ret = false;
        for (int i = 0; i < defaultPreferences.size(); i++) {
            ColumnPreferences p = defaultPreferences.get(i);
            if (!existingPrefs.contains(p)) {
                ColumnPreferences pref = new ColumnPreferences(p.getColName(), p.getDisplayName(), p.getSize(),
                        p.isVisible() ? Visibility.VISIBLE : Visibility.HIDDEN, p.isHideable() ? Hidability.HIDABLE
                                : Hidability.NON_HIDABLE);
                pref.setCreator(owner);
                existingPrefs.add(i, pref);
                ret = true;
            }
        }
        return ret;
    }

    /**
     * @{inheritDoc
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

    /**
     * 
     */
    public PreferencesBean() {
        dateTimeFotmat = FastDateFormat.getInstance(preferredDateTimeFormat);
        timestampFormat = FastDateFormat.getInstance(preferredTimeStampFormat);
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
    public FastDateFormat getDateTimeFotmat() {
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
