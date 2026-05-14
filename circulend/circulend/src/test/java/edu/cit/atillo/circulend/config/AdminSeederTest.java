package edu.cit.atillo.circulend.config;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminSeeder adminSeeder;

    @Test
    void createsAdminWhenMissing() throws Exception {
        ReflectionTestUtils.setField(adminSeeder, "adminEmail", "admin@example.com");
        ReflectionTestUtils.setField(adminSeeder, "adminPassword", "adminpass123");
        ReflectionTestUtils.setField(adminSeeder, "adminFirstName", "System");
        ReflectionTestUtils.setField(adminSeeder, "adminLastName", "Admin");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("adminpass123")).thenReturn("encoded-pass");

        CommandLineRunner runner = adminSeeder.seedAdminUser();
        runner.run();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void upgradesExistingUserToAdmin() throws Exception {
        ReflectionTestUtils.setField(adminSeeder, "adminEmail", "admin@example.com");
        ReflectionTestUtils.setField(adminSeeder, "adminPassword", "adminpass123");
        User existing = new User();
        existing.setEmail("admin@example.com");
        existing.setRole(Role.BORROWER);
        existing.setPassword("hashed");

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(existing));

        CommandLineRunner runner = adminSeeder.seedAdminUser();
        runner.run();

        verify(userRepository).save(existing);
    }
}
