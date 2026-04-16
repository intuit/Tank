package com.intuit.tank.util;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ExceptionHandlerTest {

    @InjectMocks
    private ExceptionHandler exceptionHandler;

    @Mock
    private Messages messages;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testExceptionHandler_NotNull() {
        assertNotNull(exceptionHandler);
    }

    @Test
    public void testHandle_SimpleException_CallsError() {
        RuntimeException ex = new RuntimeException("test error message");
        exceptionHandler.handle(ex);
        verify(messages).error("test error message");
    }

    @Test
    public void testHandle_ExceptionWithCause_UnwrapsCause() {
        RuntimeException cause = new RuntimeException("root cause");
        RuntimeException wrapper = new RuntimeException("wrapper", cause);
        exceptionHandler.handle(wrapper);
        verify(messages).error("root cause");
    }

    @Test
    public void testHandle_ExceptionWithNoMessage_CallsErrorWithToString() {
        RuntimeException ex = new RuntimeException() {
            @Override
            public String getMessage() {
                return null;
            }
        };
        exceptionHandler.handle(ex);
        verify(messages).error(anyString());
    }

    @Test
    public void testHandle_DeepNestedCause_UnwrapsToRoot() {
        RuntimeException root = new RuntimeException("the root");
        RuntimeException middle = new RuntimeException("middle", root);
        RuntimeException top = new RuntimeException("top", middle);
        exceptionHandler.handle(top);
        verify(messages).error("the root");
    }
}
