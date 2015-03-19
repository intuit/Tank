/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common;

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

/**
 * PercentCompleteMonitor
 * 
 * @author dangleton
 * 
 */
public interface PercentCompleteMonitor extends Serializable {

    public abstract void setSavingStarted(int id);

    public abstract void setProcessingComplete(int id);

    public abstract void setError(int id, int errorCode);

    public abstract int getPerctComplete(int id);

}