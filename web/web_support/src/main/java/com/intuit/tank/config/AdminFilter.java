package com.intuit.tank.config;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebFilter(urlPatterns = "/admin/*")
public class AdminFilter implements Filter {
    private static final Logger LOG = LogManager.getLogger(AdminFilter.class);

    @Inject
    private TankSecurityContext securityContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (securityContext.getCallerPrincipal() == null || !securityContext.isCallerInRole(TankConstants.TANK_GROUP_ADMIN)) {
            LOG.warn("Failed to access " + ((HttpServletRequest) request).getRequestURI() + ", lack of permissions");
            ((HttpServletRequest)request).getSession().invalidate();
            String contextPath = ((HttpServletRequest)request).getContextPath();
            ((HttpServletResponse)response).sendRedirect(contextPath);
            return;
        }
        chain.doFilter(request, response);
    }
}
