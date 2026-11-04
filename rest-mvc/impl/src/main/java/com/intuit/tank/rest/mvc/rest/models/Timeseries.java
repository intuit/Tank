/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.Date;
import java.util.List;

/**
 * Values over time for a chart. Every series has one value per entry in {@code times}; a null value means
 * there was no sample for that series at that time.
 */
public record Timeseries(List<Date> times, List<Series> series) {

    public record Series(String name, List<Integer> values) {
    }
}
