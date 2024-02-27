package com.intuit.tank.rest.mvc;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import jakarta.servlet.http.HttpSession;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDao userDao;

    private static final Logger LOG = LogManager.getLogger(AuthTokenFilter.class);

    private final AuthTokenUserDetailService userDetailService;

    @Inject
    public AuthTokenFilter(AuthTokenUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // check if user is logged in
            HttpSession session = request.getSession(false); // false prevents creating a new session if not existent

            if (session != null && session.getAttribute("user") != null) {
                // user is logged in
                String username = session.getAttribute("user").toString();
                setAuthentication(username);
            }
        } catch (Exception e) {
            LOG.error("Error authenticating user", e);
        }

        // check bearer token
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if(validateToken(token)) {
                    String username = getUsernameFromToken(token);
                    if(username != null) {
                        setAuthentication(username);
                    }
                }
            } catch (UsernameNotFoundException unfe) {
                    LOG.error("User not found", unfe);
            } catch (Exception e) {
                LOG.error("Error authenticating user", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String username) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean validateToken(String token) {
        User user = userDao.findByApiToken(token);
        return user != null;
    }

    private String getUsernameFromToken(String token){
        User user = userDao.findByApiToken(token);
        if(user != null){
            return user.getName();
        }

        return null;
    }

}
