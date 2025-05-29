package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.DepartmentDTO;
import com.clinic.clinic_personnel_system.mapper.DepartmentMapper;
import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.services.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
@Tag(name = "Отделения", description = "CRUD операции для отделений клиники")
public class DepartmentController {

    private final DepartmentService service;
    private final DepartmentMapper departmentMapper;

    @Autowired
    public DepartmentController(DepartmentService service, DepartmentMapper departmentMapper) {
        this.service = service;
        this.departmentMapper = departmentMapper;
    }

    @GetMapping
    @Operation(summary = "Получить все отделения")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> list = service.getAll().stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @Operation(summary = "Добавить новое отделение")
    @ApiResponse(responseCode = "201", description = "Отделение успешно создано")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO dto) {
        Department department = departmentMapper.toEntity(dto);
        Department saved = service.save(department);
        return ResponseEntity.status(201).body(departmentMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить название отделения")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @Parameter(description = "ID отделения") @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO dto) {

        Department updated = service.update(id, dto);
        return ResponseEntity.ok(departmentMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить отделение по ID")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
