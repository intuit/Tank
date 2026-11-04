/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.util.ScriptFilterType;

public class ScriptFilterDaoTest {

    private ScriptFilterDao dao;
    private final List<Integer> created = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        dao = new ScriptFilterDao();
    }

    @AfterEach
    public void tearDown() {
        created.forEach(dao::delete);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void findAll_loadsActionsAndConditionsWithoutAQueryPerFilter() {
        for (int i = 0; i < 5; i++) {
            created.add(dao.saveOrUpdate(filter("filter-" + i)).getId());
        }
        Statistics statistics = dao.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            List<ScriptFilter> filters = dao.findAll();

            assertEquals(5, filters.size());
            filters.forEach(f -> {
                assertEquals(2, f.getActions().size());
                assertEquals(2, f.getConditions().size());
            });
            // the filters, then one subselect each for actions and conditions
            assertEquals(3, statistics.getPrepareStatementCount());
        } finally {
            statistics.setStatisticsEnabled(false);
        }
    }

    private static ScriptFilter filter(String name) {
        ScriptFilter filter = new ScriptFilter();
        filter.setName(name);
        filter.setCreator("test");
        filter.setFilterType(ScriptFilterType.INTERNAL);
        for (int i = 0; i < 2; i++) {
            ScriptFilterAction action = new ScriptFilterAction();
            action.setScope("request");
            action.setKey("key" + i);
            action.setValue("value" + i);
            filter.addAction(action);
            ScriptFilterCondition condition = new ScriptFilterCondition();
            condition.setScope("request");
            condition.setCondition("Contains");
            condition.setValue("value" + i);
            filter.addCondition(condition);
        }
        return filter;
    }
}
