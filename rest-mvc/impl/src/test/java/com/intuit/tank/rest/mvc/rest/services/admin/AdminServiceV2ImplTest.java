/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.admin;

import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.dao.PagedResult;
import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.AdminGroup;
import com.intuit.tank.rest.mvc.rest.models.AdminUser;
import com.intuit.tank.rest.mvc.rest.models.AdminUserRequest;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.LogLevelSetting;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridge;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridgeProvider;
import com.intuit.tank.rest.mvc.rest.util.LogLevels;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceV2ImplTest {

    @InjectMocks
    private AdminServiceV2Impl service;

    @Mock
    private WebSessionBridgeProvider bridgeProvider;

    @Mock
    private WebSessionBridge bridge;

    private AutoCloseable mocks;
    private MockedConstruction<TankConfig> tankConfigs;
    private MockedConstruction<UserDao> userDaos;
    private MockedConstruction<GroupDao> groupDaos;
    private MockedConstruction<ProjectDao> projectDaos;
    private MockedConstruction<PreferencesDao> preferencesDaos;

    /** users by id, as the mocked UserDao finds them */
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicReference<User> saved = new AtomicReference<>();
    private final List<Project> ownedByBob = new ArrayList<>();
    private Preferences bobPreferences;
    private User bob;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        when(bridgeProvider.get()).thenReturn(bridge);
        useConfig(true, Map.of());

        SecurityConfig securityConfig = mock(SecurityConfig.class);
        when(securityConfig.getGroups()).thenReturn(Set.of("admin", "user", "projects"));
        when(securityConfig.getDefaultGroups()).thenReturn(Set.of("user"));
        tankConfigs = Mockito.mockConstruction(TankConfig.class,
                (mock, context) -> when(mock.getSecurityConfig()).thenReturn(securityConfig));

        bob = account(2, "bob", "user");
        bob.generateApiToken();
        users.put(1, account(1, "alice", "admin"));
        users.put(2, bob);
        userDaos = Mockito.mockConstruction(UserDao.class, (mock, context) -> {
            when(mock.findById(any())).thenAnswer(i -> users.get((Integer) i.getArgument(0)));
            when(mock.findByUserName(any())).thenAnswer(i -> users.values().stream()
                    .filter(u -> u.getName().equals(i.getArgument(0))).findFirst().orElse(null));
            when(mock.saveOrUpdate(any())).thenAnswer(i -> {
                User u = i.getArgument(0);
                if (u.getId() == 0) {
                    u.setId(99);
                }
                saved.set(u);
                return u;
            });
            when(mock.deleteUserData(any())).thenAnswer(i -> users.values().stream()
                    .anyMatch(u -> u.getName().equals(i.getArgument(0))) ? 1L : 0L);
            when(mock.findPaged(any())).thenReturn(new PagedResult<>(List.of(users.get(1), bob), 2));
        });
        groupDaos = Mockito.mockConstruction(GroupDao.class,
                (mock, context) -> when(mock.getOrCreateGroup(any())).thenAnswer(i -> new Group(i.getArgument(0))));
        projectDaos = Mockito.mockConstruction(ProjectDao.class,
                (mock, context) -> when(mock.listForOwner("bob")).thenReturn(ownedByBob));
        preferencesDaos = Mockito.mockConstruction(PreferencesDao.class,
                (mock, context) -> when(mock.getForOwner("bob")).thenAnswer(i -> bobPreferences));
        actAs(user("alice", "admin"));
    }

    @AfterEach
    void tearDown() throws Exception {
        preferencesDaos.close();
        projectDaos.close();
        groupDaos.close();
        userDaos.close();
        tankConfigs.close();
        mocks.close();
        reset();
    }

    private static User account(int id, String name, String... groups) {
        User user = User.builder().name(name).email(name + "@example.com").password(PasswordEncoder.encodePassword("pw")).build();
        user.setId(id);
        for (String g : groups) {
            user.addGroup(new Group(g));
        }
        return user;
    }

    // ---------------------------------------------------------------- access

    @Test
    void everyEndpointNeedsASignedInAdmin() {
        actAs(user("bob", "user"));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.listUsers(null, null, null, null));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.getUser(2));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.deleteUser(1));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.createApiToken(1));
        assertThrows(GenericServiceForbiddenAccessException.class, service::getGroups);
        assertThrows(GenericServiceForbiddenAccessException.class, service::listLogFiles);
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> service.setLogLevel(new LogLevelSetting("DEBUG", null)));
    }

    @Test
    void agentTokenAndAnonymousCallersAreRefusedEvenWithSecurityOff() {
        useConfig(false, Map.of());
        actAs(new TankPrincipal("agent", Set.of(), TankPrincipal.AuthMethod.AGENT_TOKEN));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.getUser(2));
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.getUser(2));
    }

    // ---------------------------------------------------------------- read

    @Test
    void listUsers_pagesAndMasksTokens() {
        PageResponse<AdminUser> page = service.listUsers(0, 10, "email,desc", "b");

        assertEquals(2, page.total());
        AdminUser listedBob = page.items().get(1);
        assertTrue(listedBob.hasApiToken());
        assertEquals(bob.getApiToken().substring(bob.getApiToken().length() - 4), listedBob.apiTokenHint());
        assertEquals(List.of("user"), listedBob.groups());
        ArgumentCaptor<PagedQuery> query = ArgumentCaptor.forClass(PagedQuery.class);
        verify(userDaos.constructed().get(0)).findPaged(query.capture());
        assertEquals("email", query.getValue().sortProperty());
        assertFalse(query.getValue().ascending());
        assertEquals("b", query.getValue().search());
    }

    @Test
    void listUsers_rejectsUnknownSort() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.listUsers(0, 10, "password", null));
    }

    @Test
    void getUser_notFound() {
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getUser(404));
    }

    // ---------------------------------------------------------------- create

    @Test
    void createUser_encodesPasswordAndDefaultsGroups() {
        AdminUser created = service.createUser(new AdminUserRequest(" carol ", "carol@example.com", "long-enough", null));

        assertEquals(99, created.id());
        assertEquals("carol", created.name());
        assertEquals(List.of("user"), created.groups());
        assertFalse(created.hasApiToken());
        assertTrue(PasswordEncoder.validatePassword("long-enough", saved.get().getPassword()));
        verify(bridge).userChanged(saved.get());
    }

    @Test
    void createUser_withExplicitGroups() {
        AdminUser created = service.createUser(
                new AdminUserRequest("carol", "carol@example.com", "long-enough", List.of("projects", "admin")));
        assertEquals(List.of("admin", "projects"), created.groups());
    }

    @Test
    void createUser_validates() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.createUser(null));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.createUser(new AdminUserRequest(" ", "c@example.com", "long-enough", null)));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.createUser(new AdminUserRequest("carol", "not-an-email", "long-enough", null)));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.createUser(new AdminUserRequest("carol", "c@example.com", null, null)));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.createUser(new AdminUserRequest("carol", "c@example.com", "short", null)));
        GenericServiceBadRequestException unknown = assertThrows(GenericServiceBadRequestException.class,
                () -> service.createUser(new AdminUserRequest("carol", "c@example.com", "long-enough", List.of("root"))));
        assertTrue(unknown.getMessage().contains("root"));
        assertNull(saved.get());
    }

    @Test
    void createUser_duplicateNameIsAConflict() {
        assertThrows(GenericServiceConflictException.class,
                () -> service.createUser(new AdminUserRequest("bob", "b2@example.com", "long-enough", null)));
    }

    @Test
    void createRequest_toStringHidesPassword() {
        assertFalse(new AdminUserRequest("carol", "c@example.com", "secret-pw", null).toString().contains("secret-pw"));
    }

    // ---------------------------------------------------------------- update

    @Test
    void updateUser_changesOnlyGivenFields() {
        String oldPassword = bob.getPassword();

        AdminUser updated = service.updateUser(2, new AdminUserRequest(null, "new@example.com", null, List.of("projects")));

        assertEquals("new@example.com", updated.email());
        assertEquals(List.of("projects"), updated.groups());
        assertEquals(oldPassword, bob.getPassword());
        verify(bridge).userChanged(bob);
    }

    @Test
    void updateUser_setsNewPassword() {
        service.updateUser(2, new AdminUserRequest("bob", null, "brand-new-pw", null));
        assertTrue(PasswordEncoder.validatePassword("brand-new-pw", bob.getPassword()));
        assertEquals(List.of("user"), service.getUser(2).groups());
    }

    @Test
    void updateUser_nameCannotChange() {
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.updateUser(2, new AdminUserRequest("robert", null, null, null)));
    }

    @Test
    void updateUser_adminCannotDropOwnAdminGroup() {
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.updateUser(1, new AdminUserRequest(null, null, null, List.of("user"))));
        service.updateUser(1, new AdminUserRequest(null, null, null, List.of("admin", "user")));
    }

    // ---------------------------------------------------------------- delete

    @Test
    void deleteUser_anonymizesThenDeletes() {
        service.deleteUser(2);

        UserDao dao = userDaos.constructed().get(userDaos.constructed().size() - 1);
        org.mockito.InOrder order = inOrder(dao);
        order.verify(dao).deleteUserData("bob");
        order.verify(dao).delete(2);
        verify(bridge).userChanged(bob);
    }

    @Test
    void deleteUser_refusedWhileOwningProjects() {
        for (int i = 0; i < 12; i++) {
            Project p = new Project();
            p.setName("p" + i);
            ownedByBob.add(p);
        }
        GenericServiceConflictException e = assertThrows(GenericServiceConflictException.class, () -> service.deleteUser(2));
        assertTrue(e.getMessage().contains("12 project(s)"));
        assertTrue(e.getMessage().contains("p0"));
        assertTrue(e.getMessage().contains("and 2 more"));
        userDaos.constructed().forEach(dao -> verify(dao, never()).deleteUserData(any()));
    }

    @Test
    void deleteUser_cannotDeleteSelf() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.deleteUser(1));
    }

    @Test
    void deleteUser_rowDeleteFailureStillSucceedsOnceAnonymized() {
        userDaos.close();
        userDaos = Mockito.mockConstruction(UserDao.class, (mock, context) -> {
            when(mock.findById(2)).thenReturn(bob);
            when(mock.deleteUserData("bob")).thenReturn(1L);
            doThrow(new RuntimeException("constraint")).when(mock).delete(2);
        });
        assertDoesNotThrow(() -> service.deleteUser(2));
    }

    // ---------------------------------------------------------------- tokens and preferences

    @Test
    void createApiToken_replacesTokenAndReturnsItOnce() {
        String old = bob.getApiToken();
        ApiTokenResponse response = service.createApiToken(2);
        assertNotNull(response.apiToken());
        assertNotEquals(old, response.apiToken());
        assertEquals(response.apiToken(), bob.getApiToken());
    }

    @Test
    void deleteApiToken_removesToken() {
        service.deleteApiToken(2);
        assertNull(bob.getApiToken());
        assertFalse(service.getUser(2).hasApiToken());
    }

    @Test
    void resetPreferences_deletesAndNotifies() {
        bobPreferences = new Preferences();
        service.resetPreferences(2);
        verify(preferencesDaos.constructed().get(0)).delete(bobPreferences);
        verify(bridge).preferencesChanged(bobPreferences);
    }

    @Test
    void resetPreferences_noneIsANoOp() {
        service.resetPreferences(2);
        verify(preferencesDaos.constructed().get(0), never()).delete(any(Preferences.class));
    }

    @Test
    void bridgeFailureDoesNotFailTheSave() {
        when(bridgeProvider.get()).thenThrow(new RuntimeException("no CDI"));
        assertDoesNotThrow(() -> service.updateUser(2, new AdminUserRequest(null, "x@example.com", null, null)));
    }

    // ---------------------------------------------------------------- groups and logs

    @Test
    void getGroups_sortedWithDefaults() {
        assertEquals(List.of(new AdminGroup("admin", false), new AdminGroup("projects", false), new AdminGroup("user", true)),
                service.getGroups());
    }

    @Test
    void logLevel_roundTripsAndValidates() {
        Level original = LogLevels.current();
        try {
            LogLevelSetting set = service.setLogLevel(new LogLevelSetting("warn", null));
            assertEquals("WARN", set.level());
            assertEquals("WARN", service.getLogLevel().level());
            assertThrows(GenericServiceBadRequestException.class, () -> service.setLogLevel(new LogLevelSetting("LOUD", null)));
            assertThrows(GenericServiceBadRequestException.class, () -> service.setLogLevel(null));
        } finally {
            LogLevels.set(original);
        }
    }
}
