/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Reader;
import java.util.List;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.vm.exception.WatsParseException;

/**
 * RecordedScriptReader
 * 
 * @author dangleton
 * 
 */
public interface RecordedScriptReader {

    public abstract List<ScriptStep> read(String xml) throws WatsParseException;

    public abstract List<ScriptStep> read(Reader reader) throws WatsParseException;

}