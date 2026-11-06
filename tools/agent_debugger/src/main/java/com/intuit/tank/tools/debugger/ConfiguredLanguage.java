/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * ConfiguredLanguages
 * 
 * @author dangleton
 * 
 */
public class ConfiguredLanguage {

    private String name;
    private String displayName;
    private String syntaxStyle;
    private String defaultExtension;

    private static final Logger LOG = LogManager.getLogger(ConfiguredLanguage.class);

    private static final List<ConfiguredLanguage> configuredLanguages = new ArrayList<ConfiguredLanguage>();
    private static final Set<String> extensionSet = new HashSet<String>();

    private static final String[][] data = {
            { "ECMAScript", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, "Javascript",
                    "com.sun.script.javascript.RhinoScriptEngineFactory", "js" },
            { "ruby", SyntaxConstants.SYNTAX_STYLE_RUBY, "Ruby", "com.sun.script.jruby.JRubyScriptEngineFactory", "rb" },
            { "groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY, "Groovy",
                    "org.codehaus.groovy.jsr223.GroovyScriptEngineFactory", "groovy" }
    };

    private static final ScriptEngineManager manager = new ScriptEngineManager();
    static {
        for (String[] row : data) {
            try {
                ScriptEngine engineByName = manager.getEngineByName(row[0]);
                ScriptEngineFactory factory = engineByName != null ? engineByName.getFactory() : null;
                if (factory == null) {
                    // not found on its own: register its factory, by name and by extension
                    factory = (ScriptEngineFactory) Class.forName(row[3]).getDeclaredConstructor().newInstance();
                    manager.registerEngineName(row[0], factory);
                    for (String extension : factory.getExtensions()) {
                        manager.registerEngineExtension(extension, factory);
                    }
                }
                configuredLanguages.add(new ConfiguredLanguage(row[0], row[1], row[2], row[4]));
                extensionSet.addAll(factory.getExtensions());
            } catch (Exception e) {
                LOG.info("No ScriptEngine for language {} in classpath.", row[0]);
            }
        }
        if (LOG.isDebugEnabled()) {
            for (ScriptEngineFactory fact : manager.getEngineFactories()) {
                LOG.debug("Script engine available: {}", fact.getLanguageName());
            }
        }
    }

    /**
     * @return the extensionset
     */
    public static Set<String> getConfiguredExtensions() {
        return extensionSet;
    }

    /**
     * 
     * @return
     */
    public static List<ConfiguredLanguage> getConfiguredLanguages() {
        return configuredLanguages;
    }

    /**
     * 
     * @param scriptName
     * @return
     */
    public static ConfiguredLanguage getLanguagebyExtension(String scriptName) {
        String extension = FilenameUtils.getExtension(scriptName);
        ScriptEngine engineByExtension = manager.getEngineByExtension(extension);
        if (engineByExtension == null) {
            return null; // no script engine handles this extension
        }
        return configuredLanguages.stream().filter(lang -> lang.name.equals(engineByExtension.getFactory().getLanguageName())).findFirst().orElse(null);
    }

    /**
     * @param name
     * @param syntaxStyle
     * @param displayName
     * @param defaultExtension
     */
    private ConfiguredLanguage(String name, String syntaxStyle, String displayName, String defaultExtension) {
        this.name = name;
        this.syntaxStyle = syntaxStyle;
        this.displayName = displayName;
        this.defaultExtension = defaultExtension;
    }

    /**
     * @return the name
     */
    public ScriptEngine getEngine() {
        return manager.getEngineByName(name);
    }

    /**
     * @return the syntaxStyle
     */
    public String getSyntaxStyle() {
        return syntaxStyle;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the defaultExtension
     */
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return displayName;
    }

}
