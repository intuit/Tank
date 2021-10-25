package com.intuit.tank.api.model.v1.automation;

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ApplyFiltersRequestTest</code> contains tests for the class <code>{@link ApplyFiltersRequest}</code>.
 *
 * @author msreekakula
 */
public class ApplyFiltersRequestTest {


    @Test
    @Tag(TestGroups.FUNCTIONAL)
    // Default Constructor test
    public void testApplyFiltersRequest() {
        ApplyFiltersRequest applyFiltersRequest = new ApplyFiltersRequest();
        assertNull(applyFiltersRequest.getScriptId());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testApplyFiltersRequestWithScript() {
        ApplyFiltersRequest applyFiltersRequest = new ApplyFiltersRequest("test");
        assertEquals(applyFiltersRequest.getScriptId(), "test");
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testApplyFiltersRequestWithScriptAndFilters() {
        List<Integer> filters = new ArrayList<>();
        filters.add(1);
        ApplyFiltersRequest applyFiltersRequest = new ApplyFiltersRequest("test", filters, filters);
        assertEquals(applyFiltersRequest.getScriptId(), "test");
        assertEquals(applyFiltersRequest.getFilterIds().size(), 1);
        assertEquals(applyFiltersRequest.getFilterGroupIds().size(), 1);
        assertNotNull(applyFiltersRequest.toString());
    }


}
