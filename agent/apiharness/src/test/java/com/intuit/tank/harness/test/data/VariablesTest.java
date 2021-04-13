package com.intuit.tank.harness.test.data;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import com.intuit.tank.vm.common.util.ValidationUtil;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VariablesTest {

    static Stream<Arguments> validations() {
        return Stream.of(
                Arguments.of("Hello #{name}, I want you to meet #{other}. #{string.concat('She ', 'is ', adjective, '.')}", //${string.
                        "Hello Denis Angleton, I want you to meet Sue King. She is cool." ),
                Arguments.of( "No Replacements", "No Replacements" ),
                Arguments.of( "#{bogus_name}", "" ),
                Arguments.of( "#{something  }", "" ),
                Arguments.of( "#{string.concat}", "" ),
                Arguments.of( "#{bogusFunction}", "" ),
                Arguments.of( "#{string.concat()}", "" )
            );
    }

    @BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testVariable_NoPrefix() {
        Variables variables = new Variables();
        variables.addVariable("VariableName_1", "Variable Value_1");
        assertFalse(ValidationUtil.isVariable("VariableName_1"));
        assertFalse(ValidationUtil.isVariable("NOTVARIABLE"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testVariable_Prefix() {
        // Variables variables = Variables.getInstance();
        Variables variables = new Variables();
        variables.addVariable("VariableName_2", "Variable Value_2");
        assertTrue(ValidationUtil.isVariable("@VariableName_2"));
        assertFalse(ValidationUtil.isVariable("NOTVARIABLE"));
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("validations")
    public void testEvaluate(String expression, String expected) {
        Variables variables = new Variables();
        variables.getContext().set("string", VariablesTest.class);
        variables.addVariable("name", "Denis Angleton");
        variables.addVariable("other", "Sue King");
        variables.addVariable("adjective", "cool");
        String evaluated = variables.evaluate(expression);
        assertEquals(evaluated, expected);
    }

    public static String concat(String... s) {
        return StringUtils.join(s);
    }
    
    
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSetJson() throws IOException{
    	
    	String json = readFile("src/test/resources/json-response.json");
    	
    	Variables variables = new Variables();
    	variables.addVariable("RESPONSE_BODY", json);
    }
    
    
    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
