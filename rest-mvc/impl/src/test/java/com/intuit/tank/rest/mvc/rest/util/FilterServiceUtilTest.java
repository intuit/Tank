package com.intuit.tank.rest.mvc.rest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.filters.models.FilterGroupDetailTO;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterServiceUtilTest {

    @Test
    void filterToTO_convertsAllFields() {
        ScriptFilter filter = new ScriptFilter();
        filter.setId(1);
        filter.setName("MyFilter");
        filter.setProductName("Product1");
        filter.setCreator("owner");
        filter.setCreated(new Date(1000));
        filter.setModified(new Date(2000));
        filter.setPersist(false);
        filter.setAllConditionsMustPass(true);
        filter.setFilterType(ScriptFilterType.INTERNAL);

        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setScope("path");
        condition.setCondition("contains");
        condition.setValue("/print");
        filter.addCondition(condition);

        ScriptFilterAction action = new ScriptFilterAction();
        action.setAction(ScriptFilterActionType.replace);
        action.setScope("queryString");
        action.setKey("printType");
        action.setValue("4F");
        filter.addAction(action);

        FilterTO to = FilterServiceUtil.filterToTO(filter);

        assertEquals(1, to.getId());
        assertEquals("MyFilter", to.getName());
        assertEquals("Product1", to.getProductName());
        assertEquals("owner", to.getCreator());
        assertFalse(to.isPersist());
        assertTrue(to.isAllConditionsMustPass());
        assertEquals("INTERNAL", to.getFilterType());
        assertEquals("path", to.getConditions().get(0).getScope());
        assertEquals("contains", to.getConditions().get(0).getCondition());
        assertEquals("/print", to.getConditions().get(0).getValue());
        assertEquals(ScriptFilterActionType.replace, to.getActions().get(0).getAction());
        assertEquals("queryString", to.getActions().get(0).getScope());
        assertEquals("printType", to.getActions().get(0).getKey());
        assertEquals("4F", to.getActions().get(0).getValue());
    }

    @Test
    void toScriptFilter_replacesCompleteDefinition() {
        FilterTO request = FilterTO.builder()
                .withName("Imported")
                .withProductName("Product")
                .withCreator("sync-user")
                .withPersist(true)
                .withAllConditionsMustPass(false)
                .withFilterType("INTERNAL")
                .withConditions(List.of(com.intuit.tank.filters.models.FilterConditionTO.builder()
                        .withScope("path")
                        .withCondition("contains")
                        .withValue("/checkout")
                        .build()))
                .withActions(List.of(com.intuit.tank.filters.models.FilterActionTO.builder()
                        .withAction(ScriptFilterActionType.add)
                        .withScope("requestHeaders")
                        .withKey("X-Test")
                        .withValue("true")
                        .build()))
                .build();

        ScriptFilter result = FilterServiceUtil.toScriptFilter(request, new ScriptFilter());

        assertEquals("Imported", result.getName());
        assertEquals("sync-user", result.getCreator());
        assertEquals(1, result.getConditions().size());
        assertEquals("/checkout", result.getConditions().iterator().next().getValue());
        assertEquals(1, result.getActions().size());
        assertEquals("X-Test", result.getActions().iterator().next().getKey());
    }

    @Test
    void filterGroupToTO_convertsAllFields() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setId(2);
        group.setName("MyGroup");
        group.setProductName("Product2");
        group.setCreator("owner");
        group.setCreated(new Date(1000));
        group.setModified(new Date(2000));

        ScriptFilter secondFilter = new ScriptFilter();
        secondFilter.setId(9);
        secondFilter.setName("Second");
        secondFilter.setFilterType(ScriptFilterType.INTERNAL);
        ScriptFilter firstFilter = new ScriptFilter();
        firstFilter.setId(3);
        firstFilter.setName("First");
        firstFilter.setFilterType(ScriptFilterType.INTERNAL);
        group.addFilter(secondFilter);
        group.addFilter(firstFilter);

        FilterGroupTO to = FilterServiceUtil.filterGroupToTO(group);

        assertEquals(2, to.getId());
        assertEquals("MyGroup", to.getName());
        assertEquals("Product2", to.getProductName());
        assertEquals("owner", to.getCreator());
        assertEquals(new Date(1000), to.getCreated());
        assertEquals(new Date(2000), to.getModified());
        assertEquals(List.of(3, 9), to.getFilterIds());
    }

    @Test
    void filterGroupToDetailTO_includesFullFiltersInIdOrder() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setId(2);
        group.setName("MyGroup");

        ScriptFilter secondFilter = new ScriptFilter();
        secondFilter.setId(9);
        secondFilter.setName("Second");
        secondFilter.setFilterType(ScriptFilterType.INTERNAL);
        ScriptFilter firstFilter = new ScriptFilter();
        firstFilter.setId(3);
        firstFilter.setName("First");
        firstFilter.setFilterType(ScriptFilterType.INTERNAL);
        group.addFilter(secondFilter);
        group.addFilter(firstFilter);

        FilterGroupDetailTO detail = FilterServiceUtil.filterGroupToDetailTO(group);

        assertEquals(List.of(3, 9), detail.getFilterIds());
        assertEquals(List.of(3, 9), detail.getFilters().stream().map(FilterTO::getId).toList());
        assertEquals("First", detail.getFilters().get(0).getName());
        assertNotNull(detail.getFilters().get(0).getConditions());
        assertNotNull(detail.getFilters().get(0).getActions());
    }

    @Test
    void filterGroupMappings_handleEmptyMembership() {
        ScriptFilterGroup group = new ScriptFilterGroup();

        assertTrue(FilterServiceUtil.filterGroupToTO(group).getFilterIds().isEmpty());
        assertTrue(FilterServiceUtil.filterGroupToDetailTO(group).getFilters().isEmpty());
    }

    @Test
    void filterGroupResponses_serializeSummaryAndDetailShapes() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        FilterGroupTO summary = FilterGroupTO.builder()
                .withId(2)
                .withFilterIds(List.of(3))
                .build();
        FilterGroupDetailTO detail = new FilterGroupDetailTO();
        detail.setId(2);
        detail.setFilterIds(List.of(3));
        detail.setFilters(List.of(FilterTO.builder()
                .withId(3)
                .withFilterType("INTERNAL")
                .withConditions(List.of())
                .withActions(List.of())
                .build()));

        JsonNode summaryJson = objectMapper.readTree(objectMapper.writeValueAsString(summary));
        JsonNode detailJson = objectMapper.readTree(objectMapper.writeValueAsString(detail));

        assertEquals(3, summaryJson.get("filterIds").get(0).asInt());
        assertFalse(summaryJson.has("filters"));
        assertEquals(3, detailJson.get("filters").get(0).get("id").asInt());
        assertTrue(detailJson.get("filters").get(0).has("conditions"));
        assertTrue(detailJson.get("filters").get(0).has("actions"));
    }
}
