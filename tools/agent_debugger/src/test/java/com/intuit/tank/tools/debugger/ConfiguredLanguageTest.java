package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.jupiter.api.Test;

/**
 * Only the JavaScript engine (Nashorn) is on the module's classpath; Ruby and Groovy are skipped.
 */
public class ConfiguredLanguageTest {

    @Test
    public void testOnlyLanguagesWithAnEngineAreConfigured() {
        List<ConfiguredLanguage> languages = ConfiguredLanguage.getConfiguredLanguages();

        assertEquals(1, languages.size());
        ConfiguredLanguage javascript = languages.get(0);
        assertEquals("Javascript", javascript.getDisplayName());
        assertEquals("Javascript", javascript.toString());
        assertEquals("js", javascript.getDefaultExtension());
        assertEquals(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, javascript.getSyntaxStyle());
    }

    @Test
    public void testEngineEvaluatesScripts() throws Exception {
        ConfiguredLanguage javascript = ConfiguredLanguage.getConfiguredLanguages().get(0);

        assertEquals(42, ((Number) javascript.getEngine().eval("6 * 7")).intValue());
    }

    @Test
    public void testExtensionsComeFromTheEngine() {
        assertTrue(ConfiguredLanguage.getConfiguredExtensions().contains("js"));
    }

    @Test
    public void testLanguageByExtension() {
        assertSame(ConfiguredLanguage.getConfiguredLanguages().get(0),
                ConfiguredLanguage.getLanguagebyExtension("scripts/validate.js"));
    }
}
