package edu.cit.atillo.circulend.controller;

import edu.cit.atillo.circulend.dto.ApiResponse;
import edu.cit.atillo.circulend.dto.UserResponseDTO;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.service.AuthService;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
// Add 'allowedHeaders' to your annotation
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class UserController {
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("AUTH-002", "Token missing", null));
        }
        User user = authService.getUserFromToken(token);
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromUser(user)));
    }
}
