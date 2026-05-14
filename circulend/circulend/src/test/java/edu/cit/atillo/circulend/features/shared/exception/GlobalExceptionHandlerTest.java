package edu.cit.atillo.circulend.features.shared.exception;

import edu.cit.atillo.circulend.features.auth.AuthException;
import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAuthMapsCodeAndStatus() {
        AuthException ex = new AuthException("AUTH-001", "Invalid credentials", HttpStatus.UNAUTHORIZED);

        var response = handler.handleAuth(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AUTH-001", response.getBody().getError().getCode());
    }

    @Test
    void handleResponseStatusParsesPrefixedCode() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALID-001: Bad input");

        var response = handler.handleResponseStatus(ex);
        ApiResponse<Void> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("VALID-001", body.getError().getCode());
        assertEquals("Bad input", body.getError().getMessage());
    }
}
