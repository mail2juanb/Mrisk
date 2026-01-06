package com.microdiab.mrisk.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void shouldLoadSecurityConfiguration() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBean("filterChain")).isNotNull();
    }

    @Test
    void shouldCreateUserDetailsServiceBean() {
        assertThat(userDetailsService).isNotNull();
    }

    @Test
    void shouldAllowAccessToActuatorWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessToProtectedEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessWithValidCredentials() throws Exception {
        mockMvc.perform(get("/api/test")
                        .with(httpBasic("username", "user")))
                .andExpect(status().isNotFound()); // 404 car l'endpoint n'existe pas, mais l'auth passe
    }

    @Test
    void shouldDenyAccessWithInvalidCredentials() throws Exception {
        mockMvc.perform(get("/api/test")
                        .with(httpBasic("username", "wrongpassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDenyAccessWithInvalidUsername() throws Exception {
        mockMvc.perform(get("/api/test")
                        .with(httpBasic("wronguser", "user")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INTERNAL")
    void shouldAllowAccessWithInternalRole() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isNotFound()); // 404 car l'endpoint n'existe pas, mais l'auth passe
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyAccessWithoutInternalRole() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldLoadUserFromUserDetailsService() {
        var user = userDetailsService.loadUserByUsername("username");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getAuthorities())
                .extracting("authority")
                .contains("ROLE_INTERNAL");
    }
}