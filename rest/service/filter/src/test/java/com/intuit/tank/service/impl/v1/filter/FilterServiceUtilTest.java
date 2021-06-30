package com.intuit.tank.service.impl.v1.filter;

import com.intuit.tank.api.model.v1.filter.FilterGroupTO;
import com.intuit.tank.project.ScriptFilterGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FilterServiceUtilTest {

    @Test
    public void testFilterGroupToTO() {
        ScriptFilterGroup scriptFilterGroup = new ScriptFilterGroup();
        scriptFilterGroup.setId(101);
        scriptFilterGroup.setName("FilterGroupName");
        scriptFilterGroup.setProductName("ProductName");

        FilterGroupTO filterGroupTO = FilterServiceUtil.filterGroupToTO(scriptFilterGroup);

        assertNotNull(filterGroupTO);
        assertEquals(101, filterGroupTO.getId());
        assertEquals("FilterGroupName", filterGroupTO.getName());
        assertEquals("ProductName", filterGroupTO.getProductName());
    }
}
