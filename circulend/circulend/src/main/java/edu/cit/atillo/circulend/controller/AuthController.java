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

//    @PostMapping("/register")
//    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterDTO dto) {
//        User user = authService.registration(dto);
//        return ResponseEntity.ok(UserResponseDTO.fromUser(user));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
//        return ResponseEntity.ok(authService.login(dto));
//    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginDataDTO>> register(@RequestBody RegisterDTO dto) {
        User user = authService.registration(dto);
        String token = tokenProvider.createToken(user.getUserId(), user.getRole().name());
        LoginDataDTO data = new LoginDataDTO(UserResponseDTO.fromUser(user), token, null);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDataDTO>> login(@RequestBody LoginRequestDTO dto) {
        LoginDataDTO data = authService.login(dto);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
