/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.api.enumerated;

import javax.xml.bind.annotation.XmlRootElement;

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

/**
 * TestStatus
 * 
 * @author dangleton
 * 
 */

@XmlRootElement
public enum JobQueueStatus {

    Created,
    Queued,
    Starting,
    Running,
    Stopped,
    Paused,
    RampPaused,
    Completed,
    Aborted

}
