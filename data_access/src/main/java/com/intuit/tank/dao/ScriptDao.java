/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.cfg.RecoverableException;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.SerializedScriptStep;
import com.intuit.tank.vm.common.util.MethodTimer;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class ScriptDao extends BaseDao<Script> {
    private static final Logger LOG = LogManager.getLogger(ScriptDao.class);

    /**
     * @param entityClass
     */
    public ScriptDao() {
        super();
        setReloadEntities(true);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Script findById(Integer id) {
        Script script = super.findById(id);
        if (script != null) {
            script = loadScriptSteps(script);
        }
        return script;
    }

    /**
     * @{inheritDoc
     */
    public Script getScript(Integer id) {
        Script script = super.findById(id);
        return script;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void delete(Integer id) throws HibernateException {
        EntityManager em = getEntityManager();
        try {
        	begin();
            Script entity = em.find(Script.class, id);
            if (entity != null) {
                // check if it is used in scriptGroups
                List<ScriptGroupStep> scriptGroupsForScript = new ScriptGroupStepDao().getScriptGroupsForScript(entity);
                if (!scriptGroupsForScript.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (ScriptGroupStep step : scriptGroupsForScript) {
                        Project p = extractProject(step);
                        if (sb.length() != 0) {
                            sb.append(", ");
                        }
                        if (p != null) {
                            sb.append(p.getName()).append(" (id=").append(p.getId()).append(")");
                        } else {
                            sb.append("unknown project");
                        }
                    }
                    if (sb.length() > 0) {
                        // throw exception
                        throw new IllegalArgumentException("Cannot delete script " + entity.getName()
                                + " because it is used in the following projects: " + sb.toString());
                    }
                }
                LOG.debug("deleting entity " + entity.toString());
                em.remove(entity);
                commit();
            }
            commit();
        } catch (Exception e) {
        	rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
    }

    private Project extractProject(ScriptGroupStep step) {
        if (step.getScriptGroup() != null &&
                step.getScriptGroup().getTestPlan() != null
                && step.getScriptGroup().getTestPlan().getWorkload() != null
                && step.getScriptGroup().getTestPlan().getWorkload().getProject() != null) {
            return step.getScriptGroup().getTestPlan().getWorkload().getProject();
        }
        return null;
    }

    /**
     * 
     * @param projectId
     * @return
     */
    public List<Script> getScriptsForProductId(int productId) {
        // TODO: DA Fix when using real projects in model
        // List<Script> result = new ArrayList<Script>();
        // ProjectDao projectDao = new ProjectDao();
        // Project project = projectDao.findById(projectId);
        // for (Workload w : project.getWorkloads()) {
        // for (ScriptGroup g : w.getScriptGroups()) {
        // for (ScriptGroupStep step : g.getScriptGroupSteps()) {
        // result.add(step.getScript());
        // }
        // }
        // }
        // return result;
        return findAll();
    }

    public Script loadScriptSteps(@Nonnull Script script) {
        if (script.getScriptSteps() == null || script.getScriptSteps().isEmpty()) {
            SerializedScriptStep serializedScriptStep = new SerializedScriptStepDao().findById(script
                    .getSerializedScriptStepId());
            script.setSerializedSteps(serializedScriptStep);
        }
        return script;
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public Script saveOrUpdate(Script script) {
        MethodTimer mt = new MethodTimer(LOG, getClass(), "saveOrUpdate").start();
        int size = script.getScriptSteps().size();
        ScriptUtil.setScriptStepLabels(script);
        // try {
        LOG.info("persisting script " + script.getName() + " with id " + script.getId()
                + " into database");
        EntityManager em = getEntityManager();
        try {
            begin();
            SerializedScriptStep serializedScriptStep = serialize(script.getScriptSteps());
            serializedScriptStep.setSerialzedData(
                    Hibernate.getLobCreator(getHibernateSession()).createBlob(serializedScriptStep.getBytes()));
            SerializedScriptStep serializedSteps = new SerializedScriptStepDao().saveOrUpdate(serializedScriptStep);
            script.setSerializedScriptStepId(serializedScriptStep.getId());
            if (script.getId() == 0) {
                em.persist(script);
            } else {
                script = em.merge(script);
            }
            LOG.debug("Saved Script Steps with id " + serializedSteps.getId() + " for script " + script.getId());
            commit();
        } catch (Exception e) {
        	rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        mt.markAndLog("Store script with " + size + " steps to database.");
        mt.endAndLog();
        return script;
    }

    public SerializedScriptStep serialize(List<ScriptStep> steps) {
        ObjectOutputStream s = null;
        try {
            // if (steps.size() > 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            s = new ObjectOutputStream(bos);
            s.writeObject(steps);
            return new SerializedScriptStep(bos.toByteArray());
            // }
        } catch (IOException e) {
            throw new RecoverableException(e);
        } finally {
            IOUtils.closeQuietly(s);
        }
    }

    // private String getUniqueProjects(List<ScriptGroupStep> steps) {
    // Set<String> projectNames = new HashSet<String>();
    // for (ScriptGroupStep step : steps) {
    // try {
    // if (step.getScriptGroup() != null) {
    // if (step.getScriptGroup().get)
    // }
    // projectNames.add(step.getScriptGroup().getWorkload().getProject().getName());
    // } catch (NullPointerException e) {
    // projectNames.add("Unkonwn Project with ScriptGroup " + step.getScriptGroup().getName());
    // }
    // }
    // ArrayList<String> list = new ArrayList<String>(projectNames);
    // Collections.sort(list);
    // return StringUtils.join(list, ", ");
    // }

}
