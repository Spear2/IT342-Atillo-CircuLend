package edu.cit.atillo.circulend.features.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final Object details;

    public AuthException(String code, String message, HttpStatus status) {
        this(code, message, status, null);
    }

    public AuthException(String code, String message, HttpStatus status, Object details) {
        super(message);
        this.code = code;
        this.status = status;
        this.details = details;
    }
}
