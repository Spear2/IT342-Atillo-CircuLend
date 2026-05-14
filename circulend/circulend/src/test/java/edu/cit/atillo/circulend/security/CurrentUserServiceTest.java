package edu.cit.atillo.circulend.security;

import edu.cit.atillo.circulend.features.auth.AuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsUserIdWhenPrincipalIsLong() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        7L, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_BORROWER"))
                )
        );

        assertEquals(7L, currentUserService.getCurrentUserId());
    }

    @Test
    void returnsUserIdWhenPrincipalIsNumericString() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "12", null, java.util.List.of(new SimpleGrantedAuthority("ROLE_BORROWER"))
                )
        );

        assertEquals(12L, currentUserService.getCurrentUserId());
    }

    @Test
    void throwsWhenAuthenticationMissing() {
        AuthException ex = assertThrows(AuthException.class, currentUserService::getCurrentUserId);
        assertEquals("AUTH-002", ex.getCode());
    }
}
