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

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import com.ocpsoft.shade.org.apache.commons.beanutils.PropertyUtils;

/**
 * PropertyComparer
 * 
 * @author dangleton
 * 
 */
public class PropertyComparer<T> implements Comparator<T> {

    private String propertyName;
    private SortOrder sortOrder;

    /**
     * @param propertyName
     */
    public PropertyComparer(String propertyName) {
        this(propertyName, SortOrder.ASCENDING);
    }

    /**
     * @param propertyName
     * @param sortOrder
     */
    public PropertyComparer(String propertyName,
            PropertyComparer.SortOrder sortOrder) {
        super();
        this.propertyName = propertyName;
        this.sortOrder = sortOrder;
    }

    /**
     * @{inheritDoc
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compare(T src, T tgt) {
        int retVal = 0;
        if (src == null && tgt == null) {
            retVal = 0;
        } else if (src != null && tgt == null) {
            retVal = 1;
        } else if (src == null && tgt != null) {
            retVal = -1;
        } else {
            try {
                Object property = PropertyUtils.getProperty(src, propertyName);
                Object property2 = PropertyUtils.getProperty(tgt, propertyName);

                if (property == null && property2 == null) {
                    retVal = 0;
                } else if (property == null && property2 != null) {
                    retVal = -1;
                } else if (property != null && property2 == null) {
                    retVal = 1;
                } else if (Comparable.class.isAssignableFrom(property
                        .getClass())) {
                    Comparable c1 = (Comparable) property;
                    Comparable c2 = (Comparable) property2;
                    retVal = c1.compareTo(c2);
                } else {
                    retVal = property.toString()
                            .compareTo(property2.toString());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "Cannot access the method.  Possible error in setting the access type for the getter setters of "
                                + propertyName);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getMessage());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No getter/setter method found for "
                        + propertyName);
            }
        }

        if (sortOrder == SortOrder.DESCENDING) {
            retVal = retVal * -1;
        }
        return retVal;
    }

    public enum SortOrder {
        ASCENDING, DESCENDING
    }
}
