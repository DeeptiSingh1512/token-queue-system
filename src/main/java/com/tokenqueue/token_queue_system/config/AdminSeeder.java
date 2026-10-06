package com.tokenqueue.token_queue_system.config;

import com.tokenqueue.token_queue_system.entity.User;
import com.tokenqueue.token_queue_system.enums.Role;
import com.tokenqueue.token_queue_system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${admin.email:}") String adminEmail,
                       @Value("${admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.warn("admin.email or admin.password not set, so no admin was created");
            return;
        }

        String email = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User admin = User.builder()
                .name("System Admin")
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Default admin account created: {}", email);
    }
}