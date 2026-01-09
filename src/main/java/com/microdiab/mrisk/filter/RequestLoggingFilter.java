package com.microdiab.mrisk.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Custom request logging filter for the *mrisk* microservice in the *MicroDiab* project.
 * This filter extends {@link OncePerRequestFilter} to ensure it is executed once per request.
 * It logs detailed information about incoming HTTP requests, including:
 * <ul>
 *   <li>Authenticated user details (username and roles)</li>
 *   <li>HTTP method and URI</li>
 *   <li>Client IP and User-Agent for unauthenticated requests</li>
 * </ul>
 *
 * This filter is designed to work within the *mnotes* microservice architecture,
 * which is part of the *MicroDiab* application for diabetes analysis.
 * It integrates with Spring Security to access the current authentication context
 * and logs relevant information for monitoring and debugging purposes.
 *
 * This filter is automatically registered as a Spring component
 * and integrated into the servlet filter chain.
 *
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see org.springframework.stereotype.Component
 * @see org.springframework.security.core.Authentication
 * @see org.springframework.security.core.context.SecurityContextHolder
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);


    /**
     * Processes incoming HTTP requests to log authentication and request details.
     * This method is called by the servlet container for each request.
     *
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @param filterChain The filter chain for invoking the next filter or servlet.
     * @throws ServletException If a servlet-related error occurs.
     * @throws IOException      If an I/O error occurs during processing.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Récupérer l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String roles = authentication.getAuthorities().toString();
            String method = request.getMethod();
            String uri = request.getRequestURI();

            logger.debug("User '{}' with roles [{}] accessed {} {}", username, roles, method, uri);
        } else {
            // Log pour les requêtes non authentifiées
            String clientIp = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            logger.warn("Unauthenticated request from IP: {}, User-Agent: '{}', Method: {}, URI: {}{}",
                    clientIp, userAgent, request.getMethod(), request.getRequestURI(), queryString);
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
