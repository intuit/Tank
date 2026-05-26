package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterServiceUtilTest {

    @Test
    void filterToTO_convertsAllFields() {
        ScriptFilter filter = new ScriptFilter();
        filter.setId(1);
        filter.setName("MyFilter");
        filter.setProductName("Product1");

        FilterTO to = FilterServiceUtil.filterToTO(filter);

        assertEquals(1, to.getId());
        assertEquals("MyFilter", to.getName());
        assertEquals("Product1", to.getProductName());
    }

    @Test
    void filterGroupToTO_convertsAllFields() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setId(2);
        group.setName("MyGroup");
        group.setProductName("Product2");

        FilterGroupTO to = FilterServiceUtil.filterGroupToTO(group);

        assertEquals(2, to.getId());
        assertEquals("MyGroup", to.getName());
        assertEquals("Product2", to.getProductName());
    }
}
