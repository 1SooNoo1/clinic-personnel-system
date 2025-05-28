package com.clinic.clinic_personnel_system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.findByPhone("+79990000001").isPresent()) {
            User user = new User();
            user.setPhone("+79990000001");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setFullName("Александр Иванов");
            user.setRoles("ROLE_ADMIN");

            userRepository.save(user);
            System.out.println("✅ Тестовый пользователь создан");
        }
    }
}