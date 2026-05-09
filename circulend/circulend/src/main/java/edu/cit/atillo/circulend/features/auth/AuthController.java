package edu.cit.atillo.circulend.features.auth;

import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import edu.cit.atillo.circulend.features.auth.dto.LoginDataDTO;
import edu.cit.atillo.circulend.features.auth.dto.LoginRequestDTO;
import edu.cit.atillo.circulend.features.auth.dto.RegisterDTO;
import edu.cit.atillo.circulend.features.auth.dto.ResendVerificationRequestDTO;
import edu.cit.atillo.circulend.features.users.dto.UserResponseDTO;
import edu.cit.atillo.circulend.security.TokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<String>> resendVerification(@RequestBody ResendVerificationRequestDTO dto) {
        return ResponseEntity.ok(authService.resendVerification(dto));
    }
}
