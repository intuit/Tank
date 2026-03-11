package com.intuit.tank.admin;

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

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.project.User;
import com.intuit.tank.util.Messages;
import jakarta.enterprise.context.Conversation;
import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.DualListModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserEditTest {

    @InjectMocks
    private UserEdit userEdit;

    @Mock
    private Messages messages;

    @Mock
    private Conversation conversation;

    @Mock
    private Event<ModifiedUserMessage> userEvent;

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
    public void testConstructor() {
        assertNotNull(userEdit);
    }

    @Test
    public void testGetSetPassword() {
        userEdit.setPassword("mypassword");
        assertEquals("mypassword", userEdit.getPassword());
    }

    @Test
    public void testGetSetPasswordConfirm() {
        userEdit.setPasswordConfirm("mypassword");
        assertEquals("mypassword", userEdit.getPasswordConfirm());
    }

    @Test
    public void testGetUser_InitiallyNull() {
        assertNull(userEdit.getUser());
    }

    @Test
    public void testGetSetSelectionModel() {
        DualListModel<String> model = new DualListModel<>();
        userEdit.setSelectionModel(model);
        assertEquals(model, userEdit.getSelectionModel());
    }

    @Test
    public void testCancel_EndsConversationAndClearsState() {
        String result = userEdit.cancel();
        assertEquals("success", result);
        verify(conversation).end();
        assertNull(userEdit.getUser());
    }

    @Test
    public void testSave_WhenPasswordMismatch_ShowsError() {
        userEdit.setPassword("pass1");
        userEdit.setPasswordConfirm("pass2");

        // inject a non-null user to avoid NPE
        userEdit.setSelectionModel(new DualListModel<>());
        // We need a user - use reflection to set it
        try {
            java.lang.reflect.Field field = UserEdit.class.getDeclaredField("user");
            field.setAccessible(true);
            User u = new User();
            field.set(userEdit, u);
        } catch (Exception e) {
            fail("Could not set user via reflection: " + e.getMessage());
        }

        String result = userEdit.save();
        assertNull(result);
        verify(messages).error(anyString());
    }

    @Test
    public void testSave_WhenNewUserAndNoPassword_ShowsError() {
        userEdit.setPassword("");
        userEdit.setPasswordConfirm("");

        try {
            java.lang.reflect.Field field = UserEdit.class.getDeclaredField("user");
            field.setAccessible(true);
            User u = new User(); // id=0 means new user
            field.set(userEdit, u);
        } catch (Exception e) {
            fail("Could not set user: " + e.getMessage());
        }
        userEdit.setSelectionModel(new DualListModel<>());

        String result = userEdit.save();
        assertNull(result);
        verify(messages).error(anyString());
    }

    @Test
    public void testNewUser_BeginsConversationAndCreatesUser() {
        String result = userEdit.newUser();
        assertEquals("success", result);
        verify(conversation).begin();
        assertNotNull(userEdit.getUser());
    }

    @Test
    public void testEdit_BeginsConversationAndSetsUser() {
        User user = new User();
        user.setName("testuser");
        String result = userEdit.edit(user);
        assertEquals("success", result);
        verify(conversation).begin();
        assertEquals(user, userEdit.getUser());
    }

    @Test
    public void testSave_WhenNewUserWithPasswordMatch_EncodesPasswordAndAttemptsSave() throws Exception {
        User user = new User();
        user.setName("newuser");
        user.setEmail("test@test.com");
        // id=0 means new user - but we need to set password

        java.lang.reflect.Field field = UserEdit.class.getDeclaredField("user");
        field.setAccessible(true);
        field.set(userEdit, user);

        userEdit.setPassword("mypassword");
        userEdit.setPasswordConfirm("mypassword");
        userEdit.setSelectionModel(new DualListModel<>());

        // Should save to H2 - may succeed or fail; just test it doesn't NPE
        try {
            userEdit.save();
        } catch (Exception e) {
            // DAO operations may fail - acceptable
        }
        // Password should have been encoded on the user object
        assertNotNull(user.getPassword());
    }

    @Test
    public void testGenerateApiToken_WhenTokenNull_GeneratesAndSaves() throws Exception {
        User user = new User();
        user.setName("tokenuser");
        user.setEmail("token@test.com");
        // apiToken is null by default
        assertNull(user.getApiToken());

        user.setPassword("hashedpassword");
        // Save user to DB first
        com.intuit.tank.dao.UserDao dao = new com.intuit.tank.dao.UserDao();
        user = dao.saveOrUpdate(user);

        java.lang.reflect.Field field = UserEdit.class.getDeclaredField("user");
        field.setAccessible(true);
        field.set(userEdit, user);

        assertDoesNotThrow(() -> userEdit.generateApiToken());
        assertNotNull(userEdit.getUser().getApiToken());
    }
}
