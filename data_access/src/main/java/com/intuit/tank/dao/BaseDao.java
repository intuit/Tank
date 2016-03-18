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

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.Project;
import com.intuit.tank.view.filter.ViewFilterType;

/**
 * BaseDao
 * 
 * @author dangleton
 * 
 */
public abstract class BaseDao<T_ENTITY extends BaseEntity> {

    private static final Logger LOG = Logger.getLogger(BaseDao.class);

    private static final ThreadLocalEntityManagerProvider emProvider = new ThreadLocalEntityManagerProvider();

    private Class<T_ENTITY> entityClass;
    private boolean reloadEntities;

    /**
     * @param entityClass
     */
    @SuppressWarnings("unchecked")
    protected BaseDao() {
        // entity ManagerFactory = Persistence.createEntityManagerFactory("wats");
        @SuppressWarnings("rawtypes") Class getClass = (Class) getClass();
        ParameterizedType genericSuperclass = (ParameterizedType) getClass.getGenericSuperclass();
        this.entityClass = (Class<T_ENTITY>) genericSuperclass.getActualTypeArguments()[0];
    }

    /**
     * @return the reloadEntities
     */
    boolean isReloadEntities() {
        return reloadEntities;
    }

    /**
     * @param reloadEntities
     *            the reloadEntities to set
     */
    void setReloadEntities(boolean reloadEntities) {
        this.reloadEntities = reloadEntities;
    }

    /**
     * 
     * @param id
     * @return
     */
    public int getHeadRevisionNumber(int id) {
        int result = 0;
        try {
            begin();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(entityClass, id);
            if (!revisions.isEmpty()) {
                result = revisions.get(revisions.size() - 1).intValue();
            }
            commit();
        } catch (NoResultException e) {
            LOG.warn("No result for revision with id of " + id);
        } finally {
            cleanup();
        }
        return result;
    }

    /**
     * gets the entity at the specified revision
     * 
     * @param id
     *            the id of the entity to fetch
     * @param revisionNumber
     *            the revision number
     * @return the entity or null if no entity can be found
     */
    @Nullable
    public T_ENTITY findRevision(int id, int revisionNumber) {
        T_ENTITY result = null;
        try {
            begin();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            result = reader.find(entityClass, id, revisionNumber);
            commit();
        } catch (NoResultException e) {
            LOG.warn("No result for revision " + revisionNumber + " with id of " + id);
        } finally {
            cleanup();

        }
        return result;
    }

    /**
     * Persist or update the entity. Persist if the primary key is null update otherwise.
     * 
     * @param entity
     *            the entity to persist
     * @return the persisted unattached entity.
     * @throws HibernateException
     *             if there is an error in persistence
     */
    @Nonnull
    public T_ENTITY saveOrUpdate(@Nonnull T_ENTITY entity) throws HibernateException {
        try {
            EntityManager em = getEntityManager();
            begin();
            if (entity.getId() == 0) {
                em.persist(entity);
            } else {
                entity = em.merge(entity);
            }
            commit();
        } catch (ConstraintViolationException e) {
            for (@SuppressWarnings("rawtypes") ConstraintViolation v : e.getConstraintViolations()) {
                LOG.warn("ConstraintViolation for " + entityClass.getSimpleName() + " "
                        + "[property: " + v.getPropertyPath().iterator().next().getName() + "]" + " "
                        + "[message: " + v.getMessage() + "]" + " "
                        + "[invalid value: " + v.getInvalidValue() + "]");
            }
            throw e;
        } catch (PersistenceException e) { // wrapped in a Persistence Exception
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                for (@SuppressWarnings("rawtypes") ConstraintViolation v : cve.getConstraintViolations()) {
                    LOG.warn("ConstraintViolation for " + entityClass.getSimpleName() + " "
                            + "[property: " + v.getPropertyPath().iterator().next().getName() + "]" + " "
                            + "[message: " + v.getMessage() + "]" + " "
                            + "[invalid value: " + v.getInvalidValue() + "]");
                }
                throw cve;
            }
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        return entity;
    }

