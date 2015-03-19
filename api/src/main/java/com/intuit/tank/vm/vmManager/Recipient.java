/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.vmManager;

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

import com.intuit.tank.vm.api.enumerated.RecipientType;

/**
 * Recipient
 * 
 * @author dangleton
 * 
 */
public interface Recipient {

    /**
     * @return the address
     */
    public abstract String getAddress();

    /**
     * @return the type
     */
    public abstract RecipientType getType();

}