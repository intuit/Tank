package com.intuit.tank.config;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * AdminFilter
 *
 * @author Kevin McGoldrick
 *
 */
public class AdminFilter implements Filter {
    private static final Logger LOG = LogManager.getLogger(AdminFilter.class);

    @Inject
    private TankSecurityContext securityContext;

    @Override
    public void init(FilterConfig arg0) throws ServletException {}

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

    @Override
    public void destroy() {}

}
