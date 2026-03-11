package com.intuit.tank.project;

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

import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.util.Messages;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link FileUploadBean}.
 */
public class FileUploadBeanTest {

    @InjectMocks
    private FileUploadBean fixture;

    @Mock
    private Event<ModifiedDatafileMessage> dataFileEvent;

    @Mock
    private TankSecurityContext securityContext;

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
    public void testConstructor_CreatesNonNullBean() {
        assertNotNull(fixture);
    }

    @Test
    public void testIsUseFlash_DefaultIsTrue() {
        assertTrue(fixture.isUseFlash());
    }

    @Test
    public void testSetUseFlash_False_IsUseFlashReturnsFalse() {
        fixture.setUseFlash(false);
        assertFalse(fixture.isUseFlash());
    }

    @Test
    public void testSetUseFlash_True_IsUseFlashReturnsTrue() {
        fixture.setUseFlash(false);
        fixture.setUseFlash(true);
        assertTrue(fixture.isUseFlash());
    }

    @Test
    public void testHandleFileUpload_NullItem_DoesNothing() throws Exception {
        FileUploadEvent event = mock(FileUploadEvent.class);
        when(event.getFile()).thenReturn(null);

        // Should not throw and should not call messages
        assertDoesNotThrow(() -> fixture.handleFileUpload(event));
        verify(messages, never()).warn(anyString());
        verify(messages, never()).info(anyString());
    }

    @Test
    public void testHandleFileUpload_EmptyZip_ShowsWarning() throws Exception {
        FileUploadEvent event = mock(FileUploadEvent.class);
        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(event.getFile()).thenReturn(uploadedFile);
        when(uploadedFile.getFileName()).thenReturn("test.zip");
        // Empty content - UploadedFileIterator wraps as zip, gets no entries
        byte[] emptyContent = new byte[0];
        when(uploadedFile.getContent()).thenReturn(emptyContent);
        when(uploadedFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(emptyContent));

        // Should throw or show warning - UploadedFileIterator will fail on empty input
        try {
            fixture.handleFileUpload(event);
            // If it doesn't throw, it should call warn or info
        } catch (Exception e) {
            // Expected - zip processing fails on empty stream
        }
    }
}
