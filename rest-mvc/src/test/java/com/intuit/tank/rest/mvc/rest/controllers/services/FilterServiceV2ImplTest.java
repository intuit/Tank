package com.intuit.tank.rest.mvc.rest.controllers.services;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.dao.FilterGroupDao;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.Script;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.models.filters.*;
import com.intuit.tank.rest.mvc.rest.services.filters.FilterServiceV2Impl;
import com.intuit.tank.rest.mvc.rest.util.FilterServiceUtil;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.rest.mvc.rest.util.ScriptFilterUtil;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

public class FilterServiceV2ImplTest {

    @Test
    public void testGetFilter() {
        ScriptFilterDao mockFilterDao = mock(ScriptFilterDao.class);
        ScriptFilter mockFilter = mock(ScriptFilter.class);
        FilterTO mockFilterTO = mock(FilterTO.class);


        when(mockFilterDao.findById(anyInt())).thenReturn(mockFilter);


        try (MockedStatic<FilterServiceUtil> mockedFilterServiceUtil = Mockito.mockStatic(FilterServiceUtil.class)) {
            mockedFilterServiceUtil.when(() -> FilterServiceUtil.filterToTO(mockFilter)).thenReturn(mockFilterTO);


            FilterServiceV2Impl service = new FilterServiceV2Impl() {
                @Override
                protected ScriptFilterDao createScriptFilterDao() {
                    return mockFilterDao;
                }
            };

            FilterTO result = service.getFilter(5);
            assertNotNull(result);
            assertEquals(mockFilterTO, result);
        }
    }

    @Test
    public void testGetFilterException() {
        ScriptFilterDao mockFilterDao = mock(ScriptFilterDao.class);
        when(mockFilterDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterDao createScriptFilterDao() {
                return mockFilterDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getFilter(1));
    }

    @Test
    public void testGetFilterGroup() {
        ScriptFilterGroupDao mockFilterGroupDao = mock(ScriptFilterGroupDao.class);
        ScriptFilterGroup mockFilterGroup = mock(ScriptFilterGroup.class);
        FilterGroupTO mockFilterGroupTO = mock(FilterGroupTO.class);

        when(mockFilterGroupDao.findById(anyInt())).thenReturn(mockFilterGroup);

        try (MockedStatic<FilterServiceUtil> mockedFilterServiceUtil = Mockito.mockStatic(FilterServiceUtil.class)) {
            mockedFilterServiceUtil.when(() -> FilterServiceUtil.filterGroupToTO(mockFilterGroup)).thenReturn(mockFilterGroupTO);


            FilterServiceV2Impl service = new FilterServiceV2Impl() {
                @Override
                protected ScriptFilterGroupDao createScriptFilterGroupDao() {
                    return mockFilterGroupDao;
                }
            };

            FilterGroupTO result = service.getFilterGroup(1);
            assertNotNull(result);
            assertEquals(mockFilterGroupTO, result);
        }
    }


    @Test
    public void testGetFilterGroupException() {
        ScriptFilterGroupDao mockFilterGroupDao = mock(ScriptFilterGroupDao.class);
        when(mockFilterGroupDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterGroupDao createScriptFilterGroupDao() {
                return mockFilterGroupDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getFilterGroup(8));
    }

    @Test
    public void testGetFilters() {
        ScriptFilterDao mockFilterDao = mock(ScriptFilterDao.class);
        List<ScriptFilter> mockFilters = Arrays.asList(mock(ScriptFilter.class));
        FilterTO mockFilterTO = mock(FilterTO.class);

        when(mockFilterDao.findAll()).thenReturn(mockFilters);

        try (MockedStatic<FilterServiceUtil> mockedFilterServiceUtil = Mockito.mockStatic(FilterServiceUtil.class)) {
            mockedFilterServiceUtil.when(() -> FilterServiceUtil.filterToTO(mockFilters.get(0))).thenReturn(mockFilterTO);

            FilterServiceV2Impl service = new FilterServiceV2Impl() {
                @Override
                protected ScriptFilterDao createScriptFilterDao() {
                    return mockFilterDao;
                }
            };

            FilterContainer result = service.getFilters();
            assertNotNull(result);
            assertEquals(1, result.getFilters().size());
            assertEquals(mockFilterTO, result.getFilters().get(0));
        }
    }

    @Test
    public void testGetFiltersException() {
        ScriptFilterDao mockFilterDao = mock(ScriptFilterDao.class);
        when(mockFilterDao.findAll()).thenThrow(new RuntimeException("trigger exception"));

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterDao createScriptFilterDao() {
                return mockFilterDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, service::getFilters);
    }

    @Test
    public void testGetFilterGroups() {
        ScriptFilterGroupDao mockFilterGroupDao = mock(ScriptFilterGroupDao.class);
        List<ScriptFilterGroup> mockFilterGroups = Arrays.asList(mock(ScriptFilterGroup.class));
        FilterGroupTO mockFilterGroupTO = mock(FilterGroupTO.class);

        when(mockFilterGroupDao.findAll()).thenReturn(mockFilterGroups);

        try (MockedStatic<FilterServiceUtil> mockedFilterServiceUtil = Mockito.mockStatic(FilterServiceUtil.class)) {
            mockedFilterServiceUtil.when(() -> FilterServiceUtil.filterGroupToTO(mockFilterGroups.get(0))).thenReturn(mockFilterGroupTO);


            FilterServiceV2Impl service = new FilterServiceV2Impl() {
                @Override
                protected ScriptFilterGroupDao createScriptFilterGroupDao() {
                    return mockFilterGroupDao;
                }
            };


            FilterGroupContainer result = service.getFilterGroups();
            assertNotNull(result);
            assertEquals(1, result.getFilterGroups().size());
            assertEquals(mockFilterGroupTO, result.getFilterGroups().get(0));
        }
    }


    @Test
    public void testGetFilterGroupsException() {
        ScriptFilterGroupDao mockFilterGroupDao = mock(ScriptFilterGroupDao.class);
        when(mockFilterGroupDao.findAll()).thenThrow(new RuntimeException("trigger exception"));


        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterGroupDao createScriptFilterGroupDao() {
                return mockFilterGroupDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, service::getFilterGroups);
    }

    @Test
    public void testApplyFilters() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        FilterGroupDao mockFilterGroupDao = mock(FilterGroupDao.class);
        Script mockScript = mock(Script.class);
        ScriptFilterGroup mockScriptFilterGroup = mock(ScriptFilterGroup.class);
        ScriptFilter mockScriptFilter = mock(ScriptFilter.class);


        when(mockScriptDao.findById(anyInt())).thenReturn(mockScript);
        when(mockFilterGroupDao.findById(anyInt())).thenReturn(mockScriptFilterGroup);
        when(mockScriptFilterGroup.getFilters()).thenReturn(Collections.singleton(mockScriptFilter));
        when(mockScriptFilter.getId()).thenReturn(1);
        when(mockScriptDao.saveOrUpdate(any())).thenReturn(mockScript);

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
            @Override
            protected FilterGroupDao createFilterGroupDao() {
                return mockFilterGroupDao;
            }
            @Override
            protected void applyFilters(List<Integer> filterIds, Script script) {
                // do nothing
            }
            @Override
            protected void sendMsg(BaseEntity entity, ModificationType type) {
                // do nothing
            }
            @Override
            protected void setScriptStepLabels(Script script) {
                // do nothing
            }
        };

        ApplyFiltersRequest request = new ApplyFiltersRequest("2", Collections.singletonList(1), Collections.singletonList(1));

        String result = service.applyFilters(2, request);

        assertEquals("Filters applied", result);
    }


