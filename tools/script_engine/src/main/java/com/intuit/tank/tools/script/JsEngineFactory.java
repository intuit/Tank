package com.intuit.tank.tools.script;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Set;

/**
 * Factory for obtaining a JavaScript {@link ScriptEngine}.
 *
 * <p>When GraalVM JS is on the classpath a cached, thread-local engine is returned
 * with host access restricted to Tank packages that scripts legitimately need.
 * Falls back to whatever JSR-223 "js" engine the JVM provides.
 */
public final class JsEngineFactory {

    /**
     * Package prefixes that user-authored scripts are allowed to access via
     * {@code Java.type()} / {@code importPackage()}.  Anything outside this
     * list is blocked to prevent arbitrary class access (e.g. Runtime, ProcessBuilder).
     */
    private static final Set<String> ALLOWED_PACKAGE_PREFIXES = Set.of(
            "com.intuit.tank.script.models.",
            "java.util."
    );

    private static final boolean GRAALVM_AVAILABLE;

    static {
        boolean available;
        try {
            Class.forName("com.oracle.truffle.js.scriptengine.GraalJSScriptEngine");
            available = true;
        } catch (ClassNotFoundException e) {
            available = false;
        }
        GRAALVM_AVAILABLE = available;
    }

    private JsEngineFactory() {}

    static boolean isAllowedClass(String className) {
        for (String prefix : ALLOWED_PACKAGE_PREFIXES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return a JS {@link ScriptEngine} configured for restricted host access.
     *         A new engine is created per call because GraalVM contexts cannot
     *         be safely reused after script evaluation. Engine creation is fast
     *         once the GraalVM runtime is warmed up.
     */
    public static ScriptEngine createJsEngine() {
        if (GRAALVM_AVAILABLE) {
            return GraalJSScriptEngine.create(null,
                    Context.newBuilder("js")
                           .allowExperimentalOptions(true)
                           .allowHostAccess(HostAccess.ALL)
                           .allowHostClassLookup(JsEngineFactory::isAllowedClass)
                           .option("js.ecmascript-version", "2025")
                           .option("js.nashorn-compat", "true"));
        }
        // GraalVM not on classpath — fall back to whatever JSR-223 provides
        return new ScriptEngineManager().getEngineByExtension("js");
    }
}
