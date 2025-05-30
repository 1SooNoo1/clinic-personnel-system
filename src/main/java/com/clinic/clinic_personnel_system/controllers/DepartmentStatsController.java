package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.DepartmentStatsDTO;
import com.clinic.clinic_personnel_system.services.DepartmentStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DepartmentStatsController {

    private final DepartmentStatsService statsService;

    @GetMapping("/department")
    public ResponseEntity<DepartmentStatsDTO> getDepartmentStats(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        DepartmentStatsDTO stats = statsService.getStatsForDepartment(departmentId, from, to);
        return ResponseEntity.ok(stats);
    }
}
