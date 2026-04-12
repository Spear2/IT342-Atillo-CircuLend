package edu.cit.atillo.circulend.controller;

import edu.cit.atillo.circulend.dto.*;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.TokenProvider;
import edu.cit.atillo.circulend.service.AuthService;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    private final TokenProvider tokenProvider;



    public AuthController(AuthService authService, TokenProvider tokenProvider) {

        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.registration(dto)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDataDTO>> login(@RequestBody LoginRequestDTO dto) {
        LoginDataDTO data = authService.login(dto);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(
            @RequestBody ResendVerificationRequestDTO dto) {
        return ResponseEntity.ok(authService.resendVerification(dto));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<LoginDataDTO>> googleLogin(@RequestBody GoogleLoginRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.googleLogin(dto.getIdToken())));
    }


}
