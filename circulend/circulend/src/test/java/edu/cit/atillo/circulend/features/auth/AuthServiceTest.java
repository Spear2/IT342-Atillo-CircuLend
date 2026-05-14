package edu.cit.atillo.circulend.features.auth;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.features.auth.dto.LoginDataDTO;
import edu.cit.atillo.circulend.features.auth.dto.LoginRequestDTO;
import edu.cit.atillo.circulend.features.auth.dto.RegisterDTO;
import edu.cit.atillo.circulend.features.shared.service.EmailService;
import edu.cit.atillo.circulend.features.shared.util.TokenHashUtil;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private EmailService emailService;
    @Mock
    private TokenHashUtil tokenHashUtil;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokenForVerifiedLocalUser() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("user@example.com");
        user.setPassword("hashed");
        user.setRole(Role.BORROWER);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEmailVerified(true);

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("plain");

        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);
        when(tokenProvider.createToken(1L, "BORROWER")).thenReturn("jwt-token");

        LoginDataDTO result = authService.login(dto);

        assertEquals("jwt-token", result.getAccessToken());
        assertNotNull(result.getUser());
        assertEquals("user@example.com", result.getUser().getEmail());
    }

    @Test
    void loginThrowsWhenEmailNotVerified() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hashed");
        user.setRole(Role.BORROWER);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEmailVerified(false);

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("plain");

        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);

        AuthException ex = assertThrows(AuthException.class, () -> authService.login(dto));
        assertEquals("AUTH-004", ex.getCode());
    }

    @Test
    void registrationSavesUserAndReturnsDto() {
        RegisterDTO dto = new RegisterDTO();
        dto.setFirstName("Ana");
        dto.setLastName("Atillo");
        dto.setEmail("ana@example.com");
        dto.setPassword("password123");

        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");
        when(tokenHashUtil.sha256(anyString())).thenReturn("hashed-token");
        when(emailService.sendVerificationEmail(anyString(), anyString(), anyString())).thenReturn(true);
        when(userRepo.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setUserId(33L);
            u.setRole(Role.BORROWER);
            return u;
        });
        ReflectionTestUtils.setField(authService, "frontendBaseUrl", "http://localhost:5173");

        var result = authService.registration(dto);

        assertEquals("ana@example.com", result.getEmail());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCaptor.capture());
        assertEquals(AuthProvider.LOCAL, userCaptor.getValue().getAuthProvider());
        verify(auditLogRepository).save(any());
    }
}
