package com.intuit.tank.vm.common.util;

/*
 * #%L
 * Intuit Tank Api
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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MessageObject implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -5467477515213534232L;

    Object o;

    public MessageObject(Object o) {
        this.setObject(o);
    }

    public Object getObject() {
        return o;
    }

    public void setObject(Object o) {
        this.o = o;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        if (o != null) {
            return ToStringBuilder.reflectionToString(o);
        }
        return super.toString();
    }

}
