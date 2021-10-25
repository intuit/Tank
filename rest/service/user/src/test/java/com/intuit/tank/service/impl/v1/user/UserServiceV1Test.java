package com.intuit.tank.service.impl.v1.user;

import com.intuit.tank.api.model.v1.user.UserCredentials;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceV1Test {
    @Mock
    private UserDao _userDaoMock;

    @Mock
    private UserCredentials _userCredentialsMock;

    @InjectMocks
    private UserServiceV1 _sut;

    private AutoCloseable closeable;


    private List<User> _listUsersStub;
    private User _userStub;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);

        _userStub = new User();
        _listUsersStub = new ArrayList<>();
        _listUsersStub.add(_userStub);
        _listUsersStub.add(_userStub);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    //region getAllUsers

    @Test
    public void GetAllUsers_Given_Existing_Users_Calls_User_Data_Accessor() {
        // Arrange
        when(_userDaoMock.findAll()).thenReturn(_listUsersStub);

        // Act
        _sut.getAllUsers();

        // Assert
        verify(_userDaoMock, times(1)).findAll();
    }

    @Test
    public void GetAllUsers_Given_Existing_Users_Gets_Ok_Status_Code() {
        // Arrange
        when(_userDaoMock.findAll()).thenReturn(_listUsersStub);

        // Act
        Response response = _sut.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void GetAllUsers_Given_Exception_Throws_WebApplicationException() {
        // Arrange
        doThrow(new WebApplicationException()).when(_userDaoMock).findAll();

        // Act + Assert
        assertThrows(WebApplicationException.class, () -> {
            _sut.getAllUsers();
        });
    }

    //endregion

    //region getUserByUsername

    @Test
    public void GetUserByUsername_Given_Existing_User_Calls_User_Data_Accessor() {
        // Arrange
        when(_userDaoMock.findByUserName(any(String.class))).thenReturn(_userStub);

        // Act
        _sut.getUserByUsername("TestName");

        // Assert
        verify(_userDaoMock, times(1)).findByUserName(any(String.class));
    }

    @Test
    public void GetUserByUsername_Given_Existing_User_Gets_Ok_Status_Code() {
        // Arrange
        when(_userDaoMock.findByUserName(any(String.class))).thenReturn(_userStub);

        // Act
        Response response = _sut.getUserByUsername("TestName");

        // Assert
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void GetUserByUsername_Given_Not_Existing_Sets_Not_Found_Status() {
        // Arrange
        when(_userDaoMock.findByUserName(any(String.class))).thenReturn(null);

        // Act
        Response response = _sut.getUserByUsername("TestName");

        // Assert
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void GetUserByUsername_Given_Exception_Throws_WebApplicationException() {
        // Arrange
        doThrow(new WebApplicationException()).when(_userDaoMock).findByUserName(any(String.class));

        // Act + Assert
        assertThrows(WebApplicationException.class, () -> {
            _sut.getUserByUsername("Name");
        });
    }

    //endregion

    //region getUserByEmail

    @Test
    public void GetUserByEmail_Given_Existing_User_Calls_User_Data_Accessor() {
        // Arrange
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(_userStub);

        // Act
        _sut.getUserByEmail("TestEmail");

        // Assert
        verify(_userDaoMock, times(1)).findByEmail(any(String.class));
    }

    @Test
    public void GetUserByEmail_Given_Existing_User_Gets_Ok_Status_Code() {
        // Arrange
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(_userStub);

        // Act
        Response response = _sut.getUserByEmail("TestEmail");

        // Assert
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void GetUserByEmail_Given_Not_Existing_Sets_Not_Found_Status() {
        // Arrange
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(null);

        // Act
        Response response = _sut.getUserByEmail("TestEmail");

        // Assert
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void GetUserByEmail_Given_Exception_Throws_WebApplicationException() {
        // Arrange
        doThrow(new WebApplicationException()).when(_userDaoMock).findByEmail(any(String.class));

        // Act + Assert
        assertThrows(WebApplicationException.class, () -> {
            _sut.getUserByEmail("Name");
        });
    }

    //endregion

    //region authenticate

    @Test
    public void Authenticate_Given_Existing_Credentials_Calls_User_Data_Accessor() {
        // Arrange
        when(_userCredentialsMock.getName()).thenReturn("TestName");
        when(_userCredentialsMock.getPass()).thenReturn("TestPass");
        when(_userDaoMock.authenticate(any(String.class), any(String.class))).thenReturn(_userStub);

        // Act
        _sut.authenticate(_userCredentialsMock);

        // Assert
        verify(_userDaoMock, times(1)).authenticate(any(String.class), any(String.class));
    }

    @Test
    public void Authenticate_Given_Existing_Credentials_Gets_Ok_Status_Code() {
        // Arrange
        when(_userCredentialsMock.getName()).thenReturn("TestName");
        when(_userCredentialsMock.getPass()).thenReturn("TestPass");
        when(_userDaoMock.authenticate(any(String.class), any(String.class))).thenReturn(_userStub);

        // Act
        Response response = _sut.authenticate(_userCredentialsMock);

        // Assert
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void Authenticate_Given_Not_Credentials_Sets_Unauthorized_Status() {
        // Arrange
        when(_userDaoMock.authenticate(any(String.class), any(String.class))).thenReturn(null);

        // Act
        Response response = _sut.authenticate(_userCredentialsMock);

        // Assert
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void Authenticate_Given_Exception_Throws_WebApplicationException() {
        // Arrange
        when(_userCredentialsMock.getName()).thenReturn("TestName");
        when(_userCredentialsMock.getPass()).thenReturn("TestPass");
        doThrow(new WebApplicationException()).when(_userDaoMock).authenticate(any(String.class), any(String.class));

        // Act + Assert
        assertThrows(WebApplicationException.class, () -> {
            _sut.authenticate(_userCredentialsMock);
        });
    }

    //endregion
}
