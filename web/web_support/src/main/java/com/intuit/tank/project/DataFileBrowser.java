package com.intuit.tank.project;

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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.PreferencesBean;
import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.datafile.util.DataFileServiceUtil;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.util.DataFileUtil;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;

@Named
@ViewScoped
public class DataFileBrowser extends SelectableBean<DataFile> implements Serializable, Multiselectable<DataFile> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(DataFileBrowser.class);

    private SelectableWrapper<DataFile> selectedFile;

    @Inject
    @Modified
    private Event<ModifiedDatafileMessage> dataFileEvent;

    @Inject
    private Messages messages;

    @Inject
    private Security security;

    private DataFile viewDatafile;

    private int currentPage;
    private int inputPage = 1;
    private int numEntriesToShow = 50;

    private SelectItem[] creatorList;

    private List<String> currentEntries;

    private boolean current = true;

    @Inject
    private PreferencesBean userPrefs;

    @PostConstruct
    public void init() {
        tablePrefs = new TablePreferences(userPrefs.getPreferences().getDatafilesTableColumns());
        tablePrefs.registerListener(userPrefs);
    }

    /**
     * 
     * @{inheritDoc
     */
    public void delete(DataFile dataFile) {
        if (!security.hasRight(AccessRight.DELETE_DATAFILE) && !security.isOwner(dataFile)) {
            messages.warn("You don't have permission to delete this data file.");
        } else {
            try {
                new DataFileDao().delete(dataFile);
                messages.info("Datafile " + dataFile.getPath() + " has been deleted.");
                dataFileEvent.fire(new ModifiedDatafileMessage(dataFile, this));
            } catch (Exception e) {

            }
        }
    }

    /**
     * @return the creatorList
     */
    public SelectItem[] getCreatorList() {
        return creatorList;
    }

    /**
     * @return the viewDatafile
     */
    public DataFile getViewDatafile() {
        return viewDatafile;
    }

    /**
     * @param viewDatafile
     *            the viewDatafile to set
     */
    public void setViewDatafile(DataFile viewDatafile) {
        this.currentPage = 0;
        currentEntries = null;
        this.viewDatafile = viewDatafile;
    }

    public void selectDataFile() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        setViewDatafileId(Integer.parseInt(params.get("dataFileId")));
    }

    /**
     * @param viewDatafile
     *            the viewDatafile to set
     */
    public void setViewDatafileId(int viewDatafileId) {
        setViewDatafile(new DataFileDao().findById(viewDatafileId));
    }

    /**
     * @return the numEntriesToShow
     */
    public int getNumEntriesToShow() {
        return numEntriesToShow;
    }

    /**
     * @param numEntriesToShow
     *            the numEntriesToShow to set
     */
    public void setNumEntriesToShow(int numEntriesToShow) {
        this.numEntriesToShow = numEntriesToShow;
    }

    public void nextSetOfEntries() {
        currentPage++;
    }

    public void prevSetOfEntries() {
        currentPage--;
    }

    public String getEntries() {
        List<String> currentEntries = getCurrentEntries();
        int start = Math.max(0, Math.min(currentPage * numEntriesToShow, getTotalLines() - numEntriesToShow));
        int end = Math.max(0, Math.min(start + numEntriesToShow, getTotalLines()));
        return StringUtils.join(currentEntries.subList(start, end), '\n');
    }

    /**
     * 
     * @return
     */
    public int getStartIndex() {
        return Math.max(0, Math.min(currentPage * numEntriesToShow, getTotalLines() - numEntriesToShow)) + 1;
    }

    public boolean enablePrev() {
        return currentPage > 0;
    }

    public boolean enableNext() {
        return getEndIndex() < getTotalLines();
    }

    /**
     * 
     * @return
     */
    public int getEndIndex() {
        return Math.max(
                0,
                Math.min(Math.max(0, Math.min(currentPage * numEntriesToShow, getTotalLines() - numEntriesToShow))
                        + numEntriesToShow, getTotalLines()));
    }

    public int getTotalLines() {
        return getCurrentEntries().size();
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> getCurrentEntries() {
        if (currentEntries == null) {
            currentEntries = new ArrayList<String>();
            if (viewDatafile != null) {
                FileData fd = DataFileUtil.getFileData(viewDatafile); 
                try {
                    FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getDataFileStorageDir(), false);
                    currentEntries = IOUtils.readLines(fileStorage.readFileData(fd));
                } catch (IOException e) {
                    LOG.error("Error reading file " + fd.toString()+ ": " + e, e);
                    currentEntries.add("Error reading dataFile: " + e.toString());
                }
            } else {
                currentEntries.add("current Data File Not set.");
            }
        }
        return currentEntries;
    }

    /**
     * @return the selectedFile
     */
    public SelectableWrapper<DataFile> getSelectedFile() {
        return selectedFile;
    }

    /**
     * @param selectedFile
     *            the selectedFile to set
     */
    public void setSelectedFile(SelectableWrapper<DataFile> selectedFile) {
        this.selectedFile = selectedFile;
    }

    public int getNumPages() {
        int numCurrentEntries = getCurrentEntries().size();
        return numCurrentEntries / numEntriesToShow;
    }

    public void goToFirstPage() {
        currentPage = 0;
    }

    public void goToLastPage() {
        currentPage = getNumPages();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getInputPage() {
        return inputPage;
    }

    public void setInputPage(int inputPage) {
        this.inputPage = inputPage;
    }

    public void jumpToInputPage() {
        int pageNum = inputPage - 1;
        if (!(pageNum < 0 || pageNum > getNumPages())) {
            currentPage = pageNum;
        }
    }

    public String getDataFileDownloadLink(DataFile dataFile) {
        if (dataFile != null) {
            return DataFileServiceUtil.getDownloadUrl(dataFile.getId());
        } else {
            return "";
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public List<DataFile> getEntityList(ViewFilterType viewFilter) {
        List<DataFile> files = new DataFileDao().findFiltered(viewFilter);
        // Collections.sort(files);
        Set<String> set = new HashSet<String>();
        for (DataFile f : files) {
            set.add(f.getCreator());
        }
        List<String> list = new ArrayList<String>(set);
        Collections.sort(list);
        creatorList = new SelectItem[list.size() + 1];
        creatorList[0] = new SelectItem("", "All");
        for (int i = 0; i < list.size(); i++) {
            creatorList[i + 1] = new SelectItem(list.get(i));
        }

        current = true;
        return files;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isCurrent() {
        return current;
    }

    public void observerUpload(@Observes ModifiedDatafileMessage msg) {
        current = false;
    }

    public boolean canCreateDatafile() {
        return security.hasRight(AccessRight.CREATE_DATAFILE);
    }

}
