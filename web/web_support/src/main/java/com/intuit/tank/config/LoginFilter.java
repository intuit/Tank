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

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.exceptions.SegmentNotFoundException;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.TankSsoHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LoginFilter
 * 
 * @author Kevin McGoldrick
 * 
 */
public class LoginFilter implements Filter {
	private static final Logger LOG = LogManager.getLogger(LoginFilter.class);
	private static final String AUTHCODEKEY = "code";

    @Inject
    private TankSecurityContext _securityContext;

	@Inject
	private TankSsoHandler _tankSsoHandler;

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String authorizationCode = request.getParameter(AUTHCODEKEY);
		if (authorizationCode != null) {
			_tankSsoHandler.HandleSsoAuthorization(authorizationCode);
			return;
		}

		if (_securityContext.getCallerPrincipal() == null) {
			LOG.warn("Failed to access " + ((HttpServletRequest) request).getRequestURI() + ", lack of permissions");
			((HttpServletRequest)request).getSession().invalidate();
			String contextPath = ((HttpServletRequest)request).getContextPath();
			((HttpServletResponse)response).sendRedirect(contextPath);
			return;
		}
		try {
			AWSXRay.getCurrentSegment().setUser(_securityContext.getCallerPrincipal().getName());
		} catch (SegmentNotFoundException snfe ) { //Ignore
		} catch (Exception e) {
			LOG.error("Failed to set User on current segment : " + e.getMessage(), e);
		}
        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {}

}
