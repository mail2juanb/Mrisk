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

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

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

            logger.info("User '{}' with roles [{}] accessed {} {}", username, roles, method, uri);
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
