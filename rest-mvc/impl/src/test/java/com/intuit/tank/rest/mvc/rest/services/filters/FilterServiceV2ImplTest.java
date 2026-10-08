package com.intuit.tank.rest.mvc.rest.services.filters;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intuit.tank.project.User;

import com.intuit.tank.dao.UserDao;

import java.util.Map;
import org.mockito.ArgumentCaptor;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.FilterGroupDao;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.filters.models.*;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;
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
    void createOrUpdateFilter_recordsCallerAsCreator_ignoringRequestCreator() {
        SecurityTestSupport.useConfig(true, Map.of(AccessRight.CREATE_FILTER, List.of("filterers")));
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice", "filterers"));
        FilterTO request = FilterTO.builder().withName("new-filter").withCreator("mallory").build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.saveOrUpdate(any(ScriptFilter.class))).thenAnswer(i -> i.getArgument(0)))) {

            service.createOrUpdateFilter(request);

            ArgumentCaptor<ScriptFilter> saved = ArgumentCaptor.forClass(ScriptFilter.class);
            verify(daoMock.constructed().get(0)).saveOrUpdate(saved.capture());
            assertEquals("alice", saved.getValue().getCreator());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void createOrUpdateFilter_forbiddenWithoutCreateRight() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("bob"));
        try {
            assertThrows(GenericServiceForbiddenAccessException.class,
                    () -> service.createOrUpdateFilter(FilterTO.builder().withName("f").build()));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void createOrUpdateFilter_updateKeepsOwnerAndRequiresEditRightOrOwnership() {
        SecurityTestSupport.useConfig(true, Map.of());
        ScriptFilter existing = new ScriptFilter();
        existing.setCreator("alice");
        FilterTO request = FilterTO.builder().withId(4).withName("renamed").withCreator("bob").build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> {
                    when(mock.findById(4)).thenReturn(existing);
                    when(mock.saveOrUpdate(any(ScriptFilter.class))).thenAnswer(i -> i.getArgument(0));
                })) {
            SecurityTestSupport.actAs(SecurityTestSupport.user("bob"));
            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.createOrUpdateFilter(request));

            SecurityTestSupport.actAs(SecurityTestSupport.user("alice"));
            service.createOrUpdateFilter(request);
            assertEquals("alice", existing.getCreator());
            assertEquals("renamed", existing.getName());
        } finally {
            SecurityTestSupport.reset();
        }
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
                (mock, ctx) -> when(mock.findById(1)).thenReturn(filter));
             MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class)) {

            String result = service.deleteFilter(1);
            assertEquals("", result);

            verify(daoMock.constructed().get(0)).delete(filter);
        }
    }

    @Test
    void deleteFilter_removesFilterFromItsGroupsFirst() {
        ScriptFilter filter = filter(1, "doomed");
        ScriptFilter other = filter(2, "kept");
        ScriptFilterGroup group = group(5, "grp", "alice", filter, other);

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(filter));
             MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                     (mock, ctx) -> when(mock.getScriptFilterGroupForFilter(1)).thenReturn(List.of(group)))) {

            assertEquals("", service.deleteFilter(1));

            ScriptFilterGroupDao groupDao = groupDaoMock.constructed().get(0);
            ScriptFilterDao dao = daoMock.constructed().get(0);
            org.mockito.InOrder order = inOrder(groupDao, dao);
            order.verify(groupDao).saveOrUpdate(group);
            order.verify(dao).delete(filter);
            assertEquals(Set.of(other), group.getFilters());
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

    // =====================================================================
    // createFilter / updateFilter / copyFilter
    // =====================================================================

    @Test
    void createFilter_ownedByCaller_ignoringRequestIdAndCreator() {
        SecurityTestSupport.useConfig(true, Map.of(AccessRight.CREATE_FILTER, List.of("filterers")));
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice", "filterers"));
        FilterTO request = FilterTO.builder().withId(99).withName("new").withCreator("mallory")
                .withConditions(List.of(FilterConditionTO.builder().withScope("host").withCondition("Contains").withValue("x").build()))
                .build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.saveOrUpdate(any(ScriptFilter.class))).thenAnswer(i -> {
                    ScriptFilter f = i.getArgument(0);
                    f.setId(7);
                    return f;
                }))) {

            FilterTO result = service.createFilter(request);

            assertEquals(7, result.getId());
            assertEquals("alice", result.getCreator());
            assertEquals(1, result.getConditions().size());
            verify(daoMock.constructed().get(0), never()).findById(anyInt());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void createFilter_forbiddenWithoutCreateRight() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("bob"));
        try {
            assertThrows(GenericServiceForbiddenAccessException.class,
                    () -> service.createFilter(FilterTO.builder().withName("f").build()));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void createFilter_rejectsBlankNameAndExternalFilters() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice", "admin"));
        try {
            assertThrows(GenericServiceBadRequestException.class,
                    () -> service.createFilter(FilterTO.builder().withName(" ").build()));
            assertThrows(GenericServiceBadRequestException.class,
                    () -> service.createFilter(FilterTO.builder().withName("x".repeat(256)).build()));
            assertThrows(GenericServiceBadRequestException.class, () -> service.createFilter(
                    FilterTO.builder().withName("ext").withFilterType(ScriptFilterType.EXTERNAL.name()).build()));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void createFilter_requiresSignedInUser() {
        SecurityTestSupport.useConfig(false, Map.of());
        try {
            assertThrows(GenericServiceUnauthorizedException.class,
                    () -> service.createFilter(FilterTO.builder().withName("f").build()));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilter_ownerSavesAndKeepsOwner() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice"));
        ScriptFilter existing = filter(4, "old");
        existing.setCreator("alice");
        existing.setModified(new Date(1_000_000L));
        FilterTO request = FilterTO.builder().withName("renamed")
                .withModified(new Date(1_000_400L)).build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> {
                    when(mock.findById(4)).thenReturn(existing);
                    when(mock.saveOrUpdate(any(ScriptFilter.class))).thenAnswer(i -> i.getArgument(0));
                })) {

            FilterTO result = service.updateFilter(4, request);

            assertEquals("renamed", result.getName());
            assertEquals("alice", result.getCreator());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilter_forbiddenForNonOwnerWithoutEditRight() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("bob"));
        ScriptFilter existing = filter(4, "old");
        existing.setCreator("alice");
        existing.setModified(new Date(1_000_000L));

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(4)).thenReturn(existing))) {

            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.updateFilter(4,
                    FilterTO.builder().withName("x").withModified(new Date(1_000_000L)).build()));
            verify(daoMock.constructed().get(0), never()).saveOrUpdate(any());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilter_staleOrMissingModifiedIsRejected() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice", "admin"));
        ScriptFilter existing = filter(4, "old");
        existing.setModified(new Date(5_000_000L));

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(4)).thenReturn(existing))) {

            assertThrows(GenericServiceBadRequestException.class,
                    () -> service.updateFilter(4, FilterTO.builder().withName("x").build()));
            assertThrows(GenericServiceConflictException.class, () -> service.updateFilter(4,
                    FilterTO.builder().withName("x").withModified(new Date(4_000_000L)).build()));
            verify(daoMock.constructed().get(0), never()).saveOrUpdate(any());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilter_notFound() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice", "admin"));
        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class)) {
            assertThrows(GenericServiceResourceNotFoundException.class,
                    () -> service.updateFilter(404, FilterTO.builder().withName("x").build()));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilter_ownerOrAdminGivesItToAnotherUser() {
        SecurityTestSupport.useConfig(true, Map.of(AccessRight.EDIT_FILTER, List.of("editors")));
        ScriptFilter existing = filter(4, "f");
        existing.setCreator("alice");
        existing.setModified(new Date(1_000_000L));
        FilterTO toBob = FilterTO.builder().withName("f").withCreator("bob").withModified(new Date(1_000_000L)).build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> {
                    when(mock.findById(4)).thenReturn(existing);
                    when(mock.saveOrUpdate(any(ScriptFilter.class))).thenAnswer(i -> i.getArgument(0));
                });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> when(mock.findByUserName("bob")).thenReturn(new User()))) {
            // an editor may change the filter but not give it away
            SecurityTestSupport.actAs(SecurityTestSupport.user("carol", "editors"));
            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.updateFilter(4, toBob));
            assertEquals("alice", existing.getCreator());

            SecurityTestSupport.actAs(SecurityTestSupport.user("alice"));
            assertEquals("bob", service.updateFilter(4, toBob).getCreator());

            existing.setCreator("alice");
            SecurityTestSupport.actAs(SecurityTestSupport.user("dave", "admin"));
            assertEquals("bob", service.updateFilter(4, toBob).getCreator());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilter_rejectsAnUnknownNewOwner() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice"));
        ScriptFilter existing = filter(4, "f");
        existing.setCreator("alice");
        existing.setModified(new Date(1_000_000L));

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findById(4)).thenReturn(existing));
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class)) {
            GenericServiceBadRequestException e = assertThrows(GenericServiceBadRequestException.class,
                    () -> service.updateFilter(4, FilterTO.builder().withName("f").withCreator("nobody")
                            .withModified(new Date(1_000_000L)).build()));
            assertTrue(e.getMessage().contains("no user named nobody"), e.getMessage());
            assertEquals("alice", existing.getCreator());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilterGroup_ownerGivesItToAnotherUser() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice"));
        ScriptFilterGroup existing = group(5, "grp", "alice");
        existing.setModified(new Date(2_000_000L));
        FilterGroupTO request = FilterGroupTO.builder().withName("grp").withCreator("bob")
                .withModified(new Date(2_000_000L)).withFilterIds(List.of()).build();

        try (MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> {
                    when(mock.findById(5)).thenReturn(existing);
                    when(mock.saveOrUpdate(any(ScriptFilterGroup.class))).thenAnswer(i -> i.getArgument(0));
                });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> when(mock.findByUserName("bob")).thenReturn(new User()))) {
            assertEquals("bob", service.updateFilterGroup(5, request).getCreator());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void copyFilter_copiesConditionsAndActionsAsNewRowsOwnedByCaller() {
        SecurityTestSupport.useConfig(true, Map.of(AccessRight.CREATE_FILTER, List.of("filterers")));
        SecurityTestSupport.actAs(SecurityTestSupport.user("bob", "filterers"));
        ScriptFilter source = filter(3, "orig");
        source.setCreator("alice");
        source.setProductName("prod");
        source.setAllConditionsMustPass(true);
        ScriptFilterCondition condition = new ScriptFilterCondition();
        condition.setId(30);
        condition.setScope("host");
        condition.setCondition("Contains");
        condition.setValue("example");
        source.addCondition(condition);
        ScriptFilterAction action = new ScriptFilterAction();
        action.setId(31);
        action.setAction(com.intuit.tank.vm.api.enumerated.ScriptFilterActionType.replace);
        action.setScope("host");
        action.setValue("other");
        source.addAction(action);

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> {
                    when(mock.findById(3)).thenReturn(source);
                    when(mock.saveOrUpdate(any(ScriptFilter.class))).thenAnswer(i -> i.getArgument(0));
                })) {

            FilterTO result = service.copyFilter(3, new CopyRequest("  copy  "));

            ArgumentCaptor<ScriptFilter> saved = ArgumentCaptor.forClass(ScriptFilter.class);
            verify(daoMock.constructed().get(daoMock.constructed().size() - 1)).saveOrUpdate(saved.capture());
            ScriptFilter copy = saved.getValue();
            assertNotSame(source, copy);
            assertEquals(0, copy.getId());
            assertEquals("copy", copy.getName());
            assertEquals("bob", copy.getCreator());
            assertEquals("prod", copy.getProductName());
            assertTrue(copy.getAllConditionsMustPass());
            assertEquals(1, copy.getConditions().size());
            assertEquals(0, copy.getConditions().iterator().next().getId());
            assertEquals("example", copy.getConditions().iterator().next().getValue());
            assertEquals(1, copy.getActions().size());
            assertEquals(0, copy.getActions().iterator().next().getId());
            assertEquals("bob", result.getCreator());
            assertEquals("alice", source.getCreator());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void copyFilter_requiresNameAndCreateRight() {
        SecurityTestSupport.useConfig(true, Map.of());
        try {
            SecurityTestSupport.actAs(SecurityTestSupport.user("bob"));
            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.copyFilter(3, new CopyRequest("c")));

            SecurityTestSupport.actAs(SecurityTestSupport.user("admin", "admin"));
            assertThrows(GenericServiceBadRequestException.class, () -> service.copyFilter(3, new CopyRequest(" ")));
            assertThrows(GenericServiceBadRequestException.class, () -> service.copyFilter(3, null));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    // =====================================================================
    // createFilterGroup / updateFilterGroup / copyFilterGroup
    // =====================================================================

    @Test
    void createFilterGroup_resolvesMembersAndIsOwnedByCaller() {
        SecurityTestSupport.useConfig(true, Map.of(AccessRight.CREATE_FILTER, List.of("filterers")));
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice", "filterers"));
        ScriptFilter f1 = filter(1, "a");
        ScriptFilter f2 = filter(2, "b");
        FilterGroupTO request = FilterGroupTO.builder().withId(77).withName(" grp ").withProductName("prod")
                .withCreator("mallory").withFilterIds(java.util.Arrays.asList(2, 1, 2, null)).build();
        List<String> saves = new java.util.ArrayList<>();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findForIds(List.of(2, 1))).thenReturn(List.of(f2, f1)));
             MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                     (mock, ctx) -> when(mock.saveOrUpdate(any(ScriptFilterGroup.class))).thenAnswer(i -> {
                         ScriptFilterGroup g = i.getArgument(0);
                         saves.add((g.getId() == 0 ? "new" : "existing") + " with " + g.getFilters().size() + " filters");
                         g.setId(8);
                         return g;
                     }))) {

            FilterGroupDetailTO result = service.createFilterGroup(request);

            // persisting a new group with filters from another session fails ("detached entity passed to persist")
            assertEquals(List.of("new with 0 filters", "existing with 2 filters"), saves);

            assertEquals(8, result.getId());
            assertEquals("grp", result.getName());
            assertEquals("alice", result.getCreator());
            assertEquals(List.of(1, 2), result.getFilterIds());
            assertEquals(2, result.getFilters().size());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void createFilterGroup_rejectsUnknownFilterIds() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("alice", "admin"));
        FilterGroupTO request = FilterGroupTO.builder().withName("grp").withFilterIds(List.of(1, 404)).build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findForIds(anyList())).thenReturn(List.of(filter(1, "a"))));
             MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class)) {

            GenericServiceBadRequestException e = assertThrows(GenericServiceBadRequestException.class,
                    () -> service.createFilterGroup(request));
            assertTrue(e.getMessage().contains("404"));
            assertTrue(groupDaoMock.constructed().isEmpty());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void createFilterGroup_forbiddenWithoutCreateRight() {
        SecurityTestSupport.useConfig(true, Map.of());
        SecurityTestSupport.actAs(SecurityTestSupport.user("bob"));
        try {
            assertThrows(GenericServiceForbiddenAccessException.class,
                    () -> service.createFilterGroup(FilterGroupTO.builder().withName("g").build()));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilterGroup_replacesMembersAndKeepsOwner() {
        SecurityTestSupport.useConfig(true, Map.of(AccessRight.EDIT_FILTER, List.of("editors")));
        SecurityTestSupport.actAs(SecurityTestSupport.user("bob", "editors"));
        ScriptFilterGroup existing = group(5, "grp", "alice", filter(1, "a"));
        existing.setModified(new Date(2_000_000L));
        ScriptFilter f3 = filter(3, "c");
        FilterGroupTO request = FilterGroupTO.builder().withName("renamed")
                .withModified(new Date(2_000_000L)).withFilterIds(List.of(3)).build();

        try (MockedConstruction<ScriptFilterDao> daoMock = Mockito.mockConstruction(ScriptFilterDao.class,
                (mock, ctx) -> when(mock.findForIds(List.of(3))).thenReturn(List.of(f3)));
             MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                     (mock, ctx) -> {
                         when(mock.findById(5)).thenReturn(existing);
                         when(mock.saveOrUpdate(any(ScriptFilterGroup.class))).thenAnswer(i -> i.getArgument(0));
                     })) {

            FilterGroupDetailTO result = service.updateFilterGroup(5, request);

            assertEquals("renamed", result.getName());
            assertEquals("alice", result.getCreator());
            assertEquals(List.of(3), result.getFilterIds());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void updateFilterGroup_forbiddenStaleAndNotFound() {
        SecurityTestSupport.useConfig(true, Map.of());
        ScriptFilterGroup existing = group(5, "grp", "alice");
        existing.setModified(new Date(2_000_000L));

        try (MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> when(mock.findById(5)).thenReturn(existing))) {
            SecurityTestSupport.actAs(SecurityTestSupport.user("bob"));
            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.updateFilterGroup(5,
                    FilterGroupTO.builder().withName("g").withModified(new Date(2_000_000L)).build()));

            SecurityTestSupport.actAs(SecurityTestSupport.user("alice"));
            assertThrows(GenericServiceConflictException.class, () -> service.updateFilterGroup(5,
                    FilterGroupTO.builder().withName("g").withModified(new Date(1_000_000L)).build()));
            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.updateFilterGroup(6,
                    FilterGroupTO.builder().withName("g").withModified(new Date(2_000_000L)).build()));
        } finally {
            SecurityTestSupport.reset();
        }
    }

    @Test
    void copyFilterGroup_holdsSameFiltersOwnedByCaller() {
        SecurityTestSupport.useConfig(true, Map.of(AccessRight.CREATE_FILTER, List.of("filterers")));
        SecurityTestSupport.actAs(SecurityTestSupport.user("bob", "filterers"));
        ScriptFilter f1 = filter(1, "a");
        ScriptFilterGroup source = group(5, "grp", "alice", f1);
        source.setProductName("prod");

        try (MockedConstruction<ScriptFilterGroupDao> groupDaoMock = Mockito.mockConstruction(ScriptFilterGroupDao.class,
                (mock, ctx) -> {
                    when(mock.findById(5)).thenReturn(source);
                    when(mock.saveOrUpdate(any(ScriptFilterGroup.class))).thenAnswer(i -> {
                        ScriptFilterGroup g = i.getArgument(0);
                        g.setId(9);
                        return g;
                    });
                })) {

            FilterGroupDetailTO result = service.copyFilterGroup(5, new CopyRequest("grp copy"));

            assertEquals(9, result.getId());
            assertEquals("grp copy", result.getName());
            assertEquals("bob", result.getCreator());
            assertEquals("prod", result.getProductName());
            assertEquals(List.of(1), result.getFilterIds());
            assertEquals(5, source.getId());
            assertEquals("alice", source.getCreator());
        } finally {
            SecurityTestSupport.reset();
        }
    }

    private static ScriptFilter filter(int id, String name) {
        ScriptFilter filter = new ScriptFilter();
        filter.setId(id);
        filter.setName(name);
        filter.setFilterType(ScriptFilterType.INTERNAL);
        return filter;
    }

    private static ScriptFilterGroup group(int id, String name, String creator, ScriptFilter... filters) {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setId(id);
        group.setName(name);
        group.setCreator(creator);
        group.setFilters(new HashSet<>(List.of(filters)));
        return group;
    }
}
