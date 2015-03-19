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

import com.intuit.tank.project.DataFile;

/**
 * ModifiedEntityMessage
 * 
 * @author dangleton
 * 
 */
public class ModifiedDatafileMessage extends ModifiedEntityMessage<DataFile> {

    /**
     * @param modified
     * @param modifier
     */
    public ModifiedDatafileMessage(DataFile modified, Object modifier) {
        super(modified, modifier);
    }

}
