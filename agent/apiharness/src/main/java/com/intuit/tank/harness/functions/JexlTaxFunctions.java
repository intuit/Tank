/*
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

import com.intuit.tank.vm.common.util.ExpressionContextVisitor;
import org.apache.commons.jexl3.JexlContext;

/**
 * JexlTaxFunctions functions useful for tax info.
 * 
 * @author rchalmela
 * 
 */
public class JexlTaxFunctions implements ExpressionContextVisitor {

    private final static Object lockSSN = new Object();
    private static long lastSSN = -1;

    /**
     * Returns a valid SSN based on the starting SSN given
     * 
     * @param ostartSsn
     * @return a valid SSN
     */
    public String getSsn(Object ostartSsn) {

        long startSsn = FunctionHandler.getLong(ostartSsn);

        String ssnString = "";
        synchronized (lockSSN) {
            do {
                if (lastSSN < 0)
                    lastSSN = startSsn;
                else
                    lastSSN++;
            } while (!isValidSsn(lastSSN));

            ssnString = "" + lastSSN;
            for (int z = 0; z <= (9 - ssnString.length()); z++) {
                ssnString = "0" + ssnString;
            }
        }

        ssnString = ssnString.substring(0, 3) + "-" + ssnString.substring(3, 5) + "-" + ssnString.substring(5, 9);
        return ssnString;
    }

    /**
     * Checks if given number is a valid SSN
     * 
     * @param ssn
     * @return true if the given number is a valid SSN, false otherwise
     */
    private boolean isValidSsn(long ssn) {

        if (((ssn >= 1010001) && (ssn <= 699999999)) || ((ssn >= 700010001) && (ssn <= 733999999))
                || ((ssn >= 750010001) && (ssn <= 763999999)) || ((ssn >= 764010001) && (ssn <= 899999999))) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.common.util.ExpressionContextVisitor#visit(org.apache.commons.jexl2.JexlContext)
     */
    @Override
    public void visit(JexlContext context) {
        context.set("taxFunctions", this);
    }

}
