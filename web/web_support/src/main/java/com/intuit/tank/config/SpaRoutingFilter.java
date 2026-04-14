package com.intuit.tank.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Forwards all /app/* requests that are not static assets to /app/index.html,
 * enabling React Router (BrowserRouter with basename="/app") to handle deep links.
 */
@WebFilter(urlPatterns = {"/app/*"}, asyncSupported = true)
public class SpaRoutingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length()); // strip context path

        // Pass through static assets and the entry point itself
        if (path.startsWith("/app/assets/")
                || path.equals("/app/index.html")
                || path.equals("/app/favicon.png")
                || path.matches("/app/.*\\.(js|css|png|ico|svg|woff2?|map|json|txt)")) {
            chain.doFilter(request, response);
            return;
        }

        // All other /app/* paths → forward to SPA entry point
        request.getRequestDispatcher("/app/index.html").forward(request, response);
    }
}
