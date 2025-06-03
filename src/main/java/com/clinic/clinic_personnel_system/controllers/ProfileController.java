package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.EmployeeDTO;
import com.clinic.clinic_personnel_system.mapper.EmployeeMapper;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.repositories.UserRepository;
import com.clinic.clinic_personnel_system.services.EmployeeService;

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
    private final EmployeeService employeeService;

    @Autowired
    public ProfileController(UserRepository userRepository, EmployeeMapper employeeMapper, EmployeeService employeeService) {
        this.userRepository = userRepository;
        this.employeeMapper = employeeMapper;
        this.employeeService = employeeService;
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

    @PutMapping("/me")
    public ResponseEntity<EmployeeDTO> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestBody EmployeeDTO updatedData) {
        User user = userRepository.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getEmployee() == null) {
            throw new RuntimeException("Вы не являетесь сотрудником клиники");
        }

        var emp = user.getEmployee();

        emp.setFullName(updatedData.getFullName());
        emp.setEmail(updatedData.getEmail());
        emp.setPhone(updatedData.getPhone());
        emp.setBirthDate(updatedData.getBirthDate());

        employeeService.save(emp);

        return ResponseEntity.ok(updatedData); 
    }

}
