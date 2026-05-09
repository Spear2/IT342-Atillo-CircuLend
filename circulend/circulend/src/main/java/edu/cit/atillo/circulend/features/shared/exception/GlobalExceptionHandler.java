package edu.cit.atillo.circulend.features.shared.exception;

import edu.cit.atillo.circulend.features.auth.AuthException;
import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuth(AuthException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.failure(ex.getCode(), ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> details.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(ApiResponse.failure("VALID-001", "Validation failed", details));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException ex) {
        String reason = ex.getReason() != null ? ex.getReason() : "Request failed";
        String code = "REQUEST-001";
        String message = reason;

        // Optional split: "VALID-001: Asset tag mismatch"
        int idx = reason.indexOf(':');
        if (idx > 0) {
            code = reason.substring(0, idx).trim();
            message = reason.substring(idx + 1).trim();
        }

        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.failure(code, message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity.status(500)
                .body(ApiResponse.failure("SYSTEM-001", "Internal server error", null));
    }
}
