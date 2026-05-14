package edu.cit.atillo.circulend.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {

    private static final String SECRET_32_PLUS = "this-is-a-very-long-secret-key-12345";

    @Test
    void createAndParseTokenWorks() {
        TokenProvider provider = new TokenProvider(SECRET_32_PLUS);

        String token = provider.createToken(99L, "ADMIN");

        assertNotNull(token);
        assertEquals(99L, provider.getUserIdFromToken(token));
        assertEquals("ADMIN", provider.getRoleFromToken(token));
    }

    @Test
    void createTokenDefaultsRoleWhenNull() {
        TokenProvider provider = new TokenProvider(SECRET_32_PLUS);

        String token = provider.createToken(10L, null);

        assertEquals("BORROWER", provider.getRoleFromToken(token));
    }

    @Test
    void shortSecretThrows() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new TokenProvider("short-secret")
        );
        assertTrue(ex.getMessage().contains("at least 32 characters"));
    }
}
