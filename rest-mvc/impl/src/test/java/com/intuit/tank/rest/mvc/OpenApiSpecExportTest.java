/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.rest.mvc.rest.controllers.AdminController;
import com.intuit.tank.rest.mvc.rest.controllers.AgentController;
import com.intuit.tank.rest.mvc.rest.controllers.AuthController;
import com.intuit.tank.rest.mvc.rest.controllers.ConfigController;
import com.intuit.tank.rest.mvc.rest.controllers.DataFileController;
import com.intuit.tank.rest.mvc.rest.controllers.DefaultController;
import com.intuit.tank.rest.mvc.rest.controllers.FilterController;
import com.intuit.tank.rest.mvc.rest.controllers.JobController;
import com.intuit.tank.rest.mvc.rest.controllers.LogController;
import com.intuit.tank.rest.mvc.rest.controllers.MeController;
import com.intuit.tank.rest.mvc.rest.controllers.ProjectController;
import com.intuit.tank.rest.mvc.rest.controllers.ProjectJobController;
import com.intuit.tank.rest.mvc.rest.controllers.ScriptController;
import com.intuit.tank.rest.mvc.rest.controllers.UserController;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericExceptionHandler;
import com.intuit.tank.rest.mvc.rest.services.admin.AdminServiceV2;
import com.intuit.tank.rest.mvc.rest.services.agent.AgentServiceV2;
import com.intuit.tank.rest.mvc.rest.services.auth.AuthServiceV2;
import com.intuit.tank.rest.mvc.rest.services.config.ConfigServiceV2;
import com.intuit.tank.rest.mvc.rest.services.datafiles.DataFileServiceV2;
import com.intuit.tank.rest.mvc.rest.services.filters.FilterServiceV2;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobQueueServiceV2;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobServiceV2;
import com.intuit.tank.rest.mvc.rest.services.logs.LogServiceV2;
import com.intuit.tank.rest.mvc.rest.services.me.MeServiceV2;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptDraftServiceV2;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptServiceV2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exports the OpenAPI spec to {@code target/openapi/tank-openapi.json}, where {@code web/web_react}
 * generates its TypeScript client from it ({@code npm run generate:api}).
 * <p>
 * Only the controllers and springdoc are started; every service is a mock, so no database or
 * Tank configuration is needed.
 */
@SpringBootTest(classes = OpenApiSpecExportTest.SpecConfig.class, properties = {
        "springdoc.writer-with-order-by-keys=true",
        "springdoc.writer-with-default-pretty-printer=true"
})
@AutoConfigureMockMvc
public class OpenApiSpecExportTest {

    static final Path SPEC_FILE = Path.of("target", "openapi", "tank-openapi.json");

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @Import({ AdminController.class, AgentController.class, AuthController.class, ConfigController.class,
            DataFileController.class, DefaultController.class, FilterController.class, JobController.class,
            LogController.class, MeController.class, ProjectController.class, ProjectJobController.class,
            ScriptController.class, UserController.class, GenericExceptionHandler.class })
    static class SpecConfig {
        // A bean (not an @Import) so springdoc sees its @OpenAPIDefinition and @SecurityScheme
        // without its @ComponentScan pulling in the real services
        @Bean
        TankAPIApplication tankApiApplication() {
            return new TankAPIApplication();
        }
    }

    @MockitoBean private AdminServiceV2 adminService;
    @MockitoBean private AgentServiceV2 agentService;
    @MockitoBean private AuthServiceV2 authService;
    @MockitoBean private ConfigServiceV2 configService;
    @MockitoBean private DataFileServiceV2 dataFileService;
    @MockitoBean private FilterServiceV2 filterService;
    @MockitoBean private JobQueueServiceV2 jobQueueService;
    @MockitoBean private JobServiceV2 jobService;
    @MockitoBean private LogServiceV2 logServiceV2;
    @MockitoBean private MeServiceV2 meService;
    @MockitoBean private ProjectServiceV2 projectService;
    @MockitoBean private ScriptDraftServiceV2 scriptDraftService;
    @MockitoBean private ScriptServiceV2 scriptService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void exportSpec() throws Exception {
        String spec = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JsonNode paths = new ObjectMapper().readTree(spec).path("paths");
        assertTrue(paths.has("/v2/auth/login"), "spec is missing the session endpoints");
        assertTrue(paths.has("/v2/me") && paths.has("/v2/jobs/tree"),
                "spec is missing controllers: only " + paths.size() + " paths");

        Files.createDirectories(SPEC_FILE.getParent());
        Files.writeString(SPEC_FILE, spec + "\n", StandardCharsets.UTF_8);
    }
}
