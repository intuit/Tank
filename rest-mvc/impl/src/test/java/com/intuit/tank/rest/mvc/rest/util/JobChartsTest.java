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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JobChartsTest {

    private static final Date T1 = new Date(1000);
    private static final Date T2 = new Date(2000);

    @Test
    void usersAlignSeriesOnTimesWithGaps() {
        Map<Date, List<UserDetail>> map = new HashMap<>();
        map.put(T2, List.of(new UserDetail("login", 20), new UserDetail("search", 5)));
        map.put(T1, List.of(new UserDetail("login", 10)));

        Timeseries series = JobCharts.users(map);

        assertEquals(List.of(T1, T2), series.times());
        assertEquals(List.of(new Timeseries.Series("login", List.of(10, 20)),
                new Timeseries.Series("search", Arrays.asList(null, 5))), series.series());
    }

    @Test
    void usersAddUpDuplicateScripts() {
        Map<Date, List<UserDetail>> map = Map.of(T1, List.of(new UserDetail("login", 10), new UserDetail("login", 4)));
        assertEquals(List.of(14), JobCharts.users(map).series().get(0).values());
    }

    @Test
    void tpsHasTotalFirst() {
        Map<Date, Map<String, TPSInfo>> map = new HashMap<>();
        map.put(T1, Map.of("GET /a", new TPSInfo(T1, "GET /a", 50, 10), "GET /b", new TPSInfo(T1, "GET /b", 20, 10)));
        map.put(T2, Map.of("GET /a", new TPSInfo(T2, "GET /a", 30, 10)));

        Timeseries series = JobCharts.tps(map);

        assertEquals(List.of(T1, T2), series.times());
        assertEquals(JobCharts.TOTAL_TPS, series.series().get(0).name());
        assertEquals(List.of(7, 3), series.series().get(0).values());
        assertEquals(new Timeseries.Series("GET /a", List.of(5, 3)), series.series().get(1));
        assertEquals(new Timeseries.Series("GET /b", Arrays.asList(2, null)), series.series().get(2));
    }

    @Test
    void noData() {
        assertEquals(new Timeseries(List.of(), List.of()), JobCharts.users(null));
        assertEquals(new Timeseries(List.of(), List.of()), JobCharts.tps(null));
    }
}
