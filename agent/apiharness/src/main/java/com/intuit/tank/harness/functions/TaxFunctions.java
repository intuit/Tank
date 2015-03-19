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

import com.intuit.tank.harness.test.data.Variables;

/**
 * 
 * TaxFunctions functions useful for tax info. Functions start with #function and use dot notation to pass parameters.
 * The first parameter is the class of function. Valid values are:
 * <ul>
 * <li>getssn - @see {@link TaxFunctions#getSsn(long)}</li>
 * </ul>
 * 
 * @author dangleton
 * 
 */
public class TaxFunctions {

    private final static Object lockSSN = new Object();
    private final static String SSN_VAR = "SSN_START";
    private static long lastSSN = -1;

    // number = number.substring(0,3) + "-" + number.substring(3,5) + "-" + number.substring(5,9);

    static public boolean isValid(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("getssn"))
                return true;
            return false;
        } catch (Exception ex) {
            throw new UnsupportedOperationException(values[2]);
        }
    }

    /**
     * Process the numeric request
     * 
     * @param values
     *            The command line
     * @return The requested value; "" if there was an error
     */
    static public String executeFunction(String[] values, Variables variables) {
        try {
            if (values[2].equalsIgnoreCase("getssn"))
                return TaxFunctions.getSsn(Long.valueOf(variables.getVariable(SSN_VAR)));
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 
     * @param startSsn
     * @return
     */
    static String getSsn(long startSsn) {

        String ssnString = "";
        synchronized (lockSSN) {
            do {
                if (lastSSN < 0)
                    lastSSN = startSsn;
                else
                    lastSSN++;
            } while (!isValidSsn(lastSSN));
            ssnString = "" + lastSSN;
            for (int z = 0; z < (9 - ssnString.length()); z++)
                ssnString = "0" + ssnString;

        }

        ssnString = ssnString.substring(0, 3) + "-" + ssnString.substring(3, 5) + "-" + ssnString.substring(5, 9);
        return ssnString;
    }

    private static boolean isValidSsn(long ssn) {

        if (((ssn >= 1010001) && (ssn <= 699999999)) ||
                ((ssn >= 700010001) && (ssn <= 733999999)) ||
                ((ssn >= 750010001) && (ssn <= 763999999)) ||
                ((ssn >= 764010001) && (ssn <= 899999999))) {
            return true;
        }
        return false;
    }

}
