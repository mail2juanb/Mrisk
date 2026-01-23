package com.microdiab.mrisk.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private RequestLoggingFilter filter;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        filter = new RequestLoggingFilter();

        // Configuring the logger to capture logs
        Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.DEBUG);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        if (logger != null && listAppender != null) {
            logger.detachAppender(listAppender);
        }
    }

    // ========== Tests shouldNotFilter ==========

    @Test
    void shouldNotFilter_ExcludesWebjarsPath() throws ServletException {
        when(request.getRequestURI()).thenReturn("/webjars/bootstrap/css/bootstrap.min.css");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_ExcludesCssPath() throws ServletException {
        when(request.getRequestURI()).thenReturn("/css/styles.css");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_ExcludesFavicon() throws ServletException {
        when(request.getRequestURI()).thenReturn("/favicon.ico");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_ExcludesActuatorPath() throws ServletException {
        when(request.getRequestURI()).thenReturn("/actuator/health");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_IncludesApiPath() throws ServletException {
        when(request.getRequestURI()).thenReturn("/api/users");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    // ========== Tests requêtes non authentifiées et non de confiance ==========

    @Test
    void shouldLogWarning_UnauthenticatedRequest_NoAuthentication() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/login");
        when(request.getQueryString()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("Unauthenticated and untrusted request")
                .contains("192.168.1.100")
                .contains("Mozilla/5.0")
                .contains("POST")
                .contains("/api/login")
                .contains("Possible intrusion attempt");
    }

    @Test
    void shouldLogWarning_UnauthenticatedRequest_AuthenticationNotAuthenticated() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("10.0.0.5");
        when(request.getHeader("User-Agent")).thenReturn("curl/7.68.0");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/health");
        when(request.getQueryString()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("Unauthenticated and untrusted request")
                .contains("10.0.0.5")
                .contains("curl/7.68.0");
    }

    @Test
    void shouldLogWarning_WithQueryString() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("172.16.0.1");
        when(request.getHeader("User-Agent")).thenReturn("PostmanRuntime/7.29.0");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/search");
        when(request.getQueryString()).thenReturn("q=test&limit=10");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("172.16.0.1")
                .contains("PostmanRuntime/7.29.0")
                .contains("/api/search?q=test&limit=10");
    }

    @Test
    void shouldHandleNullUserAgent() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn(null);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/resource/123");
        when(request.getQueryString()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("127.0.0.1")
                .contains("null");
    }

    // ========== Tests requêtes de confiance via headers ==========

    @Test
    void shouldNotLogWarning_TrustedInternalRequest_ValidHeaders() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn("[ROLE_INTERNAL, ROLE_USER]");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).isEmpty(); // Pas de log car requête de confiance
    }

    @Test
    void shouldNotLogWarning_TrustedInternalRequest_RolesWithoutBrackets() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn("ROLE_INTERNAL,ROLE_USER");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).isEmpty();
    }

    @Test
    void shouldLogWarning_InvalidUsername_WithRoleInternal() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn("wronguser");
        when(request.getHeader("x-auth-roles")).thenReturn("[ROLE_INTERNAL]");
        when(request.getRemoteAddr()).thenReturn("192.168.1.50");
        when(request.getHeader("User-Agent")).thenReturn("TestAgent");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
    }

    @Test
    void shouldLogWarning_ValidUsername_WithoutRoleInternal() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn("[ROLE_USER, ROLE_ADMIN]");
        when(request.getRemoteAddr()).thenReturn("192.168.1.60");
        when(request.getHeader("User-Agent")).thenReturn("Chrome");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/endpoint");
        when(request.getQueryString()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
    }

    @Test
    void shouldLogWarning_NullAuthRoles() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.70");
        when(request.getHeader("User-Agent")).thenReturn("Safari");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/resource");
        when(request.getQueryString()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
    }

    // ========== Tests requêtes authentifiées mais non autorisées ==========

    @Test
    void shouldLogWarning_AuthenticatedWithoutRoleInternal() throws ServletException, IOException {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john.doe");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/users");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("Authenticated but unauthorized request")
                .contains("john.doe")
                .contains("ROLE_USER")
                .contains("ROLE_ADMIN")
                .contains("GET")
                .contains("/api/users")
                .contains("Possible intrusion attempt");
    }

    @Test
    void shouldLogWarning_AuthenticatedWithoutRoleInternal_NoAuthorities() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("guest");
        when(authentication.getAuthorities()).thenReturn((Collection) List.of());
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/create");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("Authenticated but unauthorized request")
                .contains("guest");
    }

    // ========== Tests requêtes autorisées ==========

    @Test
    void shouldLogDebug_AuthenticatedWithRoleInternal_InAuthentication() throws ServletException, IOException {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_INTERNAL"),
                new SimpleGrantedAuthority("ROLE_USER")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("internal.user");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/internal/data");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("Authorized request")
                .contains("internal.user")
                .contains("ROLE_INTERNAL")
                .contains("GET")
                .contains("/api/internal/data");
    }

    @Test
    void shouldLogDebug_AuthenticatedWithRoleInternal_InHeaders() throws ServletException, IOException {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("api.user");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn("[ROLE_INTERNAL]");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/process");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("Authorized request")
                .contains("api.user");
    }

    @Test
    void shouldLogDebug_AuthenticatedWithRoleInternal_BothAuthAndHeaders() throws ServletException, IOException {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_INTERNAL")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("super.user");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn("[ROLE_INTERNAL, ROLE_ADMIN]");
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/update");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.DEBUG);
    }

    // ========== Tests edge cases ==========

    @Test
    void shouldNotEnterAuthenticatedBlock_WhenAuthenticationExistsButNotAuthenticated() throws ServletException, IOException {
        // This test covers the case where authentication != null is TRUE
        // but authentication.isAuthenticated() is FALSE in the else-if block
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn("[ROLE_INTERNAL]");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).isEmpty(); // No logs because request is trusted via headers

        // Verify we never accessed getName() or getAuthorities() since authentication.isAuthenticated() was false
        verify(authentication, never()).getName();
        verify(authentication, never()).getAuthorities();
    }

    @Test
    void shouldAlwaysCallFilterChain_RegardlessOfAuthentication() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn(null);
        when(request.getHeader("x-auth-roles")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        when(request.getHeader("User-Agent")).thenReturn("Bot");
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getRequestURI()).thenReturn("/api/preflight");
        when(request.getQueryString()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldHandleRolesWithSpaces() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("x-auth-username")).thenReturn("username");
        when(request.getHeader("x-auth-roles")).thenReturn("[ ROLE_INTERNAL , ROLE_USER ]");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).isEmpty(); // Trim() should handle spaces
    }
}