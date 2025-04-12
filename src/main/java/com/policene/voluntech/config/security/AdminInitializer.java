package com.policene.voluntech.config.security;

import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.enums.UserRole;
import com.policene.voluntech.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {

            User admin = new User();
            admin.setEmail("admin@admin.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);

        }
    }
}