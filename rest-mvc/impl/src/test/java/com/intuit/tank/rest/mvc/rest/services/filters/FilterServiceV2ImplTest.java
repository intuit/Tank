package com.intuit.tank.rest.mvc.rest.services.filters;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.FilterGroupDao;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.filters.models.*;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.util.FilterServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptFilterUtil;
import com.intuit.tank.util.ScriptFilterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.ServletContext;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FilterServiceV2ImplTest {

    @InjectMocks
    private FilterServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void ping_returnsPong() {
        assertTrue(service.ping().contains("PONG"));
    }

    // =====================================================================
    // getFilter
    // =====================================================================

    @Test
    void getFilter_returnsFilter() {
        ScriptFilter filter = new ScriptFilter();
        FilterTO to = FilterTO.builder().withId(1).withName("test").withProductName("prod").build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(filter));
             MockedStatic<FilterServiceUtil> utilMock = Mockito.mockStatic(FilterServiceUtil.class)) {
            utilMock.when(() -> FilterServiceUtil.filterToTO(filter)).thenReturn(to);

            FilterTO result = service.getFilter(1);
            assertNotNull(result);
        }
    }

    @Test
    void getFilter_throwsOnError() {
        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getFilter(1));
        }
    }

    // =====================================================================
    // getFilterGroup
    // =====================================================================

    @Test
    void getFilterGroup_returnsFilterGroup() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        FilterGroupDetailTO to = new FilterGroupDetailTO();
        to.setId(1);
        to.setName("grp");
        to.setProductName("prod");
        to.setFilterIds(List.of(3));
        to.setFilters(List.of(FilterTO.builder().withId(3).withName("filter").build()));

        try (MockedConstruction<ScriptFilterGroupDao> daoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(group));
             MockedStatic<FilterServiceUtil> utilMock = Mockito.mockStatic(FilterServiceUtil.class)) {
            utilMock.when(() -> FilterServiceUtil.filterGroupToDetailTO(group)).thenReturn(to);

            FilterGroupDetailTO result = service.getFilterGroup(1);
            assertNotNull(result);
            assertEquals(List.of(3), result.getFilterIds());
            assertEquals(3, result.getFilters().get(0).getId());
        }
    }

    // =====================================================================
    // getFilters
    // =====================================================================

    @Test
    void getFilters_returnsAll() {
        ScriptFilter f1 = new ScriptFilter();
        FilterTO to1 = FilterTO.builder().withId(1).withName("f1").withProductName("p").build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(List.of(f1)));
             MockedStatic<FilterServiceUtil> utilMock = Mockito.mockStatic(FilterServiceUtil.class)) {
            utilMock.when(() -> FilterServiceUtil.filterToTO(f1)).thenReturn(to1);

            FilterContainer result = service.getFilters();
            assertNotNull(result);
            assertEquals(1, result.getFilters().size());
        }
    }

    @Test
    void createOrUpdateFilter_createsCompleteFilter() {
        FilterTO request = FilterTO.builder().withName("new-filter").withCreator("sync-user").build();
        ScriptFilter filter = new ScriptFilter();
        FilterTO response = FilterTO.builder().withId(7).withName("new-filter").withCreator("sync-user").build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.saveOrUpdate(filter)).thenReturn(filter));
             MockedStatic<FilterServiceUtil> utilMock = Mockito.mockStatic(FilterServiceUtil.class)) {
            utilMock.when(() -> FilterServiceUtil.toScriptFilter(eq(request), any(ScriptFilter.class))).thenReturn(filter);
            utilMock.when(() -> FilterServiceUtil.filterToTO(filter)).thenReturn(response);

            FilterTO result = service.createOrUpdateFilter(request);

            assertEquals(7, result.getId());
            verify(daoMock.constructed().get(0)).saveOrUpdate(filter);
        }
    }

    @Test
    void createOrUpdateFilter_updatesExistingFilter() {
        FilterTO request = FilterTO.builder().withId(4).withName("updated").build();
        ScriptFilter existing = new ScriptFilter();
        FilterTO response = FilterTO.builder().withId(4).withName("updated").build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> {
                    when(mock.findById(4)).thenReturn(existing);
                    when(mock.saveOrUpdate(existing)).thenReturn(existing);
                });
             MockedStatic<FilterServiceUtil> utilMock = Mockito.mockStatic(FilterServiceUtil.class)) {
            utilMock.when(() -> FilterServiceUtil.toScriptFilter(request, existing)).thenReturn(existing);
            utilMock.when(() -> FilterServiceUtil.filterToTO(existing)).thenReturn(response);

            FilterTO result = service.createOrUpdateFilter(request);

            assertEquals(4, result.getId());
            verify(daoMock.constructed().get(0)).findById(4);
        }
    }

    @Test
    void createOrUpdateFilter_rejectsCreateWithoutCreator() {
        FilterTO request = FilterTO.builder().withName("invalid").build();

        assertThrows(GenericServiceCreateOrUpdateException.class,
                () -> service.createOrUpdateFilter(request));
    }

    @Test
    void createOrUpdateFilter_rejectsExternalFilter() {
        FilterTO request = FilterTO.builder()
                .withName("external")
                .withCreator("sync-user")
                .withFilterType(ScriptFilterType.EXTERNAL.name())
                .build();

        assertThrows(GenericServiceCreateOrUpdateException.class,
                () -> service.createOrUpdateFilter(request));
    }

    // =====================================================================
    // getFilterGroups
    // =====================================================================

    @Test
    void getFilterGroups_returnsAll() {
        ScriptFilterGroup g1 = new ScriptFilterGroup();
        FilterGroupTO to1 = FilterGroupTO.builder().withId(1).withName("g1").withProductName("p").build();

        try (MockedConstruction<ScriptFilterGroupDao> daoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(List.of(g1)));
             MockedStatic<FilterServiceUtil> utilMock = Mockito.mockStatic(FilterServiceUtil.class)) {
            utilMock.when(() -> FilterServiceUtil.filterGroupToTO(g1)).thenReturn(to1);

            FilterGroupContainer result = service.getFilterGroups();
            assertNotNull(result);
            assertEquals(1, result.getFilterGroups().size());
        }
    }

    // =====================================================================
    // applyFilters
    // =====================================================================

    @Test
    void applyFilters_appliesFiltersToScript() {
        Script script = new Script();
        script.setId(1);
        script.setName("TestScript");
        script.setCreated(new Date());
        script.setModified(new Date());

        ApplyFiltersRequest request = new ApplyFiltersRequest(null, List.of(10, 20), List.of());

        MessageEventSender mockSender = mock(MessageEventSender.class);

        try (MockedConstruction<ScriptDao> scriptDaoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> {
                    when(mock.findById(1)).thenReturn(script);
                    when(mock.saveOrUpdate(any(Script.class))).thenReturn(script);
                });
             MockedConstruction<FilterGroupDao> fgDaoMock = Mockito.mockConstruction(FilterGroupDao.class);
             MockedStatic<ScriptFilterUtil> filterUtilMock = Mockito.mockStatic(ScriptFilterUtil.class);
             MockedStatic<ScriptUtil> scriptUtilMock = Mockito.mockStatic(ScriptUtil.class);
             MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(MessageEventSender.class)))
                        .thenReturn(mockSender))) {

            String result = service.applyFilters(1, request);
            assertEquals("Filters applied", result);
        }
    }

    @Test
    void applyFilters_returnsMessageWhenScriptNotFound() {
        ApplyFiltersRequest request = new ApplyFiltersRequest(null, List.of(10), List.of());

        try (MockedConstruction<ScriptDao> scriptDaoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            String result = service.applyFilters(999, request);
            assertTrue(result.contains("does not exist"));
        }
    }

    @Test
    void applyFilters_withFilterGroups_flattensAndApplies() {
        Script script = new Script();
        script.setId(1);
        script.setName("Test");
        script.setCreated(new Date());
        script.setModified(new Date());

        ScriptFilter groupFilter = new ScriptFilter();
        groupFilter.setId(30);
        Set<ScriptFilter> filters = new HashSet<>();
        filters.add(groupFilter);

        com.intuit.tank.project.ScriptFilterGroup filterGroup = new com.intuit.tank.project.ScriptFilterGroup();
        filterGroup.setFilters(filters);

        ApplyFiltersRequest request = new ApplyFiltersRequest(null, List.of(), List.of(5));

        MessageEventSender mockSender = mock(MessageEventSender.class);

        try (MockedConstruction<ScriptDao> scriptDaoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> {
                    when(mock.findById(1)).thenReturn(script);
                    when(mock.saveOrUpdate(any(Script.class))).thenReturn(script);
                });
             MockedConstruction<FilterGroupDao> fgDaoMock = Mockito.mockConstruction(FilterGroupDao.class,
                (mock, ctx) -> when(mock.findById(5)).thenReturn(filterGroup));
             MockedStatic<ScriptFilterUtil> filterUtilMock = Mockito.mockStatic(ScriptFilterUtil.class);
             MockedStatic<ScriptUtil> scriptUtilMock = Mockito.mockStatic(ScriptUtil.class);
             MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(MessageEventSender.class)))
                        .thenReturn(mockSender))) {

            String result = service.applyFilters(1, request);
            assertEquals("Filters applied", result);
            filterUtilMock.verify(() -> ScriptFilterUtil.applyFilters(anyList(), eq(script)));
        }
    }

    @Test
    void applyFilters_returnsNullWhenScriptIdNull() {
        ApplyFiltersRequest request = new ApplyFiltersRequest(null, List.of(1), List.of());

        String result = service.applyFilters(null, request);
        assertNull(result);
    }

    // =====================================================================
    // deleteFilter
    // =====================================================================

    @Test
    void deleteFilter_deletesExisting() {
        ScriptFilter filter = new ScriptFilter();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(filter))) {

            String result = service.deleteFilter(1);
            assertEquals("", result);

            verify(daoMock.constructed().get(0)).delete(filter);
        }
    }

    @Test
    void deleteFilter_returnsMessageWhenNotFound() {
        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            String result = service.deleteFilter(999);
            assertTrue(result.contains("does not exist"));
        }
    }

    @Test
    void deleteFilter_throwsOnError() {
        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceDeleteException.class, () -> service.deleteFilter(1));
        }
    }

    // =====================================================================
    // deleteFilterGroup
    // =====================================================================

    @Test
    void deleteFilterGroup_deletesExisting() {
        ScriptFilterGroup group = new ScriptFilterGroup();

        try (MockedConstruction<ScriptFilterGroupDao> daoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(group))) {

            String result = service.deleteFilterGroup(1);
            assertEquals("", result);

            verify(daoMock.constructed().get(0)).delete(group);
        }
    }

    @Test
    void deleteFilterGroup_returnsMessageWhenNotFound() {
        try (MockedConstruction<ScriptFilterGroupDao> daoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            String result = service.deleteFilterGroup(999);
            assertTrue(result.contains("does not exist"));
        }
    }

    @Test
    void deleteFilterGroup_throwsOnError() {
        try (MockedConstruction<ScriptFilterGroupDao> daoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceDeleteException.class, () -> service.deleteFilterGroup(1));
        }
    }
}
