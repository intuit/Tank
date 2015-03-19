package com.intuit.tank.harness.functions;

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

import org.junit.*;

import com.intuit.tank.harness.functions.StdRandom;

import static org.junit.Assert.*;

/**
 * The class <code>StdRandomTest</code> contains tests for the class <code>{@link StdRandom}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class StdRandomTest {
    /**
     * Run the boolean bernoulli() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testBernoulli_1()
            throws Exception {

        boolean result = StdRandom.bernoulli();

    }

    /**
     * Run the double cauchy() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testCauchy_1()
            throws Exception {

        double result = StdRandom.cauchy();

    }

    /**
     * Run the int discrete(double[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testDiscrete_1()
            throws Exception {
        double[] a = new double[] { 1.0 };

        int result = StdRandom.discrete(a);

    }

    /**
     * Run the double exp(double) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testExp_1()
            throws Exception {
        double lambda = 1.0;

        double result = StdRandom.exp(lambda);

    }

    /**
     * Run the double gaussian() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGaussian_1()
            throws Exception {

        double result = StdRandom.gaussian();

    }

    /**
     * Run the double gaussian() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGaussian_2()
            throws Exception {

        double result = StdRandom.gaussian();

    }

    /**
     * Run the double gaussian() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGaussian_3()
            throws Exception {

        double result = StdRandom.gaussian();

    }

    /**
     * Run the double gaussian(double,double) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGaussian_4()
            throws Exception {
        double mean = 1.0;
        double stddev = 1.0;

        double result = StdRandom.gaussian(mean, stddev);

    }

    /**
     * Run the int geometric(double) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGeometric_1()
            throws Exception {
        double p = 1.0;

        int result = StdRandom.geometric(p);

    }

    /**
     * Run the double pareto(double) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testPareto_1()
            throws Exception {
        double alpha = 1.0;

        double result = StdRandom.pareto(alpha);

    }

    /**
     * Run the int poisson(double) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testPoisson_1()
            throws Exception {
        double lambda = 1.0;

        int result = StdRandom.poisson(lambda);

    }

    /**
     * Run the int poisson(double) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testPoisson_2()
            throws Exception {
        double lambda = 1.0;

        int result = StdRandom.poisson(lambda);

    }

    /**
     * Run the double random() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testRandom_1()
            throws Exception {

        double result = StdRandom.random();

    }

    /**
     * Run the void setSeed(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetSeed_1()
            throws Exception {
        long seed = 1L;

        StdRandom.setSeed(seed);

    }

    /**
     * Run the void shuffle(double[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testShuffle_1()
            throws Exception {
        double[] a = new double[] { 1.0 };

        StdRandom.shuffle(a);

    }

    /**
     * Run the void shuffle(double[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testShuffle_2()
            throws Exception {
        double[] a = new double[] {};

        StdRandom.shuffle(a);

    }

    /**
     * Run the void shuffle(int[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testShuffle_3()
            throws Exception {
        int[] a = new int[] { 1 };

        StdRandom.shuffle(a);

    }

    /**
     * Run the void shuffle(int[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testShuffle_4()
            throws Exception {
        int[] a = new int[] {};

        StdRandom.shuffle(a);

    }

    /**
     * Run the void shuffle(Object[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testShuffle_5()
            throws Exception {
        Object[] a = new Object[] { new Object() };

        StdRandom.shuffle(a);

    }

    /**
     * Run the void shuffle(Object[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testShuffle_6()
            throws Exception {
        Object[] a = new Object[] {};

        StdRandom.shuffle(a);

    }

    /**
     * Run the double uniform() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testUniform_1()
            throws Exception {

        double result = StdRandom.uniform();

    }

    /**
     * Run the int uniform(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testUniform_2()
            throws Exception {
        int N = 1;

        int result = StdRandom.uniform(N);

        assertEquals(0, result);
    }

    /**
     * Run the double uniform(double,double) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testUniform_3()
            throws Exception {
        double a = 1.0;
        double b = 1.0;

        double result = StdRandom.uniform(a, b);

        assertEquals(1.0, result, 0.1);
    }

}