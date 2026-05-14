package edu.cit.atillo.circulend.features.shared.factory;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.features.auth.dto.RegisterDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void newLocalUserForRegistrationBuildsExpectedDefaults() {
        RegisterDTO dto = new RegisterDTO();
        dto.setFirstName("Ana");
        dto.setLastName("Atillo");
        dto.setEmail("ana@example.com");
        dto.setPassword("password123");

        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        User user = UserFactory.newLocalUserForRegistration(
                dto,
                "encoded-pass",
                "hash-123",
                expiry
        );

        assertEquals("Ana", user.getFirstName());
        assertEquals("Atillo", user.getLastName());
        assertEquals("ana@example.com", user.getEmail());
        assertEquals("encoded-pass", user.getPassword());
        assertEquals(Role.BORROWER, user.getRole());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());
        assertFalse(user.isEmailVerified());
        assertEquals("hash-123", user.getVerificationTokenHash());
        assertEquals(expiry, user.getVerificationTokenExpiresAt());
    }
}
