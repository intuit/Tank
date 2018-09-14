package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestHttpClient;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>TestStepRunnerTest</code> contains tests for the class <code>{@link TestStepRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class TestStepRunnerTest {
    /**
     * Run the TestStepRunner(TestStepContext) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testTestStepRunner_1()
        throws Exception {
        TestStepContext tsc = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient()));

        TestStepRunner result = new TestStepRunner(tsc);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ClassNotFoundException: com.intuit.tank.harness.data.TestStep
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:372)
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at java.net.URLClassLoader.findClass(URLClassLoader.java:360)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
        //       at com.instantiations.assist.eclipse.junit.execution.core.UserDefinedClassLoader.loadClass(UserDefinedClassLoader.java:62)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionContextImpl.getClass(ExecutionContextImpl.java:99)
        //       at com.instantiations.eclipse.analysis.expression.model.SimpleTypeExpression.execute(SimpleTypeExpression.java:205)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:448)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:449)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_1()
        throws Exception {
        TestStepRunner fixture = new TestStepRunner(new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient())));
        fixture.lastSslHandshake = 1L;
        fixture.sslTimeout = 1L;

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ClassNotFoundException: com.intuit.tank.harness.data.TestStep
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:372)
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at java.net.URLClassLoader.findClass(URLClassLoader.java:360)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
        //       at com.instantiations.assist.eclipse.junit.execution.core.UserDefinedClassLoader.loadClass(UserDefinedClassLoader.java:62)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionContextImpl.getClass(ExecutionContextImpl.java:99)
        //       at com.instantiations.eclipse.analysis.expression.model.SimpleTypeExpression.execute(SimpleTypeExpression.java:205)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:448)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:449)
        //       at com.instantiations.eclipse.analysis.expression.model.VariableAccessExpression.assign(VariableAccessExpression.java:295)
        //       at com.instantiations.eclipse.analysis.expression.model.AssignmentExpression.execute(AssignmentExpression.java:200)
        //       at com.instantiations.eclipse.analysis.expression.model.ExpressionSequence.execute(ExpressionSequence.java:311)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:550)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_2()
        throws Exception {
        TestStepRunner fixture = new TestStepRunner(new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient())));
        fixture.lastSslHandshake = 1L;
        fixture.sslTimeout = 1L;

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ClassNotFoundException: com.intuit.tank.harness.data.TestStep
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:372)
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at java.net.URLClassLoader.findClass(URLClassLoader.java:360)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
        //       at com.instantiations.assist.eclipse.junit.execution.core.UserDefinedClassLoader.loadClass(UserDefinedClassLoader.java:62)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionContextImpl.getClass(ExecutionContextImpl.java:99)
        //       at com.instantiations.eclipse.analysis.expression.model.SimpleTypeExpression.execute(SimpleTypeExpression.java:205)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:448)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:449)
        //       at com.instantiations.eclipse.analysis.expression.model.VariableAccessExpression.assign(VariableAccessExpression.java:295)
        //       at com.instantiations.eclipse.analysis.expression.model.AssignmentExpression.execute(AssignmentExpression.java:200)
        //       at com.instantiations.eclipse.analysis.expression.model.ExpressionSequence.execute(ExpressionSequence.java:311)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:550)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_3()
        throws Exception {
        TestStepRunner fixture = new TestStepRunner(new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient())));
        fixture.lastSslHandshake = 1L;
        fixture.sslTimeout = 1L;

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ClassNotFoundException: com.intuit.tank.harness.data.TestStep
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:372)
        //       at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
        //       at java.security.AccessController.doPrivileged(Native Method)
        //       at java.net.URLClassLoader.findClass(URLClassLoader.java:360)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
        //       at com.instantiations.assist.eclipse.junit.execution.core.UserDefinedClassLoader.loadClass(UserDefinedClassLoader.java:62)
        //       at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionContextImpl.getClass(ExecutionContextImpl.java:99)
        //       at com.instantiations.eclipse.analysis.expression.model.SimpleTypeExpression.execute(SimpleTypeExpression.java:205)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:448)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:449)
        //       at com.instantiations.eclipse.analysis.expression.model.VariableAccessExpression.assign(VariableAccessExpression.java:295)
        //       at com.instantiations.eclipse.analysis.expression.model.AssignmentExpression.execute(AssignmentExpression.java:200)
        //       at com.instantiations.eclipse.analysis.expression.model.ExpressionSequence.execute(ExpressionSequence.java:311)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:550)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
        assertNotNull(result);
    }
}