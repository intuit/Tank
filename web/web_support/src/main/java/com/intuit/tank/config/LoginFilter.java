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
import com.intuit.tank.vm.common.ThreadLocalUsernameProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.picketlink.Identity;


/**
 * LoginFilter
 * 
 * @author Kevin McGoldrick
 * 
 */
public class LoginFilter implements Filter {
	private static Logger LOG = LogManager.getLogger(LoginFilter.class);
	
    @Inject
    private Identity identity;

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!identity.isLoggedIn()) {
			((HttpServletRequest)request).getSession().invalidate();
			String contextPath = ((HttpServletRequest)request).getContextPath();
			((HttpServletResponse)response).sendRedirect(contextPath + "/denied.xhtml");
		}
		try {
			AWSXRay.getCurrentSegment().setUser(ThreadLocalUsernameProvider.getUsernameProvider().getUserName());
		} catch (SegmentNotFoundException snfe ) { //Ignore
		} catch (Exception e) {
			LOG.error("Failed to set User on current segment : " + e.getMessage(), e);
		}
        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {}

}
