package com.intuit.tank.config;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.TankSsoHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * LoginFilterTest
 *
 * @author Shawn Park
 */
public class LoginFilterTest {

    @Mock
    private TankSecurityContext _tankSecurityContextMock;
    @Mock
    private TankSsoHandler _tankSsoHandlerMock;
    @Mock
    private ServletRequest _servletRequestMock;
    @Mock
    private ServletResponse _servletResponseMock;
    @Mock
    private FilterChain _filterChainResponseMock;
    @Mock
    private Principal _principalMock;

    @InjectMocks
    private LoginFilter _sut;

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
    public void DoFilter_Given_Authorization_Code_Parameter_Call_SSO_Handler() throws IOException, ServletException {
        // Arrange
        when(_servletRequestMock.getParameter(any(String.class))).thenReturn("testAuthCodeParameter");

        // Act
        _sut.doFilter(_servletRequestMock, _servletResponseMock, _filterChainResponseMock);

        // Assert
        verify(_tankSsoHandlerMock, times(1)).HandleSsoAuthorization(any(String.class));
    }

    @Test
    public void DoFilter_With_No_Authorization_Code_Parameter_Does_Not_Call_SSO_Handler() throws IOException, ServletException {
        // Arrange
        when(_tankSecurityContextMock.getCallerPrincipal()).thenReturn(_principalMock);

        // Act
        _sut.doFilter(_servletRequestMock, _servletResponseMock, _filterChainResponseMock);

        // Assert
        verify(_tankSsoHandlerMock, times(0)).HandleSsoAuthorization(any(String.class));
    }
}
