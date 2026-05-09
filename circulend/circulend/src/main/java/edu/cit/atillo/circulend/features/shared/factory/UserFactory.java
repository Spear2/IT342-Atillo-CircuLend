package edu.cit.atillo.circulend.features.shared.factory;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.features.auth.dto.RegisterDTO;

import java.time.LocalDateTime;

public final class UserFactory {

    private UserFactory() {
    }

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
