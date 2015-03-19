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

import javax.annotation.Nonnull;

/**
 * JpaUtil
 * 
 * @author dangleton
 * 
 */
public class JpaUtil {

    private JpaUtil() {

    }

    /**
     * Joins the association path for JPAQL.
     * 
     * @param alias
     *            the alias for the owning object
     * @param propertyName
     *            the propertyname
     * @return the associationPath e.g. a.name
     */
    public static final String associationPath(@Nonnull String alias, @Nonnull String propertyName) {
        return alias + "." + propertyName;
    }

    /**
     * Joins the association path for JPAQL.
     * 
     * @param alias
     *            the alias for the owning object
     * @param propertyName
     *            the property name
     * @return the associationPath e.g. SELECT p from Project p
     */
    @Nonnull
    public static final String selectPath(@Nonnull String alias, @SuppressWarnings("rawtypes") @Nonnull Class clazz) {
        return "SELECT " + " " + alias + " " + "FROM" + " " + clazz.getSimpleName() + " " + alias;
    }
}
