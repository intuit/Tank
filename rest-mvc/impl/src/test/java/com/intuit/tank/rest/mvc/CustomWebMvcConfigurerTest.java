package com.intuit.tank.rest.mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import jakarta.servlet.ServletContext;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomWebMvcConfigurerTest {

    @InjectMocks
    private CustomWebMvcConfigurer configurer;

    @Mock
    private ServletContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addViewControllers_registersViewsFromServletContext() {
        when(context.getResourcePaths("/")).thenReturn(Set.of("/app/", "/docs/"));

        ViewControllerRegistry registry = mock(ViewControllerRegistry.class);
        ViewControllerRegistration registration = mock(ViewControllerRegistration.class);
        when(registry.addViewController(anyString())).thenReturn(registration);

        configurer.addViewControllers(registry);

        // Should register root "/" plus entries for each resource path
        verify(registry).addViewController("/");
        verify(registry, atLeast(4)).addViewController(anyString()); // root + 2 redirects + 2 forwards
    }

    @Test
    void addInterceptors_registersLoggingInterceptor() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(any(LoggingInterceptor.class))).thenReturn(registration);

        configurer.addInterceptors(registry);

        verify(registry).addInterceptor(any(LoggingInterceptor.class));
    }
}
