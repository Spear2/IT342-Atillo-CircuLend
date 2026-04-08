package edu.cit.atillo.circulend.factory;

import edu.cit.atillo.circulend.dto.RegisterDTO;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;

import java.time.LocalDateTime;

/**
 * Factory: builds a User for LOCAL email/password registration (pre-persist).
 */
public final class UserFactory {

    private UserFactory() {
    }

    /**
     * @param verificationTokenHash SHA-256 of the raw token sent in the verify-email link
     * @param verificationTokenExpiresAt when the link expires
     */
    public static User newLocalUserForRegistration(
            RegisterDTO dto,
            String encodedPassword,
            String verificationTokenHash,
            LocalDateTime verificationTokenExpiresAt
    ) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(Role.BORROWER);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEmailVerified(false);
        user.setVerificationTokenHash(verificationTokenHash);
        user.setVerificationTokenExpiresAt(verificationTokenExpiresAt);
        return user;
    }
}