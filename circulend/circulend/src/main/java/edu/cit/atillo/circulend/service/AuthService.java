package edu.cit.atillo.circulend.service;

import edu.cit.atillo.circulend.Util.TokenHashUtil;
import edu.cit.atillo.circulend.dto.*;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.exception.AuthException;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    private final TokenHashUtil tokenHashUtil;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider,
                       EmailService emailService,
                       TokenHashUtil tokenHashUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
        this.tokenHashUtil = tokenHashUtil;
    }

    public LoginDataDTO login(LoginRequestDTO dto) {
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException("AUTH-001", "Invalid credentials", HttpStatus.UNAUTHORIZED));
        if (user.getAuthProvider() == AuthProvider.LOCAL) {
            if (user.getPassword() == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                throw new AuthException("AUTH-001", "Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
            if (!user.isEmailVerified()) {
                throw new AuthException("AUTH-004", "Email not verified", HttpStatus.FORBIDDEN);
            }
        }
        String token = tokenProvider.createToken(user.getUserId(), user.getRole().name());
        return new LoginDataDTO(UserResponseDTO.fromUser(user), token, null);
    }

    public UserResponseDTO registration(RegisterDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new AuthException("AUTH-005", "Email already exists", HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.BORROWER);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEmailVerified(false);
        String rawToken = UUID.randomUUID().toString();
        user.setVerificationTokenHash(tokenHashUtil.sha256(rawToken));
        user.setVerificationTokenExpiresAt(java.time.LocalDateTime.now().plusMinutes(30));
        User saved = userRepo.save(user);
        String verifyUrl = frontendBaseUrl + "/verify-email?token=" + rawToken;
        emailService.sendVerificationEmail(saved.getEmail(), saved.getFirstName(), verifyUrl);
        return UserResponseDTO.fromUser(saved);
    }

    public ApiResponse<?> verifyEmail(String rawToken) {
        String hashed = tokenHashUtil.sha256(rawToken);
        User user = userRepo.findByVerificationTokenHash(hashed)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        if (user.getVerificationTokenExpiresAt() == null ||
                user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            return ApiResponse.failure("AUTH-006", "Verification token expired", null);
        }
        user.setEmailVerified(true);
        user.setVerifiedAt(LocalDateTime.now());
        user.setVerificationTokenHash(null);
        user.setVerificationTokenExpiresAt(null);
        userRepo.save(user);
        return ApiResponse.success("Email verified successfully.");
    }

    public User getUserFromToken(String token) {
        Long userId = tokenProvider.getUserIdFromToken(token);
        return userRepo.findById(userId)
                .orElseThrow(() -> new AuthException(
                        "AUTH-002",
                        "Invalid or expired token",
                        HttpStatus.UNAUTHORIZED
                ));
    }



}
