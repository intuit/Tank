package com.intuit.tank.tools.script;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Factory for obtaining a JavaScript {@link ScriptEngine}.
 *
 * <p>When GraalVM JS is on the classpath it is returned with {@link HostAccess#ALL}
 * so that scripts can invoke public methods on Java objects (e.g. {@code ioBean.setOutput(...)}).
 * Falls back to whatever JSR-223 "js" engine the JVM provides.
 */
public final class JsEngineFactory {

    private JsEngineFactory() {}

    /**
     * @return a JS {@link ScriptEngine} configured for full host access.
     */
    public static ScriptEngine createJsEngine() {
        try {
            return GraalJSScriptEngine.create(null,
                    Context.newBuilder("js")
                           .allowExperimentalOptions(true)
                           .allowHostAccess(HostAccess.ALL)
                           .allowHostClassLookup(className -> true)
                           .option("js.ecmascript-version", "2023")
                           .option("js.nashorn-compat", "true"));
        } catch (NoClassDefFoundError e) {
            // GraalVM not on classpath — fall back to whatever JSR-223 provides
            return new ScriptEngineManager().getEngineByExtension("js");
        }
    }
}
