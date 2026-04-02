package edu.cit.atillo.circulend.security;

import edu.cit.atillo.circulend.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new AuthException("AUTH-002", "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof Long userId) return userId;
        if (principal instanceof String s) return Long.parseLong(s);

        throw new AuthException("AUTH-002", "Invalid or expired token", HttpStatus.UNAUTHORIZED);
    }
}