    /**
     * 
     * @param entities
     */
    public List<T_ENTITY> persistCollection(Collection<T_ENTITY> entities) {
        List<T_ENTITY> ret = new ArrayList<T_ENTITY>();
        try {
            EntityManager em = getEntityManager();
            begin();
            int count = 0;
            for (T_ENTITY entity : entities) {
                if (entity.getId() == 0) {
                    em.persist(entity);
                } else {
                    entity = em.merge(entity);
                }
                ret.add(entity);
                if (++count % 1000 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            commit();
        } catch (ConstraintViolationException e) {
            for (@SuppressWarnings("rawtypes") ConstraintViolation v : e.getConstraintViolations()) {
                LOG.warn("ConstraintViolation for " + entityClass.getSimpleName() + " "
                        + "[property: " + v.getPropertyPath().iterator().next().getName() + "]" + " "
                        + "[message: " + v.getMessage() + "]" + " "
                        + "[invalid value: " + v.getInvalidValue() + "]");
            }
            throw e;
        } catch (PersistenceException e) { // wrapped in a Persistence Exception
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                for (@SuppressWarnings("rawtypes") ConstraintViolation v : cve.getConstraintViolations()) {
                    LOG.warn("ConstraintViolation for " + entityClass.getSimpleName() + " "
                            + "[property: " + v.getPropertyPath().iterator().next().getName() + "]" + " "
                            + "[message: " + v.getMessage() + "]" + " "
                            + "[invalid value: " + v.getInvalidValue() + "]");
                }
                throw cve;
            }
            throw e;
        } catch (Exception e) {
            LOG.error("Error storing object to persistent storage: " + e.toString(), e);
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        return ret;
    }

    /**
     * Deletes the entity from the datastore and applies cascade rules.
     * 
     * @param id
     *            the id of the entity to delete
     * @throws HibernateException
     *             if there is an error in persistence
     */

    public void delete(@Nonnull Integer id) throws HibernateException {
        EntityManager em = getEntityManager();
        try {
            begin();
            T_ENTITY entity = em.find(entityClass, id);
            if (entity != null) {
                LOG.debug("deleting entity " + entity.toString());
                em.remove(entity);
            }
            commit();
        } finally {
            cleanup();
        }
    }

    /**
     * Deletes the entity from the datastore and applies cascade rules.
     * 
     * @param entity
     *            the entity to delete
     * @throws HibernateException
     *             if there is an error in persistence
     */

    public void delete(@Nonnull T_ENTITY entity) throws HibernateException {
        delete(entity.getId());

    }

    /**
     * Gets an entity by the id or null if no entity exists with the specified id.
     * 
     * @param id
     *            the primary key
     * @return the entity or null
     */
    @Nullable
    public T_ENTITY findById(@Nonnull Integer id) {
        T_ENTITY result = null;
        try {
            begin();
            result = getEntityManager().find(entityClass, id);
            if (reloadEntities && result != null) {
                getHibernateSession().refresh(result, LockOptions.READ);
            }
            commit();
        } finally {
            cleanup();
        }
        return result;
    }

    /**
     * Finds all where in IDs
     * 
     * @param ids
     * @return
     */
    @Nonnull
    public List<T_ENTITY> findForIds(@Nonnull List<Integer> ids) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(BaseEntity.PROPERTY_ID, "id", ids);
        StringBuilder sb = new StringBuilder();
        sb.append(buildQlSelect(prefix)).append(startWhere()).append(buildWhereClause(Operation.IN, prefix, parameter));
        return listWithJQL(sb.toString(), parameter);
    }

    /**
     * Finds all Objects of type T_ENTITY
     * 
     * @return the nonnull list of entities
     * @throws HibernateException
     *             if there is an error in persistence
     */
    @Nonnull
    public List<T_ENTITY> findAll() throws HibernateException {
        List<T_ENTITY> results = null;
        // try {
        EntityManager em = getEntityManager();
        // begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T_ENTITY> query = cb.createQuery(entityClass);
        query.from(entityClass);
        results = em.createQuery(query).getResultList();
        // commit();
        // } finally {
        // cleanup();
        // }
        return results;
    }

    /**
     * Find all objects of type T_ENTITY that satisfy the ViewFilterType
     * 
     * @param viewFilter
     * @return the list of entities that satisfy the filter
     */
    public List<T_ENTITY> findFiltered(ViewFilterType viewFilter) {
        List<T_ENTITY> results = null;
        EntityManager em = getEntityManager();
        try {
        	begin();
	        if (!viewFilter.equals(ViewFilterType.ALL)) {
	            CriteriaBuilder cb = em.getCriteriaBuilder();
	            CriteriaQuery<T_ENTITY> query = cb.createQuery(entityClass);
	            Root<T_ENTITY> root = query.from(entityClass);
	            query.select(root);
	            query.where(cb.greaterThan(root.<Date>get(BaseEntity.PROPERTY_CREATE), ViewFilterType.getViewFilterDate(viewFilter)));
	            query.orderBy(cb.desc(root.get(BaseEntity.PROPERTY_CREATE)));
	            results =  em.createQuery(query).getResultList();
	        } else {
	            CriteriaBuilder cb = em.getCriteriaBuilder();
	            CriteriaQuery<T_ENTITY> query = cb.createQuery(entityClass);
	            Root<T_ENTITY> root = query.from(entityClass);
	            query.select(root);
	            query.orderBy(cb.desc(root.get(BaseEntity.PROPERTY_CREATE)));
	            results =  em.createQuery(query).getResultList();
	        }
	        commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
        	cleanup();
        }
        return results;
    }

    /**
     * 
     * @param qlString
     * @param params
     * @return
     */
    @Nullable
    public T_ENTITY findOneWithJQL(String qlString, NamedParameter... params) {
        T_ENTITY result = null;
        TypedQuery<T_ENTITY> query = null;
        try {
            EntityManager em = getEntityManager();
            begin();
            query = em.createQuery(qlString, entityClass);
            for (NamedParameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
            result = query.getSingleResult();
            commit();
        } catch (Exception e) {
            LOG.info("no entity matching query "+query.toString());
        } finally {
            cleanup();
        }
        return result;
    }

    /**
     * returns list of entities meeting the specified criteria.
     * 
     * @param criterion
     *            varargs criterion. (use something like Restrictions.eq(propertyName, value);) null criterion returns
     *            all.
     * @param sortOrder
     *            the Order Object. Null value indicates no sort order.
     * @return the non null list.
     * @throws HibernateException
     *             if there is an error in persistence
     */
    @Nonnull
    public List<T_ENTITY> listWithJQL(String qlString, NamedParameter... params) {
        List<T_ENTITY> result = null;
        try {
            EntityManager em = getEntityManager();
            begin();
            TypedQuery<T_ENTITY> query = em.createQuery(qlString, entityClass);
            for (NamedParameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
            result = query.getResultList();
            commit();
        } finally {
            cleanup();
        }
        return result;
    }

    protected String buildQlSelect(String prefix) {
        return new StringBuilder().append("SELECT ").append(prefix).append(" FROM ").append(entityClass.getName())
                .append(" AS ").append(prefix).append(" ").toString();
    }

    protected String buildWhereClause(Operation op, String prefix, NamedParameter param) {
        if (op == Operation.NULL || op == Operation.NOT_NULL) {
            return buildFieldId(prefix, param.getField()) + " " + op.getRepresentation();
        }
        return buildFieldId(prefix, param.getField()) + buildParameterName(op, param.getName());
    }

    protected String buildFieldId(String prefix, String field) {
        return " " + prefix + "." + field + " ";
    }

    protected String buildParameterName(Operation op, String name) {
        return " " + op.getRepresentation() + " :" + name + op.getEnding() + " ";
    }

    protected String buildSortOrderClause(SortDirection direction, String prefix, String field) {
        return " ORDER BY " + prefix + "." + field + " " + direction.name();
    }

    /**
     * @return
     */
    protected String startWhere() {
        return " WHERE ";
    }

    /**
     * @return
     */
    protected String getAnd() {
        return " AND ";
    }

    /**
     * Gets an open hibernate session.
     * 
     * @return the session
     */
    protected Session getHibernateSession() {
        Session s = (Session) getEntityManager().getDelegate();
        return s;
    }

    /**
     * Gets a JPA EntityManager
     * 
     * @return the entityManager
     */
    protected EntityManager getEntityManager() {
        return emProvider.get().getEntityManager();

    }

    /**
     * @return the emProvider
     */
    protected ThreadLocalEntityManagerProvider getEmProvider() {
        return emProvider;
    }

    protected void begin() {
        emProvider.get().startTrasaction(this);
    }

    protected void commit() {
        emProvider.get().commitTransaction(this);
    }

    protected void cleanup() {
        emProvider.get().cleanup(this);
    }

    public static class ThreadLocalEntityManagerProvider extends ThreadLocal<TransactionContainer> {
        public TransactionContainer initialValue() {
            return new TransactionContainer();

        }
    }
}
