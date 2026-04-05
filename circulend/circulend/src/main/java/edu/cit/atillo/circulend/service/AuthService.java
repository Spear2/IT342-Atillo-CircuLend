package edu.cit.atillo.circulend.service;

import edu.cit.atillo.circulend.Util.TokenHashUtil;
import edu.cit.atillo.circulend.dto.*;
import edu.cit.atillo.circulend.entity.AuditLog;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuditActionType;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.exception.AuthException;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
        // Generic response to avoid email enumeration
        String generic = "If this email is registered and unverified, a verification email was sent.";

        Optional<User> userOpt = userRepo.findByEmail(dto.getEmail());
        if (userOpt.isEmpty()) {
            return ApiResponse.success(generic);
        }

        User user = userOpt.get();

        // Only LOCAL accounts need email verification
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
