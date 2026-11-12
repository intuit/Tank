package com.intuit.tank.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ClassicRedirectFilterTest {

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain chain = mock(FilterChain.class);
    private final ServletContext servletContext = mock(ServletContext.class);

    @BeforeEach
    public void setUp() {
        when(request.getServletContext()).thenReturn(servletContext);
    }

    @Test
    public void redirectsAnOldPageToClassic() throws Exception {
        when(servletContext.getContextPath()).thenReturn("/tank");
        when(request.getServletPath()).thenReturn("/projects/index.jsf");

        new ClassicRedirectFilter().doFilter(request, response, chain);

        verify(response).sendRedirect("/tank/classic/projects/index.jsf");
        verifyNoInteractions(chain);
    }

    @Test
    public void keepsTheQueryStringSoAnSsoCodeArrives() throws Exception {
        when(servletContext.getContextPath()).thenReturn("");
        when(request.getServletPath()).thenReturn("/login.jsf");
        when(request.getQueryString()).thenReturn("code=abc&state=xyz");

        new ClassicRedirectFilter().doFilter(request, response, chain);

        verify(response).sendRedirect("/classic/login.jsf?code=abc&state=xyz");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/classic/projects/index.jsf", "/classic/login.jsf", "/jakarta.faces.resource/theme.css.jsf" })
    public void passesClassicPagesAndFacesResourcesThrough(String servletPath) throws Exception {
        when(request.getServletPath()).thenReturn(servletPath);

        new ClassicRedirectFilter().doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }
}
