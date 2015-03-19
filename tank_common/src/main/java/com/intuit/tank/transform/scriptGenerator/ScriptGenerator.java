package com.intuit.tank.transform.scriptGenerator;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import com.intuit.tank.project.BaseJob;
import com.intuit.tank.project.ScriptStep;

public interface ScriptGenerator {

    public void addData(String key, String value);

    public String generate(List<ScriptStep> requests, BaseJob job);
}
