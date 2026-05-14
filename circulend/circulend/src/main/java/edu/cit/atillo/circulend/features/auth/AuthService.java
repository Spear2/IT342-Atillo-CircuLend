package edu.cit.atillo.circulend.features.auth;

import edu.cit.atillo.circulend.features.shared.util.TokenHashUtil;
import edu.cit.atillo.circulend.entity.AuditLog;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuditActionType;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.features.shared.factory.UserFactory;
import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import edu.cit.atillo.circulend.features.auth.dto.LoginDataDTO;
import edu.cit.atillo.circulend.features.auth.dto.LoginRequestDTO;
import edu.cit.atillo.circulend.features.auth.dto.RegisterDTO;
import edu.cit.atillo.circulend.features.auth.dto.ResendVerificationRequestDTO;
import edu.cit.atillo.circulend.features.users.dto.UserResponseDTO;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.TokenProvider;
import edu.cit.atillo.circulend.features.shared.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    private final TokenHashUtil tokenHashUtil;
    private final AuditLogRepository auditLogRepository;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider,
                       EmailService emailService,
                       TokenHashUtil tokenHashUtil,
                       AuditLogRepository auditLogRepository) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
        this.tokenHashUtil = tokenHashUtil;
        this.auditLogRepository = auditLogRepository;
    }

    public LoginDataDTO login(LoginRequestDTO dto) {
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException("AUTH-001", "Invalid credentials", HttpStatus.UNAUTHORIZED));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new AuthException(
                    "AUTH-007",
                    "This account uses Google sign-in. Please continue with Google.",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String rawPassword = dto.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new AuthException("AUTH-001", "Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        if (user.getPassword() == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthException("AUTH-001", "Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        if (!user.isEmailVerified()) {
            throw new AuthException("AUTH-004", "Email not verified", HttpStatus.FORBIDDEN);
        }

        String token = tokenProvider.createToken(user.getUserId(), user.getRole().name());
        return new LoginDataDTO(UserResponseDTO.fromUser(user), token, null);
    }

    public UserResponseDTO registration(RegisterDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new AuthException("AUTH-005", "Email already exists", HttpStatus.CONFLICT);
        }
        String rawToken = UUID.randomUUID().toString();
        String verificationHash = tokenHashUtil.sha256(rawToken);
        LocalDateTime verificationExpires = LocalDateTime.now().plusMinutes(30);
        User user = UserFactory.newLocalUserForRegistration(
                dto,
                passwordEncoder.encode(dto.getPassword()),
                verificationHash,
                verificationExpires
        );
        User saved = userRepo.save(user);
        String verifyUrl = frontendBaseUrl + "/verify-email?token=" + rawToken;
        boolean sent = emailService.sendVerificationEmail(saved.getEmail(), saved.getFirstName(), verifyUrl);

        writeSmtpAudit(saved, sent,
                sent ? "Verification email sent for registration" : "Verification email failed for registration");

        return UserResponseDTO.fromUser(saved);
    }

    public ApiResponse<String> verifyEmail(String rawToken) {
        String hashed = tokenHashUtil.sha256(rawToken);
        User user = userRepo.findByVerificationTokenHash(hashed)
                .orElseThrow(() -> new AuthException(
                        "AUTH-006",
                        "Invalid or expired verification token",
                        HttpStatus.BAD_REQUEST
                ));
        if (user.getVerificationTokenExpiresAt() == null ||
                user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException(
                    "AUTH-006",
                    "Verification token expired",
                    HttpStatus.BAD_REQUEST
            );
        }

        user.setEmailVerified(true);
        user.setVerifiedAt(LocalDateTime.now());
        user.setVerificationTokenHash(null);
        user.setVerificationTokenExpiresAt(null);
        userRepo.save(user);

        boolean sent = emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
        writeSmtpAudit(user, sent,
                sent ? "Welcome email sent after verification" : "Welcome email failed after verification");

        return ApiResponse.success("Email verified successfully.");
    }

    public ApiResponse<String> resendVerification(ResendVerificationRequestDTO dto) {
        String generic = "If this email is registered and unverified, a verification email was sent.";

        Optional<User> userOpt = userRepo.findByEmail(dto.getEmail());
        if (userOpt.isEmpty()) {
            return ApiResponse.success(generic);
        }

        User user = userOpt.get();
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            return ApiResponse.success(generic);
        }

        if (user.isEmailVerified()) {
            return ApiResponse.success("Email is already verified.");
        }

        String rawToken = UUID.randomUUID().toString();
        String hashed = tokenHashUtil.sha256(rawToken);

        user.setVerificationTokenHash(hashed);
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusMinutes(30));
        userRepo.save(user);

        String verifyUrl = frontendBaseUrl + "/verify-email?token=" + rawToken;
        boolean sent = emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verifyUrl);
        writeSmtpAudit(user, sent,
                sent ? "Verification email resent" : "Verification email resend failed");

        return ApiResponse.success(generic);
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

    private void writeSmtpAudit(User user, boolean sent, String description) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setActionType(sent ? AuditActionType.SMTP_SENT : AuditActionType.SMTP_FAILED);
        log.setDescription(description);
        auditLogRepository.save(log);
    }
}
