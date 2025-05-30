package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.ApplicationDTO;
import com.clinic.clinic_personnel_system.mapper.ApplicationMapper;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.models.Vacancy;
import com.clinic.clinic_personnel_system.repositories.UserRepository;
import com.clinic.clinic_personnel_system.repositories.VacancyRepository;
import com.clinic.clinic_personnel_system.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByVacancy(@PathVariable Long id) {
        List<ApplicationDTO> apps = applicationService.getByVacancy(id)
                .stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(apps);
    }
}
