package com.intuit.tank.vm.settings;

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

import java.util.Random;

/**
 * 
 * @author dangleton
 * 
 */
public class Range {
    private long min;
    private long max;
    private Random random = new Random();

    public Range(long min, long max) {
        super();
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public long getRandomValueWithin() {
        int num = (int) (max - min);
        if (num < 1) {
            return min;
        }
        return random.nextInt(num) + min;
    }

}