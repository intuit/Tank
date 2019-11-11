package com.intuit.tank.util;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Date;

import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Summary
 * 
 * @author wlee5
 */
public class ModifiedDateComparatorTest {
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testClass() {
        BaseEntityImpl base1 = new BaseEntityImpl();
        BaseEntityImpl base2 = new BaseEntityImpl();
        base1.setModified(new Date());
        base2.setModified(new Date(new Date().getTime() + 200));

        ModifiedDateComparator cmptr = new ModifiedDateComparator();
        assertTrue(cmptr.compare(base1, base2) < 0);

        ModifiedDateComparator cmptr2 = new ModifiedDateComparator(ModifiedDateComparator.SortOrder.DESCENDING);
        assertTrue(cmptr2.compare(base1, base2) > 0);
    }

    /**
     * Run the ModifiedDateComparator() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:59 PM
     */
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testModifiedDateComparator_1()
            throws Exception {

        ModifiedDateComparator result = new ModifiedDateComparator();

        assertNotNull(result);
    }

    /**
     * Run the ModifiedDateComparator(SortOrder) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:59 PM
     */
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testModifiedDateComparator_2()
            throws Exception {
        ModifiedDateComparator.SortOrder order = ModifiedDateComparator.SortOrder.ASCENDING;

        ModifiedDateComparator result = new ModifiedDateComparator(order);

        assertNotNull(result);
    }

}

class BaseEntityImpl extends BaseEntity {

}