/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.rest.mvc.rest.models.Timeseries;
import com.intuit.tank.vm.vmManager.models.UserDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Turns the controller's raw samples into chart series: users per script and transactions per second per
 * request key. The client draws the charts.
 */
public final class JobCharts {

    public static final String TOTAL_TPS = "Total TPS";

    private JobCharts() {
    }

    /**
     * @param detailMap users per script at each report time, from the agents' reports; may be null
     */
    public static Timeseries users(Map<Date, List<UserDetail>> detailMap) {
        Map<Date, Map<String, Integer>> samples = new TreeMap<>();
        if (detailMap != null) {
            detailMap.forEach((time, details) -> {
                Map<String, Integer> byScript = samples.computeIfAbsent(time, t -> new TreeMap<>());
                for (UserDetail detail : details) {
                    if (detail.getScript() != null && detail.getUsers() != null) {
                        byScript.merge(detail.getScript(), detail.getUsers(), Integer::sum);
                    }
                }
            });
        }
        return toSeries(samples, null);
    }

    /**
     * @param tpsMap TPS per request key at each period, from the results reader; may be null
     */
    public static Timeseries tps(Map<Date, Map<String, TPSInfo>> tpsMap) {
        Map<Date, Map<String, Integer>> samples = new TreeMap<>();
        if (tpsMap != null) {
            tpsMap.forEach((time, byKey) -> {
                Map<String, Integer> values = samples.computeIfAbsent(time, t -> new TreeMap<>());
                byKey.values().forEach(info -> values.merge(info.getKey(), info.getTPS(), Integer::sum));
            });
        }
        return toSeries(samples, TOTAL_TPS);
    }

    /**
     * @param totalName when not null, a first series with this name holds each time's sum
     */
    private static Timeseries toSeries(Map<Date, Map<String, Integer>> samples, String totalName) {
        List<Date> times = new ArrayList<>(samples.keySet());
        Map<String, Integer[]> byName = new TreeMap<>();
        Integer[] totals = new Integer[times.size()];
        for (int i = 0; i < times.size(); i++) {
            int total = 0;
            for (Map.Entry<String, Integer> value : samples.get(times.get(i)).entrySet()) {
                byName.computeIfAbsent(value.getKey(), k -> new Integer[times.size()])[i] = value.getValue();
                total += value.getValue();
            }
            totals[i] = total;
        }
        List<Timeseries.Series> series = new ArrayList<>();
        if (totalName != null && !times.isEmpty()) {
            series.add(new Timeseries.Series(totalName, Arrays.asList(totals)));
        }
        byName.forEach((name, values) -> series.add(new Timeseries.Series(name, Arrays.asList(values))));
        return new Timeseries(times, series);
    }
}
