package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.DepartmentDTO;
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

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
@Tag(name = "Отделения", description = "CRUD операции для отделений клиники")
public class DepartmentController {

    private final DepartmentService service;

    @Autowired
    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить все отделения")
    public List<Department> getAllDepartments() {
        return service.getAll();
    }

    @PostMapping
    @Operation(summary = "Добавить новое отделение")
    @ApiResponse(responseCode = "201", description = "Отделение успешно создано")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody DepartmentDTO dto) {
        Department department = new Department();
        department.setName(dto.getName());
        Department saved = service.save(department);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить название отделения")
    public ResponseEntity<Department> updateDepartment(
            @Parameter(description = "ID отделения") @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO dto) {
        Department existing = service.findById(id);
        existing.setName(dto.getName());
        Department updated = service.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить отделение по ID")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}