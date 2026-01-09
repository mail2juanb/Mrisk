package com.microdiab.mrisk.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @BeforeEach
    void setUp() {
        filter = new RequestLoggingFilter();

        // Configuration du logger pour capturer les logs
        Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.DEBUG);
    }

    @Test
    void shouldLogAuthenticatedRequestWithUserInfo() throws ServletException, IOException {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john.doe");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/users");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.DEBUG);
        assertThat(logsList.getFirst().getFormattedMessage())
                .contains("john.doe")
                .contains("ROLE_USER")
                .contains("ROLE_ADMIN")
                .contains("GET")
                .contains("/api/users");
    }

    @Test
    void shouldLogUnauthenticatedRequestWhenAuthenticationIsNull() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
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
        verify(request).getRemoteAddr();
        verify(request).getHeader("User-Agent");
        verify(request).getQueryString();

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage())
                .contains("Unauthenticated request")
                .contains("192.168.1.100")
                .contains("Mozilla/5.0")
                .contains("POST")
                .contains("/api/login");
    }

    @Test
    void shouldLogUnauthenticatedRequestWhenAuthenticationIsNotAuthenticated() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
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
        verify(request).getRemoteAddr();
        verify(request).getHeader("User-Agent");
        verify(request).getQueryString();

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage())
                .contains("Unauthenticated request")
                .contains("10.0.0.5")
                .contains("curl/7.68.0");
    }

    @Test
    void shouldLogUnauthenticatedRequestWithQueryString() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
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
        verify(request).getRemoteAddr();
        verify(request).getHeader("User-Agent");
        verify(request, times(2)).getQueryString(); // appelé 2 fois : vérification null et concaténation

        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage())
                .contains("172.16.0.1")
                .contains("PostmanRuntime/7.29.0")
                .contains("/api/search?q=test&limit=10");
    }

    @Test
    void shouldHandleNullUserAgent() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
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
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage())
                .contains("127.0.0.1")
                .contains("null") // User-Agent null sera logué comme "null"
                .contains("DELETE");
    }

    @Test
    void shouldAlwaysCallFilterChain() throws ServletException, IOException {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn((Collection) List.of());
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/update");

        SecurityContextHolder.setContext(securityContext);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }
}