/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
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
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.Assert;

import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.Workload;

/**
 * DaoTestUtil
 * 
 * @author dangleton
 * 
 */
public class DaoTestUtil {

    private static Random random = new Random();

    /**
     * private constructor to implement util pattern.
     */
    private DaoTestUtil() {
        // emtpy
    }

    /**
     * @return
     */
    public static Project createProject() {
        return Project.builder().comments("Comments")
                .creator("Test Creator")
                .productName("Test Product Name")
                .name("Test Project Name " + generateStringOfLength(15)).build();
    }

    /**
     * Generate a new Workload object for testing.
     * 
     * @return the new Workload
     */
    public static Workload createWorkload() {
        // .baselineVirtualUsers(random.nextInt(100))
        // .rampTime(random.nextInt(1000000))
        // .simulationTime(random.nextInt(1000000))
        // .totalVirtualUsers(random.nextInt(10000000))
        // .userIntervalIncrement(random.nextInt(100))
        return Workload.builder()
                .name("Test Workload " + generateStringOfLength(15))
                .build();
    }

    /**
     * Generate a new ScriptGroup object for testing.
     * 
     * @param numLoops
     *            the number of loops to set
     * @return the ScriptGroup
     */
    public static ScriptGroup createScriptGroup(int numLoops) {
        return ScriptGroup.builder().name("Test Script Group " + generateStringOfLength(15)).loop(numLoops).build();
    }

    /**
     * Generate a new ScriptGroupStep object for testing.
     * 
     * @param numLoops
     *            the number of loops for this step.
     * @return the ScriptGroupStep
     */
    public static ScriptGroupStep createScriptGroupStep(int numLoops) {
        return ScriptGroupStep.builder().loop(numLoops).build();
    }

    /**
     * Generate a new Script object for testing.
     * 
     * @return the Script
     */
    public static Script createScript() {
        return Script.builder()
                .name("Test Script " + generateStringOfLength(15))
                .comments("Test Comments")
                .creator("Test Creator")
                .productName("Test Product Name")
                .runtime(10)
                .build();
    }

    /**
     * @return
     */
    public static ScriptStep createScriptStep() {
        return ScriptStep.builder()
                .comments("Test Comments")
                .hostname("www.test.com")
                .build();
    }

    /**
     * gets a
     * 
     * @param numSteps
     * @return
     */
    public static List<ScriptStep> getScriptSteps(@Nonnegative int numSteps) {
        List<ScriptStep> result = new ArrayList<ScriptStep>();
        for (int i = 0; i < numSteps; i++) {
            result.add(createScriptStep());
        }
        return result;
    }

    /**
     * Generate a random string of the specified length.
     * 
     * @param length
     *            the number of characters in the string
     * @return the string of the specified length
     */
    public static String generateStringOfLength(int length) {
        StringBuilder sb = new StringBuilder();

        while (--length >= 0) {
            sb.append((char) (random.nextInt(52) + 65));
        }
        return sb.toString();
    }

    /**
     * Assert that a specific constraint violation was thrown. Will find the Violation by iterating the violations in
     * the exception and checking each one. Throws Assertion
     * 
     * @param e
     *            the exception
     * @param property
     *            the property that the violation should have occurred on.
     * @param messageContains
     *            the contents to match in the message. Uses String.contains()
     * @throws AssertionError
     *             if the exception does not contain a violatino on the property with the specified string.
     */
    public static void checkConstraintViolation(@Nonnull ConstraintViolationException e, @Nonnull String property,
            @Nonnull String messageContains) throws AssertionError {
        System.out.println(e.getMessage());
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : constraintViolations) {
            if (property.equalsIgnoreCase(violation.getPropertyPath().iterator().next().getName())) {
                Assert.assertTrue(violation.getMessage().contains(messageContains));
                return;
            }
        }
        Assert.fail("Constraint violation did not contain a violation on property " + property);
    }

}
