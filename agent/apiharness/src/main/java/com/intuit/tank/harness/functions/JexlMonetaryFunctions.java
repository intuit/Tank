/**
 * Copyright 2012 Intuit Inc. All Rights Reserved
 */

package com.intuit.tank.harness.functions;

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

import org.apache.commons.jexl3.JexlContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.common.util.ExpressionContextVisitor;

/**
 * 
 * JexlMonetaryFunctions functions that return random monetary values.
 * 
 * @author rchalmela
 * 
 */
public class JexlMonetaryFunctions implements ExpressionContextVisitor {

    @SuppressWarnings("unused")
    private static Logger LOG = LogManager.getLogger(JexlIOFunctions.class);

    private static JexlNumericFunctions numericFunctions = new JexlNumericFunctions();

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.common.util.ExpressionContextVisitor#visit(org.apache.commons.jexl2.JexlContext)
     */
    @Override
    public void visit(JexlContext context) {
        context.set("monetaryFunctions", this);
    }

    /**
     * Get a positive random monetary value of given length(of the Integral part)
     * 
     * @param length
     * @return random monetary value in string form
     */
    public String randomPositive(Object length) {
        return numericFunctions.randomPositiveFloat(length, 2);
    }

    /**
     * Gets a random negative monetary value of given length (of the Integral part)
     * 
     * @param length
     * @return random monetary value in string form
     */
    public String randomNegative(Object length) {
        return numericFunctions.randomNegativeFloat(length, 2);
    }

}
