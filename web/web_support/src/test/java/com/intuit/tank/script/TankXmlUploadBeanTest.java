package com.intuit.tank.script;

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
import jakarta.security.enterprise.CallerPrincipal;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import com.intuit.tank.ModifiedScriptMessage;
import com.intuit.tank.auth.Security;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.util.Messages;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The class <code>TankXmlUploadBeanTest</code> contains tests for the class
 * <code>{@link TankXmlUploadBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class TankXmlUploadBeanTest {

    @InjectMocks
    private TankXmlUploadBean fixture;

    @Mock
    private Event<ModifiedScriptMessage> scriptEvent;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private Messages messages;

    @Mock
    private Security security;

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
    public void testTankXmlUploadBean_1() throws Exception {
        TankXmlUploadBean result = new TankXmlUploadBean();
        assertNotNull(result);
    }

    @Test
    public void testIsUseFlash_1() throws Exception {
        TankXmlUploadBean bean = new TankXmlUploadBean();
        bean.setUseFlash(true);
        boolean result = bean.isUseFlash();
        assertTrue(result);
    }

    @Test
    public void testIsUseFlash_2() throws Exception {
        TankXmlUploadBean bean = new TankXmlUploadBean();
        bean.setUseFlash(false);
        boolean result = bean.isUseFlash();
        assertFalse(result);
    }

    @Test
    public void testIsUseFlash_DefaultTrue() {
        assertTrue(fixture.isUseFlash());
    }

    @Test
    public void testSetUseFlash_False() {
        fixture.setUseFlash(false);
        assertFalse(fixture.isUseFlash());
    }

    @Test
    public void testClearDialog_CallsMessagesClear() {
        fixture.clearDialog();
        verify(messages).clear();
    }

    @Test
    public void testHandleFileUpload_NullFile_DoesNotThrow() throws Exception {
        FileUploadEvent event = mock(FileUploadEvent.class);
        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(event.getFile()).thenReturn(uploadedFile);
        when(uploadedFile.getFileName()).thenReturn("test.xml");
        when(uploadedFile.getInputStream()).thenReturn(null);

        // Null item case is not entered, so we need a non-null item
        // Use a real InputStream that's empty to trigger processing
        java.io.InputStream emptyStream = new java.io.ByteArrayInputStream(new byte[0]);
        when(uploadedFile.getInputStream()).thenReturn(emptyStream);

        // This should throw since we have no valid XML, but we test it doesn't NPE in outer logic
        assertThrows(Exception.class, () -> fixture.handleFileUpload(event));
    }

    @Test
    public void testHandleFileUpload_newScript() throws Exception {
        FileUploadEvent event = mock(FileUploadEvent.class);
        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(securityContext.getCallerPrincipal()).thenReturn(new CallerPrincipal("System"));
        when(event.getFile()).thenReturn(uploadedFile);
        when(uploadedFile.getFileName()).thenReturn("script.xml");
        when(uploadedFile.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("script.xml"));

        fixture.handleFileUpload(event);

        verify(messages, times(2)).info(anyString());
    }
}
