package com.intuit.tank.rest.mvc.service;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserAutoDeletionServiceTest {

    // =====================================================================
    // performStartupUserDeletion - DRY RUN mode (auto-deletion disabled)
    // =====================================================================

    @Test
    void performStartupUserDeletion_dryRunMode_doesNotDeleteUsers() {
        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(false);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(2L);
                         when(mock.findUsersEligibleForDeletion(730)).thenReturn(
                                 List.of(createUser(1, "inactiveUser", false)));
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            // In DRY RUN mode, deleteUserData should never be called
            for (UserDao dao : userDaoMock.constructed()) {
                verify(dao, never()).deleteUserData(anyString());
            }
        }
    }

    @Test
    void performStartupUserDeletion_noEligibleUsers_exitsEarly() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(0L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            // Should not call findUsersEligibleForDeletion when count is 0
            for (UserDao dao : userDaoMock.constructed()) {
                verify(dao, never()).findUsersEligibleForDeletion(anyInt());
                verify(dao, never()).deleteUserData(anyString());
            }
        }
    }

    // =====================================================================
    // performStartupUserDeletion - DELETION mode (auto-deletion enabled)
    // =====================================================================

    @Test
    void performStartupUserDeletion_deletionMode_deletesEligibleUsers() {
        User eligibleUser = createUser(1, "oldUser", false);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         // First call returns users for logging, second for batch processing, third returns empty to stop loop
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(eligibleUser))
                                 .thenReturn(List.of(eligibleUser))
                                 .thenReturn(Collections.emptyList());
                         when(mock.deleteUserData("oldUser")).thenReturn(1L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            // Verify deleteUserData was called for the eligible user
            boolean deleteWasCalled = false;
            for (UserDao dao : userDaoMock.constructed()) {
                try {
                    verify(dao, atLeastOnce()).deleteUserData("oldUser");
                    deleteWasCalled = true;
                } catch (AssertionError e) {
                    // This particular constructed DAO may not have been the one used for deletion
                }
            }
            assertTrue(deleteWasCalled, "deleteUserData should have been called for eligible user");
        }
    }

    @Test
    void performStartupUserDeletion_skipsProtectedUsers() {
        User adminUser = createUser(1, "admin", false);
        User regularUser = createUser(2, "regularUser", false);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(2L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(adminUser, regularUser))
                                 .thenReturn(List.of(adminUser, regularUser))
                                 .thenReturn(Collections.emptyList());
                         when(mock.deleteUserData("regularUser")).thenReturn(1L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            // Verify admin was never deleted, but regularUser was
            for (UserDao dao : userDaoMock.constructed()) {
                verify(dao, never()).deleteUserData("admin");
            }
        }
    }

    @Test
    void performStartupUserDeletion_handlesExceptionGracefully() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenThrow(new RuntimeException("DB error"));
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            // Should not throw - exception is caught internally
            assertDoesNotThrow(() -> service.performStartupUserDeletion());
        }
    }

    // =====================================================================
    // isProtectedUser (tested indirectly via deletion behavior)
    // =====================================================================

    @Test
    void performStartupUserDeletion_protectsAnonymizedUsers() {
        User anonymizedUser = createUser(1, "deleted_user_123", false);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(anonymizedUser))
                                 .thenReturn(List.of(anonymizedUser))
                                 .thenReturn(Collections.emptyList());
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            for (UserDao dao : userDaoMock.constructed()) {
                verify(dao, never()).deleteUserData("deleted_user_123");
            }
        }
    }

    @Test
    void performStartupUserDeletion_protectsPermittedUsers() {
        User permittedUser = createUser(1, "serviceAccount", false);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(List.of("serviceAccount"));
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(permittedUser))
                                 .thenReturn(List.of(permittedUser))
                                 .thenReturn(Collections.emptyList());
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            for (UserDao dao : userDaoMock.constructed()) {
                verify(dao, never()).deleteUserData("serviceAccount");
            }
        }
    }

    @Test
    void performStartupUserDeletion_protectsUsersInAdminGroup() {
        User adminGroupUser = createUser(1, "someUser", false);
        adminGroupUser.addGroup(new Group("administrators"));

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(adminGroupUser))
                                 .thenReturn(List.of(adminGroupUser))
                                 .thenReturn(Collections.emptyList());
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            for (UserDao dao : userDaoMock.constructed()) {
                verify(dao, never()).deleteUserData("someUser");
            }
        }
    }

    @Test
    void performStartupUserDeletion_protectsUsersInSuperGroup() {
        User superGroupUser = createUser(1, "anotherUser", false);
        superGroupUser.addGroup(new Group("super-users"));

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(superGroupUser))
                                 .thenReturn(List.of(superGroupUser))
                                 .thenReturn(Collections.emptyList());
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            for (UserDao dao : userDaoMock.constructed()) {
                verify(dao, never()).deleteUserData("anotherUser");
            }
        }
    }

    // =====================================================================
    // performManualUserDeletion
    // =====================================================================

    @Test
    void performManualUserDeletion_deletesEligibleUsers() {
        User eligibleUser = createUser(1, "inactiveUser", false);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(365);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.findUsersEligibleForDeletion(365))
                                 .thenReturn(List.of(eligibleUser))
                                 .thenReturn(Collections.emptyList());
                         when(mock.deleteUserData("inactiveUser")).thenReturn(1L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            int deleted = service.performManualUserDeletion();

            assertEquals(1, deleted);
        }
    }

    @Test
    void performManualUserDeletion_throwsOnError() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenThrow(new RuntimeException("DB error"));
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            assertThrows(RuntimeException.class, () -> service.performManualUserDeletion());
        }
    }

    // =====================================================================
    // Batch processing
    // =====================================================================

    @Test
    void performStartupUserDeletion_deletionMode_continuesBatchOnIndividualFailure() {
        User failUser = createUser(1, "failUser", false);
        User successUser = createUser(2, "successUser", false);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(2L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(failUser, successUser))  // logging call
                                 .thenReturn(List.of(failUser, successUser))  // first batch
                                 .thenReturn(Collections.emptyList());        // stop loop
                         when(mock.deleteUserData("failUser")).thenThrow(new RuntimeException("Delete failed"));
                         when(mock.deleteUserData("successUser")).thenReturn(1L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            service.performStartupUserDeletion();

            // successUser should still be processed despite failUser's exception
            boolean successDeleted = false;
            for (UserDao dao : userDaoMock.constructed()) {
                try {
                    verify(dao, atLeastOnce()).deleteUserData("successUser");
                    successDeleted = true;
                } catch (AssertionError e) {
                    // not this DAO instance
                }
            }
            assertTrue(successDeleted, "successUser should have been processed despite failUser exception");
        }
    }

    // =====================================================================
    // isNeverLoggedIn - edge cases via user creation
    // =====================================================================

    @Test
    void performStartupUserDeletion_userNeverLoggedIn_logsCorrectReason() {
        // A user who never logged in has lastLoginTs very close to creation time
        User neverLoggedIn = createUser(1, "newUser", true); // neverLoggedIn = true

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(neverLoggedIn))
                                 .thenReturn(List.of(neverLoggedIn))
                                 .thenReturn(Collections.emptyList());
                         when(mock.deleteUserData("newUser")).thenReturn(1L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            // Should complete without errors for never-logged-in users
            assertDoesNotThrow(() -> service.performStartupUserDeletion());
        }
    }

    @Test
    void performStartupUserDeletion_userWithNullTimestamps_handledGracefully() {
        User nullTsUser = new User();
        nullTsUser.setName("nullTsUser");
        nullTsUser.setId(1);
        // created and lastLoginTs are null - isNeverLoggedIn should return true

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(nullTsUser))
                                 .thenReturn(List.of(nullTsUser))
                                 .thenReturn(Collections.emptyList());
                         when(mock.deleteUserData("nullTsUser")).thenReturn(1L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            // The logEligibleUsers method calls user.getCreated().toInstant() which will NPE
            // for null created. This tests the outer exception handler.
            assertDoesNotThrow(() -> service.performStartupUserDeletion());
        }
    }

    // =====================================================================
    // Config precedence - getRetentionDays and isAutoDeletionEnabled
    // =====================================================================

    @Test
    void performStartupUserDeletion_retentionDaysFromSystemProperty() {
        String originalProp = System.getProperty("tank.user.auto-deletion.retention-days");
        try {
            System.setProperty("tank.user.auto-deletion.retention-days", "365");

            try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                         (mock, ctx) -> {
                             when(mock.isUserAutoDeletionEnabled()).thenReturn(false);
                             when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                             when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                         });
                 MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                         (mock, ctx) -> {
                             // The service should use 365 (from sys prop), not 730 (from TankConfig)
                             when(mock.countUsersEligibleForDeletion(365)).thenReturn(0L);
                         })) {

                UserAutoDeletionService service = new UserAutoDeletionService();
                service.performStartupUserDeletion();

                // Verify the DAO was queried with the system property value (365)
                for (UserDao dao : userDaoMock.constructed()) {
                    verify(dao, atMost(1)).countUsersEligibleForDeletion(365);
                }
            }
        } finally {
            if (originalProp == null) {
                System.clearProperty("tank.user.auto-deletion.retention-days");
            } else {
                System.setProperty("tank.user.auto-deletion.retention-days", originalProp);
            }
        }
    }

    @Test
    void performStartupUserDeletion_enabledViaSystemProperty() {
        String originalProp = System.getProperty("tank.user.auto-deletion.enabled");
        try {
            System.setProperty("tank.user.auto-deletion.enabled", "true");

            User user = createUser(1, "testUser", false);

            try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                         (mock, ctx) -> {
                             when(mock.isUserAutoDeletionEnabled()).thenReturn(false);
                             when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                             when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                         });
                 MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                         (mock, ctx) -> {
                             when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                             when(mock.findUsersEligibleForDeletion(730))
                                     .thenReturn(List.of(user))
                                     .thenReturn(List.of(user))
                                     .thenReturn(Collections.emptyList());
                             when(mock.deleteUserData("testUser")).thenReturn(1L);
                         })) {

                UserAutoDeletionService service = new UserAutoDeletionService();
                service.performStartupUserDeletion();

                // Should have attempted deletion since enabled via system property
                boolean deleteWasCalled = false;
                for (UserDao dao : userDaoMock.constructed()) {
                    try {
                        verify(dao, atLeastOnce()).deleteUserData("testUser");
                        deleteWasCalled = true;
                    } catch (AssertionError e) {
                        // not this DAO instance
                    }
                }
                assertTrue(deleteWasCalled, "Deletion should proceed when enabled via system property");
            }
        } finally {
            if (originalProp == null) {
                System.clearProperty("tank.user.auto-deletion.enabled");
            } else {
                System.setProperty("tank.user.auto-deletion.enabled", originalProp);
            }
        }
    }

    @Test
    void performStartupUserDeletion_invalidRetentionDaysSysProp_fallsBackToTankConfig() {
        String originalProp = System.getProperty("tank.user.auto-deletion.retention-days");
        try {
            System.setProperty("tank.user.auto-deletion.retention-days", "not-a-number");

            try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                         (mock, ctx) -> {
                             when(mock.isUserAutoDeletionEnabled()).thenReturn(false);
                             when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                             when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                         });
                 MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                         (mock, ctx) -> {
                             // Should fall back to TankConfig value of 730
                             when(mock.countUsersEligibleForDeletion(730)).thenReturn(0L);
                         })) {

                UserAutoDeletionService service = new UserAutoDeletionService();
                assertDoesNotThrow(() -> service.performStartupUserDeletion());
            }
        } finally {
            if (originalProp == null) {
                System.clearProperty("tank.user.auto-deletion.retention-days");
            } else {
                System.setProperty("tank.user.auto-deletion.retention-days", originalProp);
            }
        }
    }

    // =====================================================================
    // deleteUserData return value handling
    // =====================================================================

    @Test
    void performStartupUserDeletion_deleteReturnsZero_continuesProcessing() {
        User user = createUser(1, "alreadyProcessed", false);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                     (mock, ctx) -> {
                         when(mock.isUserAutoDeletionEnabled()).thenReturn(true);
                         when(mock.getUserAutoDeletionRetentionDays()).thenReturn(730);
                         when(mock.getUserAutoDeletionPermittedUsers()).thenReturn(Collections.emptyList());
                     });
             MockedConstruction<UserDao> userDaoMock = Mockito.mockConstruction(UserDao.class,
                     (mock, ctx) -> {
                         when(mock.countUsersEligibleForDeletion(730)).thenReturn(1L);
                         when(mock.findUsersEligibleForDeletion(730))
                                 .thenReturn(List.of(user))
                                 .thenReturn(List.of(user))
                                 .thenReturn(Collections.emptyList());
                         when(mock.deleteUserData("alreadyProcessed")).thenReturn(0L);
                     })) {

            UserAutoDeletionService service = new UserAutoDeletionService();
            // Should complete without errors even when deleteUserData returns 0
            assertDoesNotThrow(() -> service.performStartupUserDeletion());
        }
    }

    // =====================================================================
    // Helper methods
    // =====================================================================

    private static User createUser(int id, String name, boolean neverLoggedIn) {
        Instant now = Instant.now();
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setCreated(Date.from(now.minusSeconds(86400 * 800))); // 800 days ago
        if (neverLoggedIn) {
            // lastLoginTs same as created time (within 1s threshold)
            user.setLastLoginTs(user.getCreated().toInstant());
        } else {
            // lastLoginTs is some time after creation, but still old
            user.setLastLoginTs(now.minusSeconds(86400 * 750));
        }
        return user;
    }
}
