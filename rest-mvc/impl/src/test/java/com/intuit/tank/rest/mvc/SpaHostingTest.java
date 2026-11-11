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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
 * The React app at the root, served from the test fixture in META-INF/resources/app, beside the JSF UI
 * under /classic.
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
    @ValueSource(strings = { "/tank/index.html", "/tank/projects", "/tank/projects/12/edit", "/tank/app/projects" })
    public void clientRoutesGetIndexWithContextBase(String path) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(path).contextPath("/tank"))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertTrue(response.getContentType().startsWith("text/html"), response.getContentType());
        // a charset in the header overrides <meta charset="UTF-8">, and the CSS inherits it
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertTrue(response.getContentAsString().contains("<base href=\"/tank/\">"));
        assertEquals("no-cache", response.getHeader(HttpHeaders.CACHE_CONTROL));
    }

    @Test
    public void rootForwardsToIndexAsUtf8() throws Exception {
        // the forward's render sets the locale, from which Tomcat would otherwise pick ISO-8859-1
        MockHttpServletResponse response = mockMvc.perform(get("/tank/").contextPath("/tank"))
                .andExpect(forwardedUrl("/index.html")).andReturn().getResponse();
        assertEquals("UTF-8", response.getCharacterEncoding());
    }

    @Test
    public void rootContextKeepsBase() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/scripts"))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertTrue(response.getContentAsString().contains("<base href=\"/\">"));
    }

    @Test
    public void assetsAreCachedAndUntouched() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/tank/assets/index-test.js").contextPath("/tank"))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertTrue(response.getContentAsString().contains("console.log"));
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertFalse(response.getContentAsString().contains("<base"));
        assertTrue(response.getHeader(HttpHeaders.CACHE_CONTROL).contains("immutable"),
                response.getHeader(HttpHeaders.CACHE_CONTROL));
    }

    @ParameterizedTest
    @ValueSource(strings = { "/tank/assets/missing.js", "/tank/missing.png", "/tank/projects/old.css",
            "/tank/v2/missing", "/tank/v3/missing/", "/tank/classic/login.xhtml", "/tank/classic/missing" })
    public void missingFilesAre404(String path) throws Exception {
        mockMvc.perform(get(path).contextPath("/tank"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void classicRootGoesToTheJsfLogin() throws Exception {
        mockMvc.perform(get("/tank/classic").contextPath("/tank"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tank/classic/"));
        mockMvc.perform(get("/tank/classic/").contextPath("/tank"))
                .andExpect(forwardedUrl("/classic/login.jsf"));
    }
}
