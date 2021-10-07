package com.intuit.tank.admin;

import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserCreateTest {
    @Mock
    private UserDao _userDaoMock;
    @Mock
    private User _userMock;
    @Mock
    private UserInfo _userInfoMock;

    @InjectMocks
    private UserCreate _sut;

    private AutoCloseable closeable;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void CreateUser_Given_UserInfo_Call_UserDao_To_Create() {
        // Arrange
        when(_userInfoMock.getUsername()).thenReturn("TestUserName");
        when(_userInfoMock.getEmail()).thenReturn("Test@intuit.com");
        when(_userDaoMock.saveOrUpdate(_userMock)).thenReturn(_userMock);

        // Act
        _sut.CreateUser(_userInfoMock);

        // Assert
        verify(_userDaoMock, atLeastOnce()).saveOrUpdate(any(User.class));
    }

    @Test
    public void CreateUser_Given_Null_UserInfo_Throws_IllegalArgumentException() {
        // Arrange + Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.CreateUser(null);
        });
    }

    @Test
    public void CreateUser_Given_Exception_Throws_Exception() {
        // Arrange
        when(_userInfoMock.getUsername()).thenReturn("TestUserName");
        when(_userInfoMock.getEmail()).thenReturn("Test@intuit.com");
        doThrow(new PersistenceException()).when(_userDaoMock).saveOrUpdate(any(User.class));

        // Act + Assert
        assertThrows(PersistenceException.class, () -> {
            _sut.CreateUser(_userInfoMock);
        });
    }
}
