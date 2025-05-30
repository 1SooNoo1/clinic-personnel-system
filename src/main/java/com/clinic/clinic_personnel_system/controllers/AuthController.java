package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.LoginRequest;
import com.clinic.clinic_personnel_system.dto.RegisterRequest;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.repositories.EmployeeRepository;
import com.clinic.clinic_personnel_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository,
                          EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return ResponseEntity.badRequest().body("Такой телефон уже зарегистрирован");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(request.getRoles());

        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));
            user.setEmployee(employee);
        }

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Пользователь создан"));
    }
}
