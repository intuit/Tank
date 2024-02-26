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

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.ProjectBean;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.util.CreateDateComparator;
import com.intuit.tank.util.CreateDateComparator.SortOrder;

@Named
@ConversationScoped
public class AssociateDataFileBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjectBean projectBean;

    private DualListModel<DataFile> selectionModel;

    private List<DataFile> selectedAvailableFiles;
    private List<DataFile> selectedSelectedFiles;


    public List<DataFile> getSelectedAvailableFiles() {
        return selectedAvailableFiles;
    }

    public void setSelectedAvailableFiles(List<DataFile> selectedAvailableFiles) {
        this.selectedAvailableFiles = selectedAvailableFiles;
    }

    public List<DataFile> getSelectedSelectedFiles() {
        return selectedSelectedFiles;
    }

    public void setSelectedSelectedFiles(List<DataFile> selectedSelectedFiles) {
        this.selectedSelectedFiles = selectedSelectedFiles;
    }

    public void addAllToTarget() {
        selectionModel.getTarget().addAll(selectionModel.getSource());
        selectionModel.getSource().clear();
    }

    public void addToTarget() {
        if(!selectedAvailableFiles.isEmpty()) {
            selectionModel.getTarget().addAll(0, selectedAvailableFiles);
            selectionModel.getSource().removeAll(selectedAvailableFiles);
        }
    }

    public void removeFromTarget() {
        if(!selectedSelectedFiles.isEmpty()) {
            selectionModel.getSource().addAll(0, selectedSelectedFiles);
            selectionModel.getTarget().removeAll(selectedSelectedFiles);
        }
    }

    public void removeAllFromTarget() {
        selectionModel.getSource().addAll(selectionModel.getTarget());
        selectionModel.getTarget().clear();
    }

    public void onSourceSelect(SelectEvent event) {
        selectedAvailableFiles = (List<DataFile>) event.getObject();
    }

    public void onTargetSelect(SelectEvent event) {
        selectedSelectedFiles = (List<DataFile>) event.getObject();
    }

    /**
     * Initializes the instance variables. It also performs database operations to fetch the file items from the
     * database and perform check for the files that are associated with the given project and populate the instance
     * variables accordingly.
     */
    public void init() {
        initScriptSelectionModel();
    }

    /**
     * @return the selectionModel
     */
    public DualListModel<DataFile> getSelectionModel() {
        return selectionModel;
    }

    /**
     * @param selectionModel
     *            the selectionModel to set
     */
    public void setSelectionModel(DualListModel<DataFile> selectionModel) {
        if (selectionModel != null && (!selectionModel.getSource().isEmpty() || !selectionModel.getTarget().isEmpty())) {
            this.selectionModel = selectionModel;
        }
    }

    private void initScriptSelectionModel() {
        selectionModel = new DualListModel<DataFile>();
        List<DataFile> files = new DataFileDao().findAll();
        files.sort(new CreateDateComparator(SortOrder.DESCENDING));
        Set<Integer> dataFileIds = projectBean.getJobConfiguration().getDataFileIds();
        for (DataFile d : files) {
            if (dataFileIds.contains(d.getId())) {
                selectionModel.getTarget().add(d);
            } else {
                selectionModel.getSource().add(d);
            }
        }
    }

    public void observerUpload(@Observes ModifiedDatafileMessage msg) {
        DataFile df = msg.getModified();
        if (df != null && selectionModel != null) {
            selectionModel.getTarget().add(df);
        }
    }

    /**
     * Persist the job configuration to the database.
     */
    public void save() {
        projectBean.getJobConfiguration().getDataFileIds().clear();
        projectBean.getJobConfiguration().getDataFileIds().addAll(getDataFileIds(selectionModel.getTarget()));
    }

    /**
     * Get the set of ids of the data files of the selected file items.
     * 
     * @param list
     * 
     * @return
     */
    private static Set<Integer> getDataFileIds(List<DataFile> list) {
        return list.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

}
