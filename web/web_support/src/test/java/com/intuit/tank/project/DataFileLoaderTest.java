package com.intuit.tank.project;

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

import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.wrapper.VersionContainer;
import jakarta.faces.model.SelectItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataFileLoaderTest {

    @Test
    public void testConstructor() {
        DataFileLoader loader = new DataFileLoader();
        assertNotNull(loader);
    }

    @Test
    public void testGetCreatorList_ReturnsNonNull() {
        DataFileLoader loader = new DataFileLoader();
        SelectItem[] items = loader.getCreatorList();
        assertNotNull(items);
        assertTrue(items.length >= 1);
        assertEquals("", items[0].getValue());
        assertEquals("All", items[0].getLabel());
    }

    @Test
    public void testGetCreatorList_CalledTwice_ConsistentResult() {
        DataFileLoader loader = new DataFileLoader();
        SelectItem[] first = loader.getCreatorList();
        SelectItem[] second = loader.getCreatorList();
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first.length, second.length);
    }

    @Test
    public void testObserveEvents_WhenDataFileClass_Invalidates() {
        DataFileLoader loader = new DataFileLoader();
        loader.getCreatorList(); // init
        ModifiedEntityMessage msg = new ModifiedEntityMessage(DataFile.class, 0, ModificationType.UPDATE);
        assertDoesNotThrow(() -> loader.observeEvents(msg));
        // After invalidation, getCreatorList should still work
        assertNotNull(loader.getCreatorList());
    }

    @Test
    public void testObserveEvents_WhenOtherClass_NoEffect() {
        DataFileLoader loader = new DataFileLoader();
        loader.getCreatorList();
        ModifiedEntityMessage msg = new ModifiedEntityMessage(Script.class, 0, ModificationType.UPDATE);
        assertDoesNotThrow(() -> loader.observeEvents(msg));
    }

    @Test
    public void testGetVersionContainer_ReturnsContainer() {
        DataFileLoader loader = new DataFileLoader();
        VersionContainer<DataFile> container = loader.getVersionContainer(ViewFilterType.ALL);
        assertNotNull(container);
        assertNotNull(container.getEntities());
    }

    @Test
    public void testIsCurrent_AfterLoad_ReturnsTrue() {
        DataFileLoader loader = new DataFileLoader();
        VersionContainer<DataFile> container = loader.getVersionContainer(ViewFilterType.ALL);
        assertTrue(loader.isCurrent(container.getVersion()));
    }

    @Test
    public void testIsCurrent_WithWrongVersion_ReturnsFalse() {
        DataFileLoader loader = new DataFileLoader();
        loader.getVersionContainer(ViewFilterType.ALL);
        assertFalse(loader.isCurrent(-999));
    }
}
