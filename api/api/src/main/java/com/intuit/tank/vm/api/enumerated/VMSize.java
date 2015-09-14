package com.intuit.tank.vm.api.enumerated;

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

/*
 * Large							:  4 ECUs, 2 Cores, 7.5 GB
 * ExtraLarge						:  8 ECUs, 4 Cores, 15 GB
 * HighMemoryExtraLarge				:  6.5 ECUs, 2 Cores, 17.1 GB
 * HighMemoryDoubleExtraLarge		:  13 ECUs, 4 Cores, 34.2 GB
 * HighMemoryQuadrupleExtraLarge	:  26 ECUs, 8 Cores, 68.4 GB
 * HighCPUExtraLarge				:  20 ECUs, 8 Cores, 7 GB
 */

public enum VMSize {
    Micro("t1.micro"), // $0.020 / hour
    Small("m1.small"), // $0.080 / hour
    Medium("m1.medium"), // $0.160 / hour
    Large("m1.large"), // $0.32 / hour
    ExtraLarge("m1.xlarge"), // $0.640 / hour
    HighMemoryExtraLarge("m2.xlarge"), // $0.450 / hour
    HighMemoryDoubleExtraLarge("m2.2xlarge"), // $0.900 / hour
    HighMemoryQuadrupleExtraLarge("m2.4xlarge"), // $1.800 / hour
    HighCPUMedium("c1.medium"), // $0.165 / hour
    HighCPUExtraLarge("c1.xlarge"), // $0.660 / hour
    HighIOExtraLarge("hi1.4xlarge")// $3.100 / hour
    ;

    private String representation;

    /**
     * @param representation
     */
    private VMSize(String representation) {
        this.representation = representation;
    }

    /**
     * @return the representation
     */
    public String getRepresentation() {
        return representation;
    }

    public static VMSize fromRepresentation(String representation) {
        for (VMSize s : VMSize.values()) {
            if (s.representation.equalsIgnoreCase(representation)) {
                return s;
            }
        }
        return null;
    }
}
