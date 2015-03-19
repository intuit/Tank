package com.intuit.tank.harness.test.validation;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressions {

    static public boolean validFormat(String regExp, String input) {
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }
}
