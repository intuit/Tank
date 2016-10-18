/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

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

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

/**
 * EntityManagerProducer
 * 
 * @author dangleton
 * 
 */
public class EntityManagerProducer {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("wats");

    @Produces
    @ConversationScoped
    @PersistenceUnit
    @Default
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
