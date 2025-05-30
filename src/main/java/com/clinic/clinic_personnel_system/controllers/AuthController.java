package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.LoginRequest;
import com.clinic.clinic_personnel_system.dto.RegisterRequest;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.repositories.EmployeeRepository;
import com.clinic.clinic_personnel_system.repositories.UserRepository;
import com.clinic.clinic_personnel_system.security.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserRepository userRepository,
                          EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> {
                    log.warn("Неудачная попытка входа: не найден пользователь с телефоном {}", request.getPhone());
                    return new RuntimeException("Неверный логин");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Неверный пароль для пользователя с телефоном {}", request.getPhone());
            return ResponseEntity.status(401).body("Неверный пароль");
        }

        String token = jwtUtil.generateToken(user.getPhone(), user.getRoles());

        log.info("Пользователь {} успешно вошел в систему", user.getPhone());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "roles", user.getRoles(),
                "fullName", user.getFullName()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            log.warn("Попытка регистрации с уже существующим телефоном: {}", request.getPhone());
            return ResponseEntity.badRequest().body("Такой телефон уже зарегистрирован");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(request.getRoles());

        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> {
                        log.warn("Регистрация пользователя на несуществующего сотрудника ID={}", request.getEmployeeId());
                        return new RuntimeException("Сотрудник не найден");
                    });
            user.setEmployee(employee);
        }

        userRepository.save(user);
        log.info("Зарегистрирован новый пользователь: {} (роль: {})", user.getPhone(), user.getRoles());

        return ResponseEntity.ok(Map.of("message", "Пользователь создан"));
    }
}
