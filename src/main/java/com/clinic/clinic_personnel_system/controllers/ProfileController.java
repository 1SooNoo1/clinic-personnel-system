package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.EmployeeDTO;
import com.clinic.clinic_personnel_system.mapper.EmployeeMapper;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public ProfileController(UserRepository userRepository, EmployeeMapper employeeMapper) {
        this.userRepository = userRepository;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getEmployee() == null) {
            throw new RuntimeException("Вы не являетесь сотрудником клиники");
        }

        return ResponseEntity.ok(employeeMapper.toDto(user.getEmployee()));
    }
}
