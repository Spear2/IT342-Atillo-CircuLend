package edu.cit.atillo.circulend.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private TokenProvider tokenProvider;

    @Test
    void setsAuthenticationWhenTokenIsValid() throws ServletException, IOException {
        JwtAuthFilter filter = new JwtAuthFilter(tokenProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        request.addHeader("Authorization", "Bearer abc123");

        when(tokenProvider.getUserIdFromToken("abc123")).thenReturn(42L);
        when(tokenProvider.getRoleFromToken("abc123")).thenReturn("admin");

        filter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(42L, auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void setsRequestErrorWhenTokenIsInvalid() throws ServletException, IOException {
        JwtAuthFilter filter = new JwtAuthFilter(tokenProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        request.addHeader("Authorization", "Bearer bad-token");

        when(tokenProvider.getUserIdFromToken("bad-token")).thenThrow(new RuntimeException("bad token"));

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("Invalid or expired token", request.getAttribute("auth_error_message"));
    }
}
