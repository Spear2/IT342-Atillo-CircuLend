package edu.cit.atillo.circulend.config;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@circulend.local}")
    private String adminEmail;

    @Value("${app.admin.password:ChangeMe123!}")
    private String adminPassword;

    @Value("${app.admin.first-name:System}")
    private String adminFirstName;

    @Value("${app.admin.last-name:Admin}")
    private String adminLastName;

    @Bean
    CommandLineRunner seedAdminUser() {
        return args -> {
            userRepository.findByEmail(adminEmail).ifPresentOrElse(existing -> {
                if (existing.getRole() != Role.ADMIN) {
                    existing.setRole(Role.ADMIN);
                    existing.setAuthProvider(AuthProvider.LOCAL);
                    existing.setEmailVerified(true);
                    if (existing.getPassword() == null || existing.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(adminPassword));
                    }
                    userRepository.save(existing);
                    log.info("Updated existing user to ADMIN: {}", adminEmail);
                } else {
                    log.info("Admin already exists: {}", adminEmail);
                }
            }, () -> {
                User admin = new User();
                admin.setFirstName(adminFirstName);
                admin.setLastName(adminLastName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                admin.setAuthProvider(AuthProvider.LOCAL);
                admin.setEmailVerified(true);
                userRepository.save(admin);
                log.info("Seeded default ADMIN user: {}", adminEmail);
            });
        };
    }
}