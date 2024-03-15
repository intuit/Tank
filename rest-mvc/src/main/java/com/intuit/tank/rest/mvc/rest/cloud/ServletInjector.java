/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.cloud;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.servlet.ServletContext;

public class ServletInjector<T> {

    public BeanManager getBeanManager(ServletContext servletContext) {
        return (BeanManager) servletContext
                .getAttribute("org.jboss.weld.environment.servlet.jakarta.enterprise.inject.spi.BeanManager");
    }

    @SuppressWarnings({ "unchecked" })
    public T getManagedBean(ServletContext servletContext, Class<T> beanClass) {
        BeanManager beanManager = getBeanManager(servletContext);
        Bean<?> bean = beanManager.getBeans(beanClass).iterator().next();
        CreationalContext<?> cc = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, beanClass, cc);
    }
}
