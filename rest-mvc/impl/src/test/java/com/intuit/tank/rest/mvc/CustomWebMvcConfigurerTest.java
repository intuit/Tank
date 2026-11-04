package com.intuit.tank.rest.mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.HandlerInterceptor;
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

        // root "/" plus a redirect and a forward for each folder; /app is registered once, by itself
        verify(registry).addViewController("/");
        verify(registry).addViewController("/docs");
        verify(registry).addViewController("/docs/");
        verify(registry, times(1)).addViewController("/app");
        verify(registry, times(1)).addViewController("/app/");
        verify(registry, times(5)).addViewController(anyString());
    }

    @Test
    void addInterceptors_registersLoggingInterceptor() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(any(HandlerInterceptor.class))).thenReturn(registration);

        configurer.addInterceptors(registry);

        verify(registry).addInterceptor(any(LoggingInterceptor.class));
        verify(registry).addInterceptor(any(CustomWebMvcConfigurer.Utf8Interceptor.class));
    }
}
