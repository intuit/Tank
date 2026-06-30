/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.script;

/*
 * #%L
 * External Script Engine
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 * ScriptRunner
 * 
 * @author dangleton
 * 
 */
public class ScriptRunner {

    // Cache compiled scripts to avoid expensive recompilation
    private static final ConcurrentHashMap<String, CompiledScript> COMPILED_SCRIPT_CACHE = new ConcurrentHashMap<>();
    
    // Statistics for monitoring
    private static final ConcurrentHashMap<String, Long> SCRIPT_COMPILE_COUNT = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> SCRIPT_CACHE_HIT_COUNT = new ConcurrentHashMap<>();

    /**
     * 
     */
    public ScriptRunner() {
        super();
    }
    
    /**
     * Clears the compiled script cache. Useful for testing or when scripts are updated.
     */
    public static void clearCompiledScriptCache() {
        COMPILED_SCRIPT_CACHE.clear();
        SCRIPT_COMPILE_COUNT.clear();
        SCRIPT_CACHE_HIT_COUNT.clear();
    }
    
    /**
     * Gets cache statistics for monitoring.
     * @return map with "cacheSize", "totalCompiles", "totalCacheHits"
     */
    public static Map<String, Long> getCacheStatistics() {
        long totalCompiles = SCRIPT_COMPILE_COUNT.values().stream().mapToLong(Long::longValue).sum();
        long totalHits = SCRIPT_CACHE_HIT_COUNT.values().stream().mapToLong(Long::longValue).sum();
        return Map.of(
            "cacheSize", (long) COMPILED_SCRIPT_CACHE.size(),
            "totalCompiles", totalCompiles,
            "totalCacheHits", totalHits
        );
    }

    /**
     * 
     * @param script
     * @param engine
     * @param inputs
     * @param output
     * @return
     * @throws ScriptException
     */
    public ScriptIOBean runScript(@Nonnull String script, @Nonnull ScriptEngine engine,
            @Nonnull Map<String, Object> inputs, OutputLogger output) throws ScriptException {
        return runScript(null, script, engine, inputs, output);
    }

    /**
     * Runs a script with compiled script caching for performance.
     * 
     * 
     * @param scriptName unique name for script caching (null disables caching)
     * @param script the script source code
     * @param engine the ScriptEngine to use
     * @param inputs input variables for the script
     * @param output output logger
     * @return ScriptIOBean with execution results
     * @throws ScriptException if script execution fails
     */
    public ScriptIOBean runScript(@Nullable String scriptName, @Nonnull String script, @Nonnull ScriptEngine engine,
            @Nonnull Map<String, Object> inputs, OutputLogger output) throws ScriptException {
        ScriptIOBean ioBean = new ScriptIOBean(inputs, output);
        ioBean.debug("Starting scriptEngine...");
        
        try {
            // Create isolated ScriptContext to prevent variable pollution between executions
            ScriptContext context = new SimpleScriptContext();
            context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
            
            // Add ioBean to context
            context.getBindings(ScriptContext.ENGINE_SCOPE).put("ioBean", ioBean);
            
            // Copy input variables to context
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                context.getBindings(ScriptContext.ENGINE_SCOPE).put(entry.getKey(), entry.getValue());
            }
            
            // Use compiled script caching if possible
            if (scriptName != null && !scriptName.isEmpty() && engine instanceof Compilable) {
                executeCompiledScript(scriptName, script, (Compilable) engine, context);
            } else {
                // Fallback: direct evaluation (no caching)
                try (Reader reader = new StringReader(script)) {
                    engine.eval(reader, context);
                }
            }
            
            ioBean.debug("Finished scriptEngine...");
        } catch (ScriptException e) {
            throw new ScriptException(e.getMessage(), scriptName, e.getLineNumber(), e.getColumnNumber());
        } catch (IOException e) {
            throw new ScriptException(e.getMessage(), scriptName, 0, 0);
        }
        
        return ioBean;
    }
    
    /**
     * Executes a script using compiled script caching.
     * 
     * @param scriptName unique script identifier for caching
     * @param script source code to compile (if not cached)
     * @param compilable the Compilable engine
     * @param context the ScriptContext to execute in
     * @throws ScriptException if compilation or execution fails
     */
    private void executeCompiledScript(@Nonnull String scriptName, @Nonnull String script, 
                                       @Nonnull Compilable compilable, @Nonnull ScriptContext context) 
                                       throws ScriptException {
        CompiledScript compiledScript = COMPILED_SCRIPT_CACHE.get(scriptName);
        
        if (compiledScript == null) {
            // Cache miss: compile and cache the script
            compiledScript = compileAndCache(scriptName, script, compilable);
            SCRIPT_COMPILE_COUNT.merge(scriptName, 1L, Long::sum);
        } else {
            // Cache hit: use cached compiled script
            SCRIPT_CACHE_HIT_COUNT.merge(scriptName, 1L, Long::sum);
        }
        
        // Execute the compiled script with isolated context
        compiledScript.eval(context);
    }
    
    /**
     * Compiles a script and caches it.
     * Thread-safe: uses computeIfAbsent to handle race conditions.
     * 
     * @param scriptName unique script identifier
     * @param script source code to compile
     * @param compilable the Compilable engine
     * @return compiled script
     * @throws ScriptException if compilation fails
     */
    private CompiledScript compileAndCache(@Nonnull String scriptName, @Nonnull String script, 
                                          @Nonnull Compilable compilable) throws ScriptException {
        // Use computeIfAbsent to handle race conditions (only one thread compiles)
        return COMPILED_SCRIPT_CACHE.computeIfAbsent(scriptName, name -> {
            try {
                return compilable.compile(script);
            } catch (ScriptException e) {
                // Wrap in RuntimeException since computeIfAbsent doesn't allow checked exceptions
                throw new RuntimeException("Failed to compile script: " + name, e);
            }
        });
    }

}
