package com.intuit.tank.wrapper;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.view.filter.ViewFilterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SelectableBeanTest {

    // Concrete test implementation of SelectableBean
    private static class TestSelectableBean extends SelectableBean<String> {
        private List<String> entities;
        private boolean current = true;
        private List<String> deleted = new LinkedList<>();

        TestSelectableBean(List<String> entities) {
            this.entities = entities;
        }

        @Override
        public List<String> getEntityList(ViewFilterType viewFilter) {
            return entities;
        }

        @Override
        public void delete(String entity) {
            deleted.add(entity);
            entities.remove(entity);
        }

        @Override
        public boolean isCurrent() {
            return current;
        }

        void setCurrent(boolean current) {
            this.current = current;
        }

        List<String> getDeleted() {
            return deleted;
        }
    }

    private TestSelectableBean bean;

    @BeforeEach
    void setUp() {
        bean = new TestSelectableBean(new java.util.ArrayList<>(Arrays.asList("alpha", "beta", "gamma")));
        bean.tablePrefs = new TablePreferences(new LinkedList<>());
        bean.tableState = new TableViewState();
    }

    @Test
    public void testGetSelectionList_ReturnsWrappedEntities() {
        List<SelectableWrapper<String>> list = bean.getSelectionList();
        assertNotNull(list);
        assertEquals(3, list.size());
    }

    @Test
    public void testGetSelectionList_CachesWhenCurrent() {
        List<SelectableWrapper<String>> first = bean.getSelectionList();
        List<SelectableWrapper<String>> second = bean.getSelectionList();
        assertSame(first, second);
    }

    @Test
    public void testGetSelectionList_RefreshesWhenNotCurrent() {
        bean.getSelectionList(); // populate
        bean.setCurrent(false);
        List<SelectableWrapper<String>> refreshed = bean.getSelectionList();
        assertNotNull(refreshed);
        assertEquals(3, refreshed.size());
    }

    @Test
    public void testGetFilteredData_WhenNull_ReturnsSelectionList() {
        List<SelectableWrapper<String>> result = bean.getFilteredData();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testSetFilteredData_WithNull_ClearsFilter() {
        bean.setFilteredData(null);
        List<SelectableWrapper<String>> result = bean.getFilteredData();
        assertNotNull(result);
    }

    @Test
    public void testSetFilteredData_UnselectedItemsOutsideFilter() {
        List<SelectableWrapper<String>> all = bean.getSelectionList();
        all.get(0).setSelected(true);

        List<SelectableWrapper<String>> filtered = List.of(all.get(1));
        bean.setFilteredData(filtered);

        assertFalse(all.get(0).isSelected()); // was deselected
    }

    @Test
    public void testSelectAll() {
        bean.getSelectionList(); // ensure populated
        bean.selectAll();
        assertTrue(bean.hasSelected());
    }

    @Test
    public void testUnselectAll() {
        bean.getSelectionList();
        bean.selectAll();
        bean.unselectAll();
        assertFalse(bean.hasSelected());
    }

    @Test
    public void testHasSelected_Initially_ReturnsFalse() {
        assertFalse(bean.hasSelected());
    }

    @Test
    public void testDeleteSelected_DeletesSelectedItems() {
        List<SelectableWrapper<String>> list = bean.getSelectionList();
        list.get(0).setSelected(true);

        bean.deleteSelected();

        assertEquals(1, bean.getDeleted().size());
        assertEquals("alpha", bean.getDeleted().get(0));
    }

    @Test
    public void testRefresh_ForcesReload() {
        List<SelectableWrapper<String>> before = bean.getSelectionList();
        bean.refresh();
        List<SelectableWrapper<String>> after = bean.getSelectionList();
        assertNotNull(after);
    }

    @Test
    public void testGetViewFilterType_DefaultIsAll() {
        assertEquals(ViewFilterType.ALL, bean.getViewFilterType());
    }

    @Test
    public void testSetViewFilterType() {
        bean.setViewFilterType(ViewFilterType.LAST_TWO_WEEKS);
        assertEquals(ViewFilterType.LAST_TWO_WEEKS, bean.getViewFilterType());
    }

    @Test
    public void testGetViewFilterTypeList_ReturnsAllValues() {
        ViewFilterType[] types = bean.getViewFilterTypeList();
        assertNotNull(types);
        assertTrue(types.length > 0);
    }

    @Test
    public void testGetTablePrefs() {
        assertNotNull(bean.getTablePrefs());
    }

    @Test
    public void testGetTableState() {
        assertNotNull(bean.getTableState());
    }
}
