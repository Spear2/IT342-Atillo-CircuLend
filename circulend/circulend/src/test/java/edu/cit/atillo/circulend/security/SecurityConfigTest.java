package edu.cit.atillo.circulend.security;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void corsConfigurationSourceBuilds() {
        SecurityConfig config = new SecurityConfig(
                mock(JwtAuthFilter.class),
                mock(RestAuthenticationEntryPoint.class),
                mock(RestAccessDeniedHandler.class),
                mock(OAuth2LoginSuccessHandler.class)
        );

        CorsConfigurationSource source = config.corsConfigurationSource();
        assertNotNull(source);
        assertNotNull(config.passwordEncoder());
    }
}
