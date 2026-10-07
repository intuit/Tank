/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The React app under /app, served from the test fixture in META-INF/resources/app.
 */
@SpringBootTest(classes = SpaHostingTest.SpaConfig.class)
@AutoConfigureMockMvc
public class SpaHostingTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @Import(CustomWebMvcConfigurer.class)
    static class SpaConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = { "/tank/app/index.html", "/tank/app/projects", "/tank/app/projects/12/edit" })
    public void clientRoutesGetIndexWithContextBase(String path) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(path).contextPath("/tank"))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertTrue(response.getContentType().startsWith("text/html"), response.getContentType());
        // a charset in the header overrides <meta charset="UTF-8">, and the CSS inherits it
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertTrue(response.getContentAsString().contains("<base href=\"/tank/app/\">"));
        assertEquals("no-cache", response.getHeader(HttpHeaders.CACHE_CONTROL));
    }

    @Test
    public void appRootForwardsToIndexAsUtf8() throws Exception {
        mockMvc.perform(get("/tank/app").contextPath("/tank"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tank/app/"));
        // the forward's render sets the locale, from which Tomcat would otherwise pick ISO-8859-1
        MockHttpServletResponse response = mockMvc.perform(get("/tank/app/").contextPath("/tank"))
                .andExpect(forwardedUrl("/app/index.html")).andReturn().getResponse();
        assertEquals("UTF-8", response.getCharacterEncoding());
    }

    @Test
    public void rootContextKeepsBase() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/app/scripts"))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertTrue(response.getContentAsString().contains("<base href=\"/app/\">"));
    }

    @Test
    public void assetsAreCachedAndUntouched() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/tank/app/assets/index-test.js").contextPath("/tank"))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertTrue(response.getContentAsString().contains("console.log"));
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertFalse(response.getContentAsString().contains("<base"));
        assertTrue(response.getHeader(HttpHeaders.CACHE_CONTROL).contains("immutable"),
                response.getHeader(HttpHeaders.CACHE_CONTROL));
    }

    @ParameterizedTest
    @ValueSource(strings = { "/tank/app/assets/missing.js", "/tank/app/missing.png", "/tank/app/projects/old.css" })
    public void missingFilesAre404(String path) throws Exception {
        mockMvc.perform(get(path).contextPath("/tank"))
                .andExpect(status().isNotFound());
    }
}
