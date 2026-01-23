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
import java.util.Arrays;


/**
 * Custom request logging filter for the *mrisk* microservice in the *MicroDiab* project.
 * This filter extends {@link OncePerRequestFilter} to ensure it is executed once per request.
 * It logs detailed information about incoming HTTP requests, including:
 * <ul>
 *   <li>Authenticated user details (username and roles)</li>
 *   <li>HTTP method and URI</li>
 *   <li>Client IP and User-Agent for unauthenticated requests</li>
 *   <li>Trusted internal requests (validated via custom headers: 'x-auth-username' and 'x-auth-roles')</li>
 * </ul>
 *
 * This filter is designed to work within the *mrisk* microservice architecture,
 * which is part of the *MicroDiab* application for diabetes analysis.
 * It integrates with Spring Security to access the current authentication context
 * and logs relevant information for monitoring, debugging, and security purposes.
 *
 * The filter also checks for trusted internal requests by validating the presence
 * of specific headers ('x-auth-username' and 'x-auth-roles') and the 'ROLE_INTERNAL' role.
 * Unauthenticated or unauthorized requests are logged as warnings for potential intrusion attempts.
 *
 * This filter is automatically registered as a Spring component
 * and integrated into the servlet filter chain.
 *
 * @see OncePerRequestFilter
 * @see Component
 * @see Authentication
 * @see SecurityContextHolder
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    /**
     * Determines whether this filter should be applied to the current request.
     * Static resources (e.g., CSS, webjars, favicon, actuator) are excluded to avoid breaking the UI.
     *
     * @param request The current {@link HttpServletRequest}.
     * @return {@code true} if the request should be excluded from filtering, {@code false} otherwise.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/webjars/") || path.startsWith("/css/") || path.equals("/favicon.ico") || path.startsWith("/actuator/");
    }

    /**
     * Processes incoming HTTP requests to log authentication and request details.
     * This method is called by the servlet container for each request.
     *
     * <p>It performs the following checks:
     * <ul>
     *   <li>Validates trusted internal requests via 'x-auth-username' and 'x-auth-roles' headers.</li>
     *   <li>Logs warnings for unauthenticated/untrusted requests, including client IP and User-Agent.</li>
     *   <li>Logs warnings for authenticated but unauthorized requests (missing 'ROLE_INTERNAL').</li>
     *   <li>Logs debug information for authorized requests.</li>
     * </ul>
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

        // Retrieves the x-auth-username and x-auth-roles headers
        String authUsername = request.getHeader("x-auth-username");
        String authRoles = request.getHeader("x-auth-roles");

        // Checks if x-auth-username = ‘username’ AND x-auth-roles contains ‘ROLE_INTERNAL’
        boolean isTrustedRequest = "username".equals(authUsername) &&
                (authRoles != null && Arrays.asList(authRoles.replaceAll("\\[|\\]", "").split(","))
                        .stream()
                        .anyMatch(role -> role.trim().equals("ROLE_INTERNAL")));

        // Retrieves Spring Security authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If the request is not authenticated AND does not originate from a trusted internal call
        if ((authentication == null || !authentication.isAuthenticated()) && !isTrustedRequest) {
            String clientIp = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            logger.warn("Unauthenticated and untrusted request from IP: {}, User-Agent: '{}', Method: {}, URI: {}{} - Possible intrusion attempt",
                    clientIp, userAgent, request.getMethod(), request.getRequestURI(), queryString);
        }
        // If the request is authenticated but does not have the INTERNAL role (neither in the authentication nor in the headers)
        else if (authentication != null && authentication.isAuthenticated()) {
            boolean hasInternalRoleInAuth = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_INTERNAL"));

            if (!hasInternalRoleInAuth && !isTrustedRequest) {
                String username = authentication.getName();
                String roles = authentication.getAuthorities().toString();
                String method = request.getMethod();
                String uri = request.getRequestURI();
                logger.warn("Authenticated but unauthorized request from user '{}' with roles [{}] - Method: {}, URI: {} - Possible intrusion attempt",
                        username, roles, method, uri);
            } else {
                logger.debug("Authorized request from user '{}' with roles [{}] - Method: {}, URI: {}",
                        authentication.getName(), authentication.getAuthorities(), request.getMethod(), request.getRequestURI());
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}

