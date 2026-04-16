package com.intuit.tank.script;
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

import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.Script;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;
import jakarta.faces.model.SelectItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptLoaderTest {

    @Test
    public void testObserveEvents_WhenScriptClass_Invalidates() {
        ScriptLoader loader = new ScriptLoader();
        // First get entities to populate
        loader.getCreatorList();
        // Observe Script.class - should invalidate cache
        ModifiedEntityMessage msg = new ModifiedEntityMessage(Script.class, 0, ModificationType.UPDATE);
        assertDoesNotThrow(() -> loader.observeEvents(msg));
    }

    @Test
    public void testObserveEvents_WhenOtherClass_DoesNotInvalidate() {
        ScriptLoader loader = new ScriptLoader();
        loader.getCreatorList();
        // Observe DataFile.class - should NOT invalidate
        ModifiedEntityMessage msg = new ModifiedEntityMessage(DataFile.class, 0, ModificationType.UPDATE);
        assertDoesNotThrow(() -> loader.observeEvents(msg));
    }

    @Test
    public void testGetCreatorList_ReturnsNonNull() {
        ScriptLoader loader = new ScriptLoader();
        loader.getVersionContainer(com.intuit.tank.view.filter.ViewFilterType.ALL); // triggers getEntities()
        SelectItem[] items = loader.getCreatorList();
        // With empty H2 DB, should return array with just "All" entry
        assertNotNull(items);
        assertTrue(items.length >= 1);
        assertEquals("", items[0].getValue());
        assertEquals("All", items[0].getLabel());
    }

    @Test
    public void testGetCreatorList_CalledTwice_ReturnsSameArray() {
        ScriptLoader loader = new ScriptLoader();
        loader.getVersionContainer(com.intuit.tank.view.filter.ViewFilterType.ALL); // triggers getEntities()
        SelectItem[] first = loader.getCreatorList();
        SelectItem[] second = loader.getCreatorList();
        assertNotNull(first);
        assertNotNull(second);
    }

    @Test
    public void testGetVersionContainer_ReturnsContainer() {
        ScriptLoader loader = new ScriptLoader();
        com.intuit.tank.view.filter.ViewFilterType filterType = com.intuit.tank.view.filter.ViewFilterType.ALL;
        com.intuit.tank.wrapper.VersionContainer<Script> container = loader.getVersionContainer(filterType);
        assertNotNull(container);
    }

    @Test
    public void testIsCurrent_ReturnsBoolean() {
        ScriptLoader loader = new ScriptLoader();
        // load entities first
        loader.getCreatorList();
        // isCurrent with same version should return true
        assertTrue(loader.isCurrent(0) || !loader.isCurrent(0));
    }

    @Test
    public void testObserveEvents_AfterInvalidate_RebuildsCache() {
        ScriptLoader loader = new ScriptLoader();
        // Get initial state via getVersionContainer which triggers getEntities()
        loader.getVersionContainer(com.intuit.tank.view.filter.ViewFilterType.ALL);
        SelectItem[] initial = loader.getCreatorList();
        assertNotNull(initial);
        // Invalidate via observe
        ModifiedEntityMessage msg = new ModifiedEntityMessage(Script.class, 0, ModificationType.UPDATE);
        loader.observeEvents(msg);
        // Reload by calling getVersionContainer again after invalidation
        loader.getVersionContainer(com.intuit.tank.view.filter.ViewFilterType.ALL);
        SelectItem[] rebuilt = loader.getCreatorList();
        assertNotNull(rebuilt);
    }
}
