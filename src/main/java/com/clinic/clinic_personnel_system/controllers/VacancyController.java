package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.EmployeeDTO;
import com.clinic.clinic_personnel_system.dto.VacancyDTO;
import com.clinic.clinic_personnel_system.mapper.EmployeeMapper;
import com.clinic.clinic_personnel_system.mapper.VacancyMapper;
import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.models.Vacancy;
import com.clinic.clinic_personnel_system.services.DepartmentService;
import com.clinic.clinic_personnel_system.services.PositionService;
import com.clinic.clinic_personnel_system.services.VacancyAnalysisService;
import com.clinic.clinic_personnel_system.services.VacancyService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VacancyController {

    private final VacancyService vacancyService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final VacancyMapper vacancyMapper;
    private final VacancyAnalysisService vacancyAnalysisService;


    @GetMapping(produces = "application/json; charset=UTF-8")
    @Operation(summary = "Получить список всех вакансий")
    public ResponseEntity<List<VacancyDTO>> getAll() {
        return ResponseEntity.ok(
                vacancyService.getAll().stream()
                        .map(vacancyMapper::toDto)
                        .collect(Collectors.toList())
        );
    }


    @GetMapping("/open")
    public ResponseEntity<List<VacancyDTO>> getOpen() {
        return ResponseEntity.ok(
                vacancyService.getOpen().stream()
                        .map(vacancyMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping
    @Operation(summary = "Создание новой вакансии")
    public ResponseEntity<VacancyDTO> create(@RequestBody VacancyDTO dto) {
        Department department = departmentService.findById(dto.getDepartmentId());
        Position position = positionService.getPositionById(dto.getPositionId());

        Vacancy vacancy = new Vacancy();
        vacancy.setDepartment(department);
        vacancy.setPosition(position);
        vacancy.setDescription(dto.getDescription());
        vacancy.setIsOpen(dto.getIsOpen() != null ? dto.getIsOpen() : true);

        Vacancy saved = vacancyService.create(vacancy);
        return ResponseEntity.status(201).body(vacancyMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить вакансию")
    public ResponseEntity<VacancyDTO> update(@PathVariable Long id,
                                            @RequestBody VacancyDTO dto) {
        Department department = departmentService.findById(dto.getDepartmentId());
        Position position = positionService.getPositionById(dto.getPositionId());

        Vacancy updatedVacancy = new Vacancy();
        updatedVacancy.setId(id);
        updatedVacancy.setDepartment(department);
        updatedVacancy.setPosition(position);
        updatedVacancy.setDescription(dto.getDescription());
        updatedVacancy.setIsOpen(dto.getIsOpen());

        Vacancy updated = vacancyService.update(id, updatedVacancy);
        return ResponseEntity.ok(vacancyMapper.toDto(updated));
    }



    @Autowired
    private VacancyAnalysisService analysisService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @GetMapping("/{id}/candidates")
    public ResponseEntity<List<EmployeeDTO>> getMatchingEmployees(@PathVariable Long id) {
        List<EmployeeDTO> result = analysisService.findMatchingEmployees(id)
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/analysis")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeDTO>> analyzeVacancy(@PathVariable Long id) {
        List<Employee> employees = vacancyAnalysisService.findMatchingEmployees(id);
        List<EmployeeDTO> dtos = employees.stream()
            .map(employeeMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}
