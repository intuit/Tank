package com.intuit.tank.util;

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

import java.util.ArrayList;
import java.util.List;

import com.intuit.tank.wrapper.SelectableWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SelectionTrackerTest {

    // Concrete Multiselectable implementation for testing
    private static class SimpleMultiselectable implements Multiselectable<String> {
        private List<SelectableWrapper<String>> list;
        private List<SelectableWrapper<String>> filtered;

        SimpleMultiselectable(String... items) {
            list = new ArrayList<>();
            for (String item : items) {
                list.add(new SelectableWrapper<>(item));
            }
            filtered = new ArrayList<>(list);
        }

        @Override
        public List<SelectableWrapper<String>> getSelectionList() {
            return list;
        }

        @Override
        public List<SelectableWrapper<String>> getFilteredData() {
            return filtered;
        }

        @Override
        public void deleteSelected() {}

        @Override
        public void selectAll() {}

        @Override
        public void unselectAll() {}

        @Override
        public boolean hasSelected() {
            return list.stream().anyMatch(SelectableWrapper::isSelected);
        }

        public void setFilteredItems(List<SelectableWrapper<String>> filteredData) {
            this.filtered = filteredData;
        }
    }

    private SimpleMultiselectable selectable;
    private SelectionTracker<String> tracker;

    @BeforeEach
    void setUp() {
        selectable = new SimpleMultiselectable("a", "b", "c");
        tracker = new SelectionTracker<>(selectable);
    }

    @Test
    public void testConstructor_NotNull() {
        assertNotNull(tracker);
    }

    @Test
    public void testHasSelected_Initially_ReturnsFalse() {
        assertFalse(tracker.hasSelected());
    }

    @Test
    public void testSelectAll_SelectsAllInFilteredData() {
        tracker.selectAll();
        assertTrue(tracker.hasSelected());
        selectable.getSelectionList().forEach(w -> assertTrue(w.isSelected()));
    }

    @Test
    public void testUnselectAll_UnselectsAll() {
        tracker.selectAll();
        tracker.unselectAll();
        assertFalse(tracker.hasSelected());
    }

    @Test
    public void testSelectAll_OnlySelectsFilteredItems() {
        // Filter to just first item
        List<SelectableWrapper<String>> filtered = List.of(selectable.getSelectionList().get(0));
        selectable.setFilteredItems(filtered);

        tracker.selectAll();

        assertTrue(selectable.getSelectionList().get(0).isSelected());
        assertFalse(selectable.getSelectionList().get(1).isSelected());
        assertFalse(selectable.getSelectionList().get(2).isSelected());
    }

    @Test
    public void testUnselectAll_OnlyUnselectsFilteredItems() {
        // Select all manually
        selectable.getSelectionList().forEach(w -> w.setSelected(true));

        // Filter to just first item
        List<SelectableWrapper<String>> filtered = List.of(selectable.getSelectionList().get(0));
        selectable.setFilteredItems(filtered);

        tracker.unselectAll();

        assertFalse(selectable.getSelectionList().get(0).isSelected());
        assertTrue(selectable.getSelectionList().get(1).isSelected()); // not in filter
    }

    @Test
    public void testHasSelected_AfterSelecting_ReturnsTrue() {
        selectable.getSelectionList().get(0).setSelected(true);
        assertTrue(tracker.hasSelected());
    }
}
