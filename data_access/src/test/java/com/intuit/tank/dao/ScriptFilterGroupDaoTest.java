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
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.util.ScriptFilterType;

public class ScriptFilterGroupDaoTest {

    private ScriptFilterGroupDao dao;
    private ScriptFilterDao filterDao;
    private final List<Integer> groups = new ArrayList<>();
    private final List<Integer> filters = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        dao = new ScriptFilterGroupDao();
        filterDao = new ScriptFilterDao();
    }

    @AfterEach
    public void tearDown() {
        groups.forEach(dao::delete);
        filters.forEach(filterDao::delete);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void findAll_loadsFiltersWithoutAQueryPerGroup() {
        for (int g = 0; g < 4; g++) {
            ScriptFilterGroup group = new ScriptFilterGroup();
            group.setName("group-" + g);
            group.setCreator("test");
            // saved empty first, then merged with its filters, as FilterServiceV2Impl does
            group = dao.saveOrUpdate(group);
            for (int f = 0; f < 3; f++) {
                ScriptFilter filter = filterDao.saveOrUpdate(filter("filter-" + g + "-" + f));
                filters.add(filter.getId());
                group.addFilter(filter);
            }
            groups.add(dao.saveOrUpdate(group).getId());
        }
        Statistics statistics = dao.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            List<ScriptFilterGroup> all = dao.findAll();

            assertEquals(4, all.size());
            all.forEach(group -> {
                assertEquals(3, group.getFilters().size());
                group.getFilters().forEach(filter -> {
                    assertEquals(1, filter.getActions().size());
                    assertEquals(1, filter.getConditions().size());
                });
            });
            // the groups, one subselect for their filters, then one each for the filters' actions and conditions
            assertEquals(4, statistics.getPrepareStatementCount());
        } finally {
            statistics.setStatisticsEnabled(false);
        }
    }

    private static ScriptFilter filter(String name) {
        ScriptFilter filter = new ScriptFilter();
        filter.setName(name);
        filter.setCreator("test");
        filter.setFilterType(ScriptFilterType.INTERNAL);
        ScriptFilterAction action = new ScriptFilterAction();
        action.setScope("request");
        action.setKey("key");
        action.setValue("value");
        filter.addAction(action);
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setScope("request");
        condition.setCondition("Contains");
        condition.setValue("value");
        filter.addCondition(condition);
        return filter;
    }
}
