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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

/**
 * TransactionContainer
 * 
 * @author dangleton
 * 
 */
public class TransactionContainer {

    private static final Logger LOG = Logger.getLogger(TransactionContainer.class);

    private static EntityManagerFactory entityManagerFactory;
    private static volatile boolean initialized = false;  
    private static Boolean lock = new Boolean(true);

    private EntityManager em;
    private EntityTransaction transaction;
    private Object initiatingObject;

    /**
     * @param em
     * @param initiatingObject
     */
    public TransactionContainer() {
    	synchronized(lock){  
            
  	      if(initialized){  
  	        return;  
  	      }  
  	        
  	      initialized = true;  
  	        
  	      try{  
  	    	  entityManagerFactory = Persistence.createEntityManagerFactory("wats"); 
  	          
  	      } catch(Throwable t){  
  	        LOG.error("Failed to setup persistence unit!", t);  
  	      }  
  	}  
    }

    /**
     * @return the em
     */
    public EntityManager getEntityManager() {
        if (em == null) {
            em = entityManagerFactory.createEntityManager();
        }
        return em;
    }

    public void startTrasaction(Object initiatingObject) {
        if (em == null) {
            getEntityManager();
        }
        if (transaction == null) {
            transaction = em.getTransaction();
            transaction.begin();
            LOG.debug("Starting transaciton with initiating Object " + initiatingObject);
            this.initiatingObject = initiatingObject;
        } else {
            LOG.debug("Transaction already started with initiating Object " + this.initiatingObject
                    + " attempted initiating object " + initiatingObject);
        }
    }

    public void commitTransaction(Object initiatingObject) {
        if (transaction != null && this.initiatingObject == initiatingObject) {
            LOG.debug("Committing transaciton with initiating Object " + initiatingObject);
            transaction.commit();
        } else {
            LOG.debug("Commit ignored from initiating Object " + initiatingObject + " need initiating object "
                    + this.initiatingObject);
        }
    }
    
    public void rollbackTransaction(Object initiatingObject) {
        if (transaction != null && this.initiatingObject == initiatingObject) {
            LOG.debug("Rollback transaciton with initiating Object " + initiatingObject);
            transaction.rollback();
        } else {
            LOG.debug("Rollback ignored from initiating Object " + initiatingObject + " need initiating object "
                    + this.initiatingObject);
        }
    }

    public void cleanup(Object initiatingObject) {
        if (this.initiatingObject == initiatingObject) {
            if (transaction != null) {
                if (transaction.isActive()) {
                    LOG.info("Rolling back transaciton with initiating Object " + initiatingObject);
                    transaction.rollback();
                }
                transaction = null;
            }

            if (em != null && em.isOpen()) {
                LOG.debug("Closing EntityManager");
                em.clear();
                em.close();
                em = null;
            }
        } else {
            LOG.debug("Rollback ignored from initiating Object " + initiatingObject + " need initiating object "
                    + this.initiatingObject);
        }
    }

}
