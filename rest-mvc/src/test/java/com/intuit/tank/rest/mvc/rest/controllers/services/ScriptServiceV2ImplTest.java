package com.intuit.tank.rest.mvc.rest.controllers.services;

import com.intuit.tank.api.model.v1.script.*;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2Impl;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptServiceV2Impl;
import com.intuit.tank.rest.mvc.rest.util.ProjectServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

public class ScriptServiceV2ImplTest {

    @InjectMocks
    private ScriptServiceV2Impl scriptService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPing() {
        String result = scriptService.ping();
        assertTrue(result.contains("PONG ScriptServiceV2"));
    }

    @Test
    public void testCreateScript() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        Script mockScript = mock(Script.class);

        when(mockScriptDao.saveOrUpdate(any())).thenReturn(mockScript);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        ScriptTO scriptTo = new ScriptTO();
        scriptTo.setCreated(new Date());
        scriptTo.setModified(new Date());
        scriptTo.setId(7);

        ScriptTO result = service.createScript(scriptTo);
        assertNotNull(result);
    }

    @Test
    public void testCreateScriptException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        when(mockScriptDao.saveOrUpdate(any())).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        ScriptTO scriptTo = new ScriptTO();

        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.createScript(scriptTo));
    }

    @Test
    public void testGetScript() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        Script mockScript = mock(Script.class);

        when(mockScriptDao.findById(67)).thenReturn(mockScript);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        ScriptDescription result = service.getScript(67);
        assertNotNull(result);
    }

    @Test
    public void testGetScriptException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);

        when(mockScriptDao.findById(12)).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getScript(12));
    }

    @Test
    public void testGetScripts() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        List<Script> scripts = Arrays.asList(mock(Script.class), mock(Script.class), mock(Script.class));

        when(mockScriptDao.findAll()).thenReturn(scripts);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        ScriptDescriptionContainer result = service.getScripts();
        assertNotNull(result);
        assertEquals(3, result.getScripts().size());
    }


    @Test
    public void testGetScriptsException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        when(mockScriptDao.findAll()).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };
        assertThrows(GenericServiceResourceNotFoundException.class, service::getScripts);
    }

    @Test
    public void testGetAllScriptNames() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        Script mockScript1 = mock(Script.class);
        Script mockScript2 = mock(Script.class);
        Script mockScript3 = mock(Script.class);
        Script mockScript4 = mock(Script.class);

        when(mockScript1.getId()).thenReturn(1);
        when(mockScript1.getName()).thenReturn("testScript1");
        when(mockScript2.getId()).thenReturn(2);
        when(mockScript2.getName()).thenReturn("testScript2");
        when(mockScript3.getId()).thenReturn(4);
        when(mockScript3.getName()).thenReturn("testScript4");
        when(mockScript4.getId()).thenReturn(7);
        when(mockScript4.getName()).thenReturn("testScript7");
        when(mockScript1.getModified()).thenReturn(new Date());
        when(mockScript2.getModified()).thenReturn(new Date());
        when(mockScript3.getModified()).thenReturn(new Date());
        when(mockScript4.getModified()).thenReturn(new Date());

        List<Script> scripts = Arrays.asList(mockScript1, mockScript2, mockScript3, mockScript4);
        when(mockScriptDao.findAll()).thenReturn(scripts);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        Map<Integer, String> result = service.getAllScriptNames();
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("testScript1", result.get(1));
        assertEquals("testScript2", result.get(2));
        assertEquals("testScript4", result.get(4));
        assertEquals("testScript7", result.get(7));
    }


    @Test
    public void testGetAllScriptNamesException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        when(mockScriptDao.findAll()).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, service::getAllScriptNames);
    }

    @Test
    public void testDownloadScript() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        Script mockScript = mock(Script.class);
        ScriptTO mockScriptTO = mock(ScriptTO.class);
        StreamingResponseBody mockStreamingResponse = mock(StreamingResponseBody.class);

        when(mockScriptDao.findById(anyInt())).thenReturn(mockScript);
        when(mockScript.getName()).thenReturn("testScript");

        try (MockedStatic<ScriptServiceUtil> mockedScriptServiceUtil = Mockito.mockStatic(ScriptServiceUtil.class);
                MockedStatic<ResponseUtil> mockedResponseUtil = Mockito.mockStatic(ResponseUtil.class) ) {
            mockedScriptServiceUtil.when(() -> ScriptServiceUtil.scriptToTransferObject(mockScript)).thenReturn(mockScriptTO);
            mockedResponseUtil.when(() -> ResponseUtil.getXMLStream(mockScriptTO)).thenReturn(mockStreamingResponse);

            ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
                @Override
                protected ScriptDao createScriptDao() {
                    return mockScriptDao;
                }
            };

            Map<String, StreamingResponseBody> result = service.downloadScript(67);
            assertNotNull(result);
            assertTrue(result.containsKey("testScript_TS.xml"));
            assertEquals(mockStreamingResponse, result.get("testScript_TS.xml"));
        }

    }

    @Test
    public void testDownloadScriptNull() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);

        when(mockScriptDao.findById(anyInt())).thenReturn(null);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        assertNull(service.downloadScript(78));
    }

    @Test
    public void testDownloadScriptException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        when(mockScriptDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.downloadScript(1));
    }

    @Test
    public void testDownloadHarnessScript() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        Script mockScript = mock(Script.class);
        HDWorkload mockHDWorkload = mock(HDWorkload.class);
        StreamingResponseBody mockStreamingResponse = mock(StreamingResponseBody.class);

        when(mockScriptDao.findById(anyInt())).thenReturn(mockScript);
        when(mockScript.getName()).thenReturn("testHarnessScript");

        try (MockedStatic<ConverterUtil> mockedConverterUtil = Mockito.mockStatic(ConverterUtil.class);
             MockedStatic<ResponseUtil> mockedResponseUtil = Mockito.mockStatic(ResponseUtil.class)) {
            mockedConverterUtil.when(() -> ConverterUtil.convertScriptToHdWorkload(mockScript)).thenReturn(mockHDWorkload);
            mockedResponseUtil.when(() -> ResponseUtil.getXMLStream(mockHDWorkload)).thenReturn(mockStreamingResponse);

            ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
                @Override
                protected ScriptDao createScriptDao() {
                    return mockScriptDao;
                }
            };

            Map<String, StreamingResponseBody> result = service.downloadHarnessScript(60);

            assertNotNull(result);
            assertTrue(result.containsKey("testHarnessScript_H.xml"));
            assertEquals(mockStreamingResponse, result.get("testHarnessScript_H.xml"));
        }
    }


    @Test
    public void testDownloadHarnessScriptNull() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);

        when(mockScriptDao.findById(anyInt())).thenReturn(null);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        assertNull(service.downloadHarnessScript(78));
    }

    @Test
    public void testDownloadHarnessScriptException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);

        when(mockScriptDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.downloadHarnessScript(4));
    }

    @Test
    public void testDeleteScript() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        Script mockScript = mock(Script.class);
        when(mockScriptDao.findById(anyInt())).thenReturn(mockScript);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        String result = service.deleteScript(123);

        assertEquals("", result);
        verify(mockScriptDao, times(1)).delete(mockScript);
    }


    @Test
    public void testDeleteScriptNotFound() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);
        when(mockScriptDao.findById(anyInt())).thenReturn(null);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        String result = service.deleteScript(321);
        assertEquals("Script with script id 321 does not exist", result);
    }


    @Test
    public void testDeleteScriptException() {
        ScriptDao mockScriptDao = mock(ScriptDao.class);

        when(mockScriptDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ScriptDao createScriptDao() {
                return mockScriptDao;
            }
        };

        assertThrows(GenericServiceDeleteException.class, () -> service.deleteScript(123));
    }

    @Test
    public void testGetExternalScripts() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);
        ExternalScript mockScript = mock(ExternalScript.class);
        ExternalScriptTO mockScriptTO = mock(ExternalScriptTO.class);

        when(mockDao.findAll()).thenReturn(Collections.singletonList(mockScript));

        try (MockedStatic<ScriptServiceUtil> mockedUtil = Mockito.mockStatic(ScriptServiceUtil.class)) {
            mockedUtil.when(() -> ScriptServiceUtil.externalScriptToTO(mockScript)).thenReturn(mockScriptTO);

            ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
                @Override
                protected ExternalScriptDao createExternalScriptDao() {
                    return mockDao;
                }
            };

            ExternalScriptContainer result = service.getExternalScripts();
            assertNotNull(result);
            assertEquals(1, result.getScripts().size());
            assertEquals(mockScriptTO, result.getScripts().get(0));
        }
    }

    @Test
    public void testGetExternalScriptsException() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);
        when(mockDao.findAll()).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ExternalScriptDao createExternalScriptDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, service::getExternalScripts);
    }

    @Test
    public void testGetExternalScript() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);
        ExternalScript mockScript = mock(ExternalScript.class);
        ExternalScriptTO mockScriptTO = mock(ExternalScriptTO.class);

        when(mockDao.findById(anyInt())).thenReturn(mockScript);

        try (MockedStatic<ScriptServiceUtil> mockedUtil = Mockito.mockStatic(ScriptServiceUtil.class)) {
            mockedUtil.when(() -> ScriptServiceUtil.externalScriptToTO(mockScript)).thenReturn(mockScriptTO);

            ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
                @Override
                protected ExternalScriptDao createExternalScriptDao() {
                    return mockDao;
                }
            };

            ExternalScriptTO result = service.getExternalScript(446);
            assertNotNull(result);
            assertEquals(mockScriptTO, result);
        }
    }


    @Test
    public void testGetExternalScriptNotFound() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);

        when(mockDao.findById(anyInt())).thenReturn(null);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ExternalScriptDao createExternalScriptDao() {
                return mockDao;
            }
        };

        assertNull(service.getExternalScript(687));
    }


    @Test
    public void testGetExternalScriptException() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);

        when(mockDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ExternalScriptDao createExternalScriptDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getExternalScript(2293));
    }

    @Test
    public void testCreateExternalScript() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);
        ExternalScript mockScript = mock(ExternalScript.class);
        ExternalScriptTO mockScriptRequest = mock(ExternalScriptTO.class);

        when(mockDao.saveOrUpdate(any(ExternalScript.class))).thenReturn(mockScript);

        try (MockedStatic<ScriptServiceUtil> mockedScriptServiceUtil = Mockito.mockStatic(ScriptServiceUtil.class)) {
            mockedScriptServiceUtil.when(() -> ScriptServiceUtil.TOToExternalScript(mockScriptRequest)).thenReturn(mockScript);
            mockedScriptServiceUtil.when(() -> ScriptServiceUtil.externalScriptToTO(mockScript)).thenReturn(mockScriptRequest);

            ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
                @Override
                protected ExternalScriptDao createExternalScriptDao() {
                    return mockDao;
                }
            };

            ExternalScriptTO result = service.createExternalScript(mockScriptRequest);
            assertNotNull(result);
            assertEquals(mockScriptRequest, result);
        }
    }


    @Test
    public void testCreateExternalScriptException() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);
        ExternalScriptTO mockScriptRequest = mock(ExternalScriptTO.class);

        when(mockDao.saveOrUpdate(any(ExternalScript.class))).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ExternalScriptDao createExternalScriptDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.createExternalScript(mockScriptRequest));
    }

    @Test
    public void testDeleteExternalScript() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);
        ExternalScript mockScript = mock(ExternalScript.class);

        when(mockDao.findById(anyInt())).thenReturn(mockScript);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ExternalScriptDao createExternalScriptDao() {
                return mockDao;
            }
        };

        assertEquals("", service.deleteExternalScript(2));

        verify(mockDao).delete(mockScript);
    }


    @Test
    public void testDeleteExternalScriptNotFound() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);

        when(mockDao.findById(anyInt())).thenReturn(null);

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ExternalScriptDao createExternalScriptDao() {
                return mockDao;
            }
        };

        assertEquals("External script with external script id 1 does not exist", service.deleteExternalScript(1));

        verify(mockDao, never()).delete(anyInt());
    }


    @Test
    public void testDeleteExternalScriptException() {
        ExternalScriptDao mockDao = mock(ExternalScriptDao.class);
        when(mockDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        ScriptServiceV2Impl service = new ScriptServiceV2Impl() {
            @Override
            protected ExternalScriptDao createExternalScriptDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceDeleteException.class, () -> service.deleteExternalScript(1));
    }
}
