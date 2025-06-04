package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.ApplicationDTO;
import com.clinic.clinic_personnel_system.mapper.ApplicationMapper;
import com.clinic.clinic_personnel_system.models.*;
import com.clinic.clinic_personnel_system.models.enums.ApplicationStatus;
import com.clinic.clinic_personnel_system.repositories.UserRepository;
import com.clinic.clinic_personnel_system.repositories.VacancyRepository;
import com.clinic.clinic_personnel_system.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;
    private final UserRepository userRepository;
    private final VacancyRepository vacancyRepository;
    private final EmployeeService employeeService;

    @PostMapping("/apply")
    public ResponseEntity<ApplicationDTO> apply(@RequestParam Long vacancyId,
                                                @RequestParam(required = false) String message,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new RuntimeException("Вакансия не найдена"));

        var application = applicationService.apply(user, vacancy, message);
        return ResponseEntity.ok(applicationMapper.toDto(application));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<ApplicationDTO> apps = applicationService.getByUser(user.getId())
                .stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(apps);
    }

    @GetMapping("/vacancy/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByVacancy(@PathVariable Long id) {
        List<ApplicationDTO> apps = applicationService.getByVacancy(id)
                .stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(apps);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<String> acceptApplication(@PathVariable Long id) {
        Application app = applicationService.getById(id)
                .orElseThrow(() -> new RuntimeException("Отклик не найден"));
        if (app.getStatus() == ApplicationStatus.ACCEPTED) {
            throw new RuntimeException("Кандидат уже принят");
        }
        User user = app.getUser();
        if (user.getEmployee() != null) {
            throw new RuntimeException("Пользователь уже является сотрудником");
        }

        Employee emp = new Employee();
        emp.setFullName(user.getFullName());
        emp.setEmail(user.getPhone() + "@clinic.local");
        emp.setPhone(user.getPhone());
        emp.setDepartment(app.getVacancy().getDepartment());
        emp.setPosition(app.getVacancy().getPosition());
        emp.setEmploymentDate(LocalDate.now());
        emp.setActive(true);

        employeeService.saveEmployee(emp);

        user.setEmployee(emp);
        user.setRoles("EMPLOYEE");
        userRepository.save(user);

        app.setStatus(ApplicationStatus.ACCEPTED);
        applicationService.save(app);

        return ResponseEntity.ok("Кандидат успешно принят и добавлен в сотрудники");
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<String> rejectApplication(@PathVariable Long id) {
        Application app = applicationService.getById(id)
            .orElseThrow(() -> new RuntimeException("Отклик не найден"));
        app.setStatus(ApplicationStatus.REJECTED);
        applicationService.save(app);
        return ResponseEntity.ok("Отклик отклонён");
    }
}
