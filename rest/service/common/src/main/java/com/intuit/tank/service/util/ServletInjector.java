/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.util;

/*
 * #%L
 * Rest Service Common Classes
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;

/**
 * InjectorUtil
 * 
 * @author dangleton
 * 
 */
public class ServletInjector<T> {

    private BeanManager getBeanManager(ServletContext servletContext) {
        BeanManager result = null;
        result = (BeanManager) servletContext
                .getAttribute("org.jboss.weld.environment.servlet.javax.enterprise.inject.spi.BeanManager");
        return result;
    }

    @SuppressWarnings({ "unchecked" })
    public T getManagedBean(ServletContext servletContext, Class<T> beanClass) {
        BeanManager beanManager = getBeanManager(servletContext);
        Bean<?> bean = beanManager.getBeans(beanClass).iterator().next();
        CreationalContext<?> cc = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, beanClass, cc);
    }
}