    @Test
    public void testApplyFiltersScriptNotFound() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);

        when(mockScriptDao.findById(anyInt())).thenReturn(null);

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        ApplyFiltersRequest request = new ApplyFiltersRequest("1", Collections.singletonList(1), Collections.singletonList(1));

        String result = service.applyFilters(1, request);

        assertEquals("Script with that script ID does not exist", result);
    }


    @Test
    public void testApplyFiltersException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);

        when(mockScriptDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        ApplyFiltersRequest request = new ApplyFiltersRequest("1", Collections.singletonList(1), Collections.singletonList(1));

        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.applyFilters(1, request));
    }

    @Test
    public void testDeleteFilter() {
        ScriptFilterDao mockFilterDao = mock(ScriptFilterDao.class);
        ScriptFilter mockFilter = mock(ScriptFilter.class);

        when(mockFilterDao.findById(anyInt())).thenReturn(mockFilter);

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterDao createScriptFilterDao() {
                return mockFilterDao;
            }
        };

        String result = service.deleteFilter(8);

        assertEquals("", result);
    }


    @Test
    public void testDeleteFilterNotFound() {
        ScriptFilterDao mockFilterDao = mock(ScriptFilterDao.class);

        when(mockFilterDao.findById(anyInt())).thenReturn(null);

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterDao createScriptFilterDao() {
                return mockFilterDao;
            }
        };

        String result = service.deleteFilter(9);

        assertEquals("Filter with filter id 9 does not exist", result);
    }


    @Test
    public void testDeleteFilterException() {
        ScriptFilterDao mockFilterDao = mock(ScriptFilterDao.class);

        when(mockFilterDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterDao createScriptFilterDao() {
                return mockFilterDao;
            }
        };

        assertThrows(GenericServiceDeleteException.class, () -> service.deleteFilter(5));
    }


    @Test
    public void testDeleteFilterGroup() {
        ScriptFilterGroupDao mockFilterGroupDao = mock(ScriptFilterGroupDao.class);
        ScriptFilterGroup mockFilterGroup = mock(ScriptFilterGroup.class);

        when(mockFilterGroupDao.findById(anyInt())).thenReturn(mockFilterGroup);

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterGroupDao createScriptFilterGroupDao() {
                return mockFilterGroupDao;
            }
        };

        String result = service.deleteFilterGroup(12);

        assertEquals("", result);
    }


    @Test
    public void testDeleteFilterGroupNotFound() {
        ScriptFilterGroupDao mockFilterGroupDao = mock(ScriptFilterGroupDao.class);

        when(mockFilterGroupDao.findById(anyInt())).thenReturn(null);

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterGroupDao createScriptFilterGroupDao() {
                return mockFilterGroupDao;
            }
        };

        String result = service.deleteFilterGroup(11);

        assertEquals("Filter Group with filter group id 11 does not exist", result);
    }


    @Test
    public void testDeleteFilterGroupException() {
        ScriptFilterGroupDao mockFilterGroupDao = mock(ScriptFilterGroupDao.class);

        when(mockFilterGroupDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        FilterServiceV2Impl service = new FilterServiceV2Impl() {
            @Override
            protected ScriptFilterGroupDao createScriptFilterGroupDao() {
                return mockFilterGroupDao;
            }
        };

        assertThrows(GenericServiceDeleteException.class, () -> service.deleteFilterGroup(34));
    }

}
