package com.intuit.tank.harness.functions;

import com.intuit.tank.vm.common.util.ExpressionContextVisitor;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Fails when a script function the agent supports isn't mentioned in the user docs under docs/key-concepts, so the
 * function reference can't silently fall behind the code. Covers both the #{category.function(...)} functions (the
 * public methods of the Jexl*Functions classes) and the legacy #function.category.name functions (the names the
 * *Functions dispatchers accept).
 */
public class FunctionDocsCoverageTest {

    /** Surefire runs tests from the module directory, agent/apiharness */
    private static final Path DOCS_DIR = Path.of("../../docs/key-concepts");
    private static final Path FUNCTIONS_SOURCE_DIR = Path.of("src/main/java/com/intuit/tank/harness/functions");

    private static final List<ExpressionContextVisitor> JEXL_FUNCTIONS = List.of(
            new JexlIOFunctions(), new JexlStringFunctions(), new JexlDateFunctions(),
            new JexlMonetaryFunctions(), new JexlNumericFunctions(), new JexlTaxFunctions());

    /** The legacy category (the second part of #function.category.name) and the class that implements it */
    private static final Map<String, String> LEGACY_CATEGORIES = new LinkedHashMap<>();
    static {
        LEGACY_CATEGORIES.put("date", "DateFunctions.java");
        LEGACY_CATEGORIES.put("monetary", "MonetaryFunctions.java");
        LEGACY_CATEGORIES.put("numeric", "NumericFunctions.java");
        LEGACY_CATEGORIES.put("generic", "GenericFunctions.java");
        LEGACY_CATEGORIES.put("string", "StringFunctions.java");
        LEGACY_CATEGORIES.put("datatype", "DataTypeFunctions.java");
        LEGACY_CATEGORIES.put("tax", "TaxFunctions.java");
    }

    /** How each legacy dispatcher recognises a function name: values[2].equalsIgnoreCase("name") */
    private static final Pattern LEGACY_NAME = Pattern.compile("values\\[2]\\.equalsIgnoreCase\\(\"([^\"]+)\"\\)");

    private static String docs;

    @BeforeAll
    public static void readDocs() throws IOException {
        assumeTrue(Files.isDirectory(DOCS_DIR), "No docs directory at " + DOCS_DIR.toAbsolutePath());
        try (Stream<Path> files = Files.walk(DOCS_DIR)) {
            List<Path> pages = files.filter(p -> p.toString().endsWith(".md")).sorted().toList();
            StringBuilder text = new StringBuilder();
            for (Path page : pages) {
                text.append(Files.readString(page, StandardCharsets.UTF_8)).append('\n');
            }
            docs = text.toString();
        }
    }

    @Test
    public void everyExpressionFunctionIsDocumented() {
        List<String> missing = new ArrayList<>();
        for (ExpressionContextVisitor functions : JEXL_FUNCTIONS) {
            String category = contextName(functions);
            for (String name : functionNames(functions.getClass())) {
                if (!docs.contains(category + "." + name + "(")) {
                    missing.add("#{" + category + "." + name + "(...)}");
                }
            }
        }
        assertTrue(missing.isEmpty(), "Functions not documented under " + DOCS_DIR + ": " + missing);
    }

    @Test
    public void everyLegacyFunctionIsDocumented() throws IOException {
        assumeTrue(Files.isDirectory(FUNCTIONS_SOURCE_DIR), "No sources at " + FUNCTIONS_SOURCE_DIR.toAbsolutePath());
        String lowerDocs = docs.toLowerCase();
        List<String> missing = new ArrayList<>();
        for (Map.Entry<String, String> category : LEGACY_CATEGORIES.entrySet()) {
            Set<String> names = legacyNames(category.getValue());
            assertFalse(names.isEmpty(), "Found no function names in " + category.getValue()
                    + "; has the way it recognises them changed?");
            for (String name : names) {
                String function = "#function." + category.getKey() + "." + name;
                // a name the dispatcher doesn't accept means this scan has picked up the wrong thing
                assertTrue(FunctionHandler.validFunction(function + ".1.1.1"),
                        function + " was found in " + category.getValue() + " but isn't a valid function");
                // either written out in full, or listed by name next to the category (as the datatype names are)
                String lower = function.toLowerCase();
                boolean documented = lowerDocs.contains(lower)
                        || (lowerDocs.contains("#function." + category.getKey() + ".")
                                && lowerDocs.contains("`" + name.toLowerCase() + "`"));
                if (!documented) {
                    missing.add(function);
                }
            }
        }
        assertTrue(missing.isEmpty(), "Legacy functions not documented under " + DOCS_DIR + ": " + missing);
    }

    /** The name the functions are registered under in the expression context, e.g. stringFunctions */
    private static String contextName(ExpressionContextVisitor functions) {
        Map<String, Object> vars = new HashMap<>();
        functions.visit(new MapContext(vars));
        assertTrue(vars.size() == 1, functions.getClass().getSimpleName() + " registered " + vars.keySet());
        return vars.keySet().iterator().next();
    }

    /** The functions an expression can call: public instance methods, apart from the visitor callback */
    private static Set<String> functionNames(Class<?> functionsClass) {
        Set<String> visitorMethods = Stream.of(ExpressionContextVisitor.class.getMethods())
                .map(Method::getName).collect(Collectors.toSet());
        return Stream.of(functionsClass.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()))
                .filter(m -> !m.isSynthetic() && !m.isBridge())
                .map(Method::getName)
                .filter(name -> !visitorMethods.contains(name))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /** The function names a legacy dispatcher recognises, lower case */
    private static Set<String> legacyNames(String sourceFile) throws IOException {
        String source = Files.readString(FUNCTIONS_SOURCE_DIR.resolve(sourceFile), StandardCharsets.UTF_8);
        Set<String> names = new TreeSet<>();
        Matcher m = LEGACY_NAME.matcher(source);
        while (m.find()) {
            names.add(m.group(1).toLowerCase());
        }
        return names;
    }
}